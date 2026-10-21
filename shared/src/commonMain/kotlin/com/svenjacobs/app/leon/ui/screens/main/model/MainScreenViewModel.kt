/*
 * Léon - The URL Cleaner
 * Copyright (C) 2026 Sven Jacobs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.svenjacobs.app.leon.ui.screens.main.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.core.domain.Cleaner
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.core.domain.change.Change
import com.svenjacobs.app.leon.core.domain.preference.SitePreferences
import com.svenjacobs.app.leon.core.domain.preference.site
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.shortlink.ShortLinkResolver
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.db.HistoryDao
import com.svenjacobs.app.leon.preference.SitePreferenceStore
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.ChangeRow
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkCommand
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkProgress
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkState
import com.svenjacobs.app.leon.ui.shortlink.model.isSettled
import com.svenjacobs.app.leon.ui.shortlink.model.resolveInteractively
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class MainScreenViewModel(
    private val appDataStoreManager: AppDataStoreManager,
    private val cleaner: Cleaner,
    private val historyDao: HistoryDao,
    private val shortLinkResolver: ShortLinkResolver,
    private val sitePreferenceStore: SitePreferenceStore,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val inputId: String? = null,
        val isUrlDecodeEnabled: Boolean = false,
        val isExtractUrlEnabled: Boolean = false,
        val isCustomTabsEnabled: Boolean = false,
        val result: Result = Result.Empty,
        val actionAfterClean: ActionAfterClean = ActionAfterClean.DoNothing,
        /**
         * The epoch-millis instant at which the screen must reset itself, or `null` when auto-reset
         * is off or the deadline is not known yet.
         */
        val autoResetAt: Long? = null,
        /**
         * Where resolving the short links of the input stands. [result] is always the input cleaned
         * with the targets known so far — none at all while the first attempt is still running, has
         * failed, or resolving is turned off — so that the offline result shows underneath a dialog
         * or hint.
         */
        val shortLink: ShortLinkState = ShortLinkState.Idle,
    ) {
        sealed interface Result {

            data object Empty : Result

            data class Success(
                val originalText: String,
                val cleanedText: String,
                val urls: ImmutableList<String>,
                val changes: ImmutableList<ChangeRow> = persistentListOf(),
            ) : Result

            data object Error : Result
        }

        /**
         * One row of the change list: something the sanitizers proposed, or a parameter of the
         * cleaned URL which nothing proposed to remove and which the user may remove by hand.
         *
         * Several sanitizers routinely propose the very same removal — `utm_source` is matched by
         * Google Analytics and by Salesforce alike — and they share one row, because declining one
         * of them while the others still remove the parameter would be a click without an effect.
         *
         * The names of the sanitizers are not part of this: they are translated resources, which
         * the view resolves from [sanitizerIds].
         *
         * @param sanitizerIds Every sanitizer of the catalog which proposed [action]; empty when
         *   the user asked for it themselves, on this share or on an earlier one from [site].
         * @param applied Whether the change is currently part of the cleaned URL.
         * @param site The site toggling this row is remembered for, or `null` when it is not
         *   remembered — because it is not a parameter, or because remembering is turned off.
         * @param remembered Whether [applied] follows a choice remembered for [site] rather than
         *   the catalog.
         */
        data class ChangeRow(
            val action: Change.Action,
            val sanitizerIds: ImmutableList<SanitizerId>,
            val applied: Boolean,
            val site: String? = null,
            val remembered: Boolean = false,
        )
    }

    /** Text of a single incoming intent, identified by a stable [id]. */
    private data class Input(val id: String, val text: String)

    /**
     * What the user picked in the change list. Transient: it is reset whenever a new text arrives,
     * because the changes of the previous URL say nothing about the new one. A parameter toggled
     * while remembering per site is turned on goes to the [SitePreferenceStore] instead.
     *
     * @param declined Changes a sanitizer proposed which the user unchecked.
     * @param additional Removals the user checked which no sanitizer proposed.
     */
    private data class Selection(
        val declined: Set<Change> = emptySet(),
        val additional: Set<Change> = emptySet(),
    )

    private val input = MutableStateFlow<Input?>(null)

    private val selection = MutableStateFlow(Selection())

    /** Id of the input for which the action after clean has already been performed. */
    private var handledActionInputId: String? = null

    /** The (inputId, url) pair last recorded, so an unchanged one is not written again. */
    private var recorded: Pair<String, String>? = null

    /**
     * Records [url], cleaned from the input [id], to the history.
     *
     * The outer [combine] re-emits on every DataStore tick and again on resubscribe after a
     * configuration change, even though nothing about the cleaned URL actually changed — so this is
     * called far more often than the URL itself changes. [recorded] guards against turning those
     * re-emissions into duplicate or reverted history rows.
     */
    private fun record(id: String, url: String) {
        if (recorded == (id to url)) return
        recorded = id to url
        viewModelScope.launch {
            if (!appDataStoreManager.historyEnabled.first()) return@launch
            historyDao.record(id = id, url = url, at = Clock.System.now().toEpochMilliseconds())
        }
    }

    /**
     * What the user can do about the short links being resolved, or which failed to resolve.
     * Conflated, because only the latest tap counts; [resolveInteractively] discards one which
     * arrives before the dialog it belongs to.
     */
    private val shortLinkCommands = Channel<ShortLinkCommand>(Channel.CONFLATED)

    /** An [input] with its short link progress, so a new input never gets the previous state. */
    private data class Staged(
        val input: Input?,
        val shortLinks: ShortLinkProgress = ShortLinkProgress(),
    )

    /**
     * The last [Staged] reported, from which resolving resumes when [staged] restarts for the same
     * input. Resolved targets are kept, but requests still in flight were aborted and are sent
     * again.
     */
    private var lastStaged: Staged? = null

    /**
     * Resolves the short links of each input once, ahead of cleaning: cleaning runs again on every
     * change the user toggles, and that must never lead to another request. Only a new input, or
     * the setting being switched, starts over — and switching it on is exactly what the hint's
     * "Enable" button does, so the short link at hand is resolved straight away.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val staged: Flow<Staged> =
        combine(
                input,
                // A data store emits on every write to any of its keys; only a change of this one
                // may restart resolving.
                appDataStoreManager.resolveShortLinksEnabled.distinctUntilChanged(),
                ::Pair,
            )
            .transformLatest { (input, enabled) ->
                suspend fun report(progress: ShortLinkProgress) {
                    val staged = Staged(input, progress)
                    lastStaged = staged
                    emit(staged)
                }

                val urls = input?.let { cleaner.shortLinks(it.text) }.orEmpty()
                when {
                    input == null || urls.isEmpty() -> report(ShortLinkProgress())
                    !enabled -> report(ShortLinkProgress(ShortLinkState.Disabled))
                    else ->
                        shortLinkResolver.resolveInteractively(
                            urls = urls,
                            commands = shortLinkCommands,
                            resume = lastStaged?.takeIf { it.input == input }?.shortLinks,
                            onProgress = ::report,
                        )
                }
            }

    /** The choices remembered per site, or `null` when remembering is turned off. */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val remembered: Flow<SitePreferences?> =
        appDataStoreManager.rememberPerSiteEnabled.distinctUntilChanged().flatMapLatest { enabled ->
            if (enabled) sitePreferenceStore.preferences else flowOf(null)
        }

    /**
     * The input with its short links, the user's selection, the choices remembered per site and the
     * auto-reset deadline for that same input, combined ahead of the outer [combine] so that it
     * stays within its five-flow limit.
     */
    private data class Session(
        val input: Input?,
        val shortLinks: ShortLinkProgress,
        val selection: Selection,
        val remembered: SitePreferences?,
        val autoResetAt: Long?,
    )

    val uiState =
        combine(
                combine(
                    staged,
                    selection,
                    remembered,
                    appDataStoreManager.autoReset,
                    appDataStoreManager.lastInput,
                ) { (input, shortLinks), selection, remembered, autoReset, last ->
                    // The deadline is a wall-clock timestamp, not a countdown, so it survives the
                    // app being backgrounded, the device sleeping, or the process being killed and
                    // the share intent redelivered. It only applies to `last` when `last` really
                    // belongs to the current input — a redelivered intent carries the same id, and
                    // must not be treated as if it arrived just now. A fresh input whose timestamp
                    // write has not landed in the data store yet has no deadline for a moment,
                    // which is harmless: it simply cannot be expired until it has one.
                    val autoResetAt =
                        autoReset
                            ?.minutes
                            ?.takeIf { last != null && last.id == input?.id }
                            ?.let { last!!.at + it * 60_000L }
                    Session(input, shortLinks, selection, remembered, autoResetAt)
                },
                appDataStoreManager.urlDecodeEnabled,
                appDataStoreManager.extractUrlEnabled,
                appDataStoreManager.customTabsEnabled,
                appDataStoreManager.actionAfterClean,
            ) { session, urlDecodeEnabled, extractUrlEnabled, isCustomTabsEnabled, actionAfterClean
                ->
                // An input whose deadline already passed is the process-death case: the intent was
                // redelivered, but auto-reset should already have fired. Treating it as absent here
                // avoids a flash of the stale URL while the screen catches up.
                val expired =
                    session.autoResetAt != null &&
                        Clock.System.now().toEpochMilliseconds() >= session.autoResetAt
                val input = if (expired) null else session.input
                val shortLink = if (input == null) ShortLinkState.Idle else session.shortLinks.state

                val result =
                    input?.let {
                        clean(
                            text = it.text,
                            decodeUrl = urlDecodeEnabled,
                            extractUrl = extractUrlEnabled,
                            selection = session.selection,
                            resolved = session.shortLinks.resolved,
                            remembered = session.remembered,
                        )
                    } ?: Result.Empty

                // While a short link is being resolved, or its failure awaits a decision, the
                // result is not final yet; recording it would put the short link into the history
                // only to replace it a moment later.
                if (
                    input != null &&
                        shortLink.isSettled &&
                        result is Result.Success &&
                        result.urls.isNotEmpty()
                ) {
                    record(input.id, result.urls.first())
                }

                UiState(
                    isLoading = input == null,
                    inputId = input?.id,
                    isUrlDecodeEnabled = urlDecodeEnabled,
                    isExtractUrlEnabled = extractUrlEnabled,
                    isCustomTabsEnabled = isCustomTabsEnabled,
                    result = result,
                    actionAfterClean = actionAfterClean ?: ActionAfterClean.DoNothing,
                    autoResetAt = if (expired) null else session.autoResetAt,
                    shortLink = shortLink,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState(),
            )

    fun setText(text: String?, id: String = Uuid.random().toString()) {
        if (text == null && uiState.value.result is Result.Success) return
        selection.value = Selection()
        input.value = text?.let { Input(id = id, text = it) }

        if (text != null) {
            viewModelScope.launch {
                // A redelivered intent (configuration change, or the activity being recreated
                // after process death) carries the same id and must not restart the clock.
                if (appDataStoreManager.lastInput.first()?.id != id) {
                    appDataStoreManager.setLastInput(id, Clock.System.now().toEpochMilliseconds())
                }
            }
        }
    }

    fun onResetClick() {
        selection.value = Selection()
        input.value = null
    }

    /**
     * Applies or reverts one row of the change list, which means every sanitizer's proposal behind
     * it — a row is only checked off the cleaned URL once nothing removes the value any more.
     *
     * A parameter with a [site][ChangeRow.site] is remembered for that site instead, by its name.
     * Only a deviation from the catalog is stored: toggling a row back to what the sanitizers do on
     * their own forgets it again.
     */
    fun onChangeToggled(row: ChangeRow, apply: Boolean) {
        val site = row.site
        val parameter = (row.action as? Change.Action.RemoveParameter)?.parameter?.name
        if (site != null && parameter != null) {
            viewModelScope.launch {
                when {
                    apply == row.sanitizerIds.isNotEmpty() ->
                        sitePreferenceStore.forget(site, parameter)
                    apply -> sitePreferenceStore.remove(site, parameter)
                    else -> sitePreferenceStore.keep(site, parameter)
                }
            }
            return
        }

        selection.update { current ->
            when {
                // A change the user added themselves only exists while it is applied.
                row.sanitizerIds.isEmpty() -> {
                    val change = Change(null, row.action)
                    current.copy(
                        additional =
                            if (apply) current.additional + change else current.additional - change,
                    )
                }
                else -> {
                    val changes = row.sanitizerIds.map { Change(it, row.action) }
                    current.copy(
                        declined =
                            if (apply) {
                                current.declined - changes.toSet()
                            } else {
                                current.declined + changes
                            },
                    )
                }
            }
        }
    }

    /**
     * Returns `true` if the action after clean configured by the user still needs to be performed
     * for [inputId] and marks it as handled. Returns `false` if it was already performed for this
     * exact input, which happens when the UI recomposes or the activity is recreated (e.g. on a
     * configuration change) without a genuinely new input being submitted.
     *
     * The caller only asks once [UiState.shortLink] [is settled][isSettled], so that the action is
     * never performed with a short link that is still being resolved or whose failure awaits a
     * decision. [ShortLinkState.Disabled] is settled: the user turned resolving off, so the short
     * link cleaned as it is *is* the result. Enabling resolving from the hint afterwards does not
     * perform the action a second time.
     */
    fun consumeActionAfterClean(inputId: String): Boolean {
        if (inputId == handledActionInputId) return false
        handledActionInputId = inputId
        return true
    }

    /** Stops resolving, and cleans the short links which are not resolved yet as they are. */
    fun cancelShortLinkResolving() {
        shortLinkCommands.trySend(ShortLinkCommand.Cancel)
    }

    /** Resolves the short links which failed once more, keeping those which succeeded. */
    fun retryShortLinkResolving() {
        shortLinkCommands.trySend(ShortLinkCommand.Retry)
    }

    /** Gives up on the short links which failed, and cleans them as they are. */
    fun continueWithoutResolving() {
        shortLinkCommands.trySend(ShortLinkCommand.Continue)
    }

    /** Turns resolving short links on, which resolves those of the current input straight away. */
    fun enableShortLinkResolving() {
        viewModelScope.launch { appDataStoreManager.setResolveShortLinksEnabled(true) }
    }

    fun onUrlDecodeCheckedChange(enabled: Boolean) {
        viewModelScope.launch { appDataStoreManager.setUrlDecodeEnabled(enabled) }
    }

    fun onExtractUrlCheckedChange(enabled: Boolean) {
        viewModelScope.launch { appDataStoreManager.setExtractUrlEnabled(enabled) }
    }

    private suspend fun clean(
        text: String,
        decodeUrl: Boolean,
        extractUrl: Boolean,
        selection: Selection,
        resolved: Map<Url, Url>,
        remembered: SitePreferences?,
    ): Result =
        try {
            cleaner
                .clean(
                    text = text,
                    decodeUrl = decodeUrl,
                    declined = selection.declined,
                    additional = selection.additional,
                    resolved = resolved,
                    remembered = remembered ?: persistentMapOf(),
                )
                .let { result ->
                    Result.Success(
                        originalText = result.originalText,
                        cleanedText =
                            when {
                                extractUrl ->
                                    result.urls.firstOrNull()?.display?.toString().orEmpty()
                                else -> result.cleanedText
                            },
                        urls = result.urls.map { it.display.toString() }.toImmutableList(),
                        changes =
                            result.urls
                                .flatMap { changeRows(it, remembering = remembered != null) }
                                .toImmutableList(),
                    )
                }
        } catch (e: Exception) {
            Result.Error
        }

    /**
     * Builds the change list of a single URL: everything the sanitizers proposed, whether applied
     * or declined, followed by the parts of the cleaned URL which nothing proposed to remove — its
     * remaining parameters and its fragment — so that the user can remove those by hand as well.
     *
     * @param remembering Whether toggling a parameter is remembered for its site.
     */
    private fun changeRows(url: Cleaner.CleanedUrl, remembering: Boolean): List<ChangeRow> {
        fun site(action: Change.Action, site: String?) = site?.takeIf {
            remembering && action is Change.Action.RemoveParameter
        }

        val proposed =
            url.available
                .groupBy { it.action }
                .map { (action, changes) ->
                    ChangeRow(
                        action = action,
                        sanitizerIds =
                            changes
                                .mapNotNull { it.sanitizerId }
                                .filterNot { it == SanitizerId.Remembered }
                                .distinct()
                                .toImmutableList(),
                        // Still part of the URL as long as one of them was applied.
                        applied = changes.any { it in url.applied },
                        site = site(action, url.sites[changes.first()]),
                        remembered =
                            changes.any {
                                it.sanitizerId == SanitizerId.Remembered || it in url.kept
                            },
                    )
                }

        val added =
            url.applied
                .filter { it.sanitizerId == null }
                .map { ChangeRow(it.action, persistentListOf(), applied = true) }

        val listed =
            (url.available + url.applied)
                .mapNotNull { (it.action as? Change.Action.RemoveParameter)?.parameter }
                .toSet()

        val keepable =
            url.cleaned.parameters
                .filterNot { it in listed }
                .map {
                    val action = Change.Action.RemoveParameter(it)
                    ChangeRow(
                        action,
                        persistentListOf(),
                        applied = false,
                        site = site(action, url.cleaned.site),
                    )
                }

        return (proposed + added + keepable + listOfNotNull(removableFragment(url))).sortedBy {
            url.sortKey(it.action)
        }
    }

    /**
     * Where [action] sits in the URL, so that the change list reads top to bottom like the URL does
     * and — this is the point — a row never jumps position when it is checked or unchecked. Sorting
     * by applied state instead is what used to make the list flicker: ticking a parameter moved it
     * out of the "still there" group and into the "removed" one.
     *
     * A [Change.Action.Replace] swaps out the whole URL, so it (and, by extension, everything a
     * cascading sanitizer proposes for the URL it reveals) sorts before anything else — it is, in
     * effect, the first thing that happens to the URL.
     */
    private fun Cleaner.CleanedUrl.sortKey(action: Change.Action): Long =
        when (action) {
            is Change.Action.Replace -> 0L
            is Change.Action.SetComponent ->
                when (action.component) {
                    Change.Component.HOST -> 1L
                    Change.Component.PATH -> 2L
                    Change.Component.FRAGMENT -> Long.MAX_VALUE
                }
            is Change.Action.RemoveParameter -> 3L + parameterIndex(action.parameter)
        }

    /**
     * The position of [parameter] in the URL as it was shared, which a removed parameter no longer
     * has in [Cleaner.CleanedUrl.cleaned]. Falls back to its position there for a parameter which
     * only came into being partway through cleaning, such as one a redirect revealed.
     */
    private fun Cleaner.CleanedUrl.parameterIndex(parameter: Url.Parameter): Int {
        val index = original.parameters.indexOf(parameter)
        if (index != -1) return index

        val cleanedIndex = cleaned.parameters.indexOf(parameter)
        return if (cleanedIndex == -1) 0 else original.parameters.size + cleanedIndex
    }

    /**
     * The row for removing the fragment by hand.
     *
     * A fragment is part of the address of a section, not tracking, so it is only offered — never
     * checked. When a sanitizer does propose removing it, that proposal is already among the rows
     * and this adds nothing.
     */
    private fun removableFragment(url: Cleaner.CleanedUrl): ChangeRow? {
        val fragment = url.cleaned.fragment ?: return null

        val proposed =
            (url.available + url.applied).any {
                (it.action as? Change.Action.SetComponent)?.component == Change.Component.FRAGMENT
            }
        if (proposed) return null

        return ChangeRow(
            Change.Action.SetComponent(Change.Component.FRAGMENT, from = fragment, to = null),
            persistentListOf(),
            applied = false,
        )
    }
}
