/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.inject.AppContainer.AppDataStoreManager
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.ChangeRow
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val appDataStoreManager: AppDataStoreManager = AppDataStoreManager,
    private val cleaner: Cleaner = Cleaner(),
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val inputId: String? = null,
        val isUrlDecodeEnabled: Boolean = false,
        val isExtractUrlEnabled: Boolean = false,
        val isCustomTabsEnabled: Boolean = false,
        val result: Result = Result.Empty,
        val actionAfterClean: ActionAfterClean = ActionAfterClean.DoNothing,
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
         * @param sanitizerIds Every sanitizer which proposed [action]; empty when the user asked
         *   for it themselves.
         * @param applied Whether the change is currently part of the cleaned URL.
         */
        data class ChangeRow(
            val action: Change.Action,
            val sanitizerIds: ImmutableList<SanitizerId>,
            val applied: Boolean,
        )
    }

    /** Text of a single incoming intent, identified by a stable [id]. */
    private data class Input(val id: String, val text: String)

    /**
     * What the user picked in the change list. Transient: it is reset whenever a new text arrives,
     * because the changes of the previous URL say nothing about the new one.
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

    val uiState =
        combine(
                combine(input, selection, ::Pair),
                appDataStoreManager.urlDecodeEnabled,
                appDataStoreManager.extractUrlEnabled,
                appDataStoreManager.customTabsEnabled,
                appDataStoreManager.actionAfterClean,
            ) {
                (input, selection),
                urlDecodeEnabled,
                extractUrlEnabled,
                isCustomTabsEnabled,
                actionAfterClean ->
                val result =
                    input?.let {
                        clean(
                            text = it.text,
                            decodeUrl = urlDecodeEnabled,
                            extractUrl = extractUrlEnabled,
                            selection = selection,
                        )
                    } ?: Result.Empty

                UiState(
                    isLoading = input == null,
                    inputId = input?.id,
                    isUrlDecodeEnabled = urlDecodeEnabled,
                    isExtractUrlEnabled = extractUrlEnabled,
                    isCustomTabsEnabled = isCustomTabsEnabled,
                    result = result,
                    actionAfterClean = actionAfterClean ?: ActionAfterClean.DoNothing,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState(),
            )

    fun setText(text: String?, id: String = UUID.randomUUID().toString()) {
        if (text == null && uiState.value.result is Result.Success) return
        selection.value = Selection()
        input.value = text?.let { Input(id = id, text = it) }
    }

    fun onResetClick() {
        selection.value = Selection()
        input.value = null
    }

    /**
     * Applies or reverts one row of the change list, which means every sanitizer's proposal behind
     * it — a row is only checked off the cleaned URL once nothing removes the value any more.
     */
    fun onChangeToggled(row: ChangeRow, apply: Boolean) {
        selection.update { current ->
            when {
                // A change the user added themselves only exists while it is applied.
                row.sanitizerIds.isEmpty() -> {
                    val change = Change(null, row.action)
                    current.copy(
                        additional =
                            if (apply) current.additional + change else current.additional - change
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
                            }
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
     */
    fun consumeActionAfterClean(inputId: String): Boolean {
        if (inputId == handledActionInputId) return false
        handledActionInputId = inputId
        return true
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
    ): Result =
        try {
            cleaner
                .clean(
                    text = text,
                    decodeUrl = decodeUrl,
                    declined = selection.declined,
                    additional = selection.additional,
                )
                .let { result ->
                    Result.Success(
                        originalText = result.originalText,
                        cleanedText =
                            when {
                                extractUrl ->
                                    result.urls.firstOrNull()?.cleaned?.toString().orEmpty()
                                else -> result.cleanedText
                            },
                        urls = result.urls.map { it.cleaned.toString() }.toImmutableList(),
                        changes =
                            result.urls.flatMap { changeRows(it, selection) }.toImmutableList(),
                    )
                }
        } catch (e: Exception) {
            Result.Error
        }

    /**
     * Builds the change list of a single URL: everything the sanitizers proposed, whether applied
     * or declined, followed by the parts of the cleaned URL which nothing proposed to remove — its
     * remaining parameters and its fragment — so that the user can remove those by hand as well.
     */
    private fun changeRows(url: Cleaner.CleanedUrl, selection: Selection): List<ChangeRow> {
        val proposed =
            url.available
                .groupBy { it.action }
                .map { (action, changes) ->
                    ChangeRow(
                        action = action,
                        sanitizerIds =
                            changes.mapNotNull { it.sanitizerId }.distinct().toImmutableList(),
                        // Still part of the URL as long as one of them was not declined.
                        applied = changes.any { it !in selection.declined },
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
                    ChangeRow(
                        Change.Action.RemoveParameter(it),
                        persistentListOf(),
                        applied = false,
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
