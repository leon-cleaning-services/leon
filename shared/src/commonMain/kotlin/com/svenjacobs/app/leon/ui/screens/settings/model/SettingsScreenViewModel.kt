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
package com.svenjacobs.app.leon.ui.screens.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.Platform
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.db.HistoryDao
import com.svenjacobs.app.leon.db.SiteParameterDao
import com.svenjacobs.app.leon.ui.model.AutoReset
import com.svenjacobs.app.leon.ui.model.Browser
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class SettingsScreenViewModel(
    private val platform: Platform,
    private val appDataStoreManager: AppDataStoreManager,
    private val historyDao: HistoryDao,
    private val siteParameterDao: SiteParameterDao,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val browserEnabled: Boolean = false,
        val customTabsEnabled: Boolean = false,
        val protectScreenEnabled: Boolean = false,
        val historyEnabled: Boolean = true,
        val resolveShortLinksEnabled: Boolean = false,
        val shortenLinksEnabled: Boolean = false,
        val rememberPerSiteEnabled: Boolean = true,
        val autoReset: AutoReset = AutoReset.Off,
        val supportsBrowserRegistration: Boolean = false,
        val supportsCustomTabs: Boolean = false,
        val supportsProtectScreen: Boolean = false,
        val supportsBrowserSelection: Boolean = false,
        /** The installed browsers the user can open links with. */
        val browsers: ImmutableList<Browser> = persistentListOf(),
        /**
         * The browser the user chose, or `null` for the default browser — also when the chosen one
         * is not installed anymore, because links then open with the default browser again.
         */
        val selectedBrowser: Browser? = null,
        /** Whether Léon is the default browser, which therefore cannot open links. */
        val isDefaultBrowser: Boolean = false,
        /** Whether the dialog choosing the browser links are opened with is showing. */
        val isBrowserDialogShown: Boolean = false,
    )

    private val browserEnabled = MutableStateFlow(false)

    /** The installed browsers and whether Léon is the default one. */
    private data class Browsers(
        val installed: ImmutableList<Browser> = persistentListOf(),
        val isDefaultBrowser: Boolean = false,
    )

    /** `null` until first loaded, so that the screen never shows the wrong browser in between. */
    private val browsers = MutableStateFlow<Browsers?>(null)

    private val browserDialogShown = MutableStateFlow(false)

    /**
     * [Browsers] with the package of the chosen one and whether the dialog choosing it is showing,
     * combined ahead of the outer [combine].
     */
    private data class BrowserSelection(
        val browsers: Browsers,
        val browserPackage: String?,
        val dialogShown: Boolean,
    )

    /**
     * The browser, history, short link, shorten and remember per site switches, combined ahead of
     * the outer [combine] so that it stays within its five-flow limit.
     */
    private data class Toggles(
        val browserEnabled: Boolean,
        val historyEnabled: Boolean,
        val resolveShortLinksEnabled: Boolean,
        val shortenLinksEnabled: Boolean,
        val rememberPerSiteEnabled: Boolean,
    )

    val uiState: StateFlow<UiState> =
        combine(
                combine(
                    browserEnabled,
                    appDataStoreManager.historyEnabled,
                    appDataStoreManager.resolveShortLinksEnabled,
                    appDataStoreManager.shortenLinksEnabled,
                    appDataStoreManager.rememberPerSiteEnabled,
                    ::Toggles,
                ),
                appDataStoreManager.customTabsEnabled,
                appDataStoreManager.protectScreenEnabled,
                appDataStoreManager.autoReset,
                combine(
                    browsers.filterNotNull(),
                    appDataStoreManager.browserPackage,
                    browserDialogShown,
                    ::BrowserSelection,
                ),
            ) { toggles, customTabsEnabled, protectScreenEnabled, autoReset, browserSelection ->
                val (browsers, browserPackage, browserDialogShown) = browserSelection
                UiState(
                    isLoading = false,
                    browserEnabled = toggles.browserEnabled,
                    customTabsEnabled = customTabsEnabled,
                    protectScreenEnabled = protectScreenEnabled,
                    historyEnabled = toggles.historyEnabled,
                    resolveShortLinksEnabled = toggles.resolveShortLinksEnabled,
                    shortenLinksEnabled = toggles.shortenLinksEnabled,
                    rememberPerSiteEnabled = toggles.rememberPerSiteEnabled,
                    autoReset = autoReset ?: AutoReset.Off,
                    supportsBrowserRegistration = platform.supportsBrowserRegistration,
                    supportsCustomTabs = platform.supportsCustomTabs,
                    supportsProtectScreen = platform.supportsProtectScreen,
                    supportsBrowserSelection = platform.supportsBrowserSelection,
                    browsers = browsers.installed,
                    selectedBrowser = browsers.installed.find { it.packageName == browserPackage },
                    isDefaultBrowser = browsers.isDefaultBrowser,
                    isBrowserDialogShown = browserDialogShown,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue =
                    UiState(
                        supportsBrowserRegistration = platform.supportsBrowserRegistration,
                        supportsCustomTabs = platform.supportsCustomTabs,
                        supportsProtectScreen = platform.supportsProtectScreen,
                        supportsBrowserSelection = platform.supportsBrowserSelection,
                    ),
            )

    init {
        browserEnabled.value = platform.isRegisteredAsBrowser()
        viewModelScope.launch { loadBrowsers() }
    }

    /**
     * Shows the dialog choosing the browser links are opened with — from the settings row, or from
     * a screen that just failed to open a link because it would have reopened Léon.
     *
     * The installed browsers are looked up again first: a browser may have been installed or
     * removed, or Léon made the default browser, since the screen was opened.
     */
    fun onBrowserClick() {
        viewModelScope.launch {
            loadBrowsers()
            browserDialogShown.value = true
        }
    }

    fun onBrowserDialogDismiss() {
        browserDialogShown.value = false
    }

    /** Stores the browser links are opened with, `null` being the default browser. */
    fun onBrowserSelect(browser: Browser?) {
        browserDialogShown.value = false
        viewModelScope.launch { appDataStoreManager.setBrowserPackage(browser?.packageName) }
    }

    private suspend fun loadBrowsers() {
        val installed =
            if (platform.supportsBrowserSelection) platform.browsers().toImmutableList()
            else persistentListOf()
        browsers.value =
            Browsers(installed = installed, isDefaultBrowser = platform.isDefaultBrowser())
    }

    fun onBrowserSwitchCheckedChange(checked: Boolean) {
        browserEnabled.value = checked
        platform.setRegisteredAsBrowser(checked)
    }

    fun onCustomTabsSwitchCheckedChange(checked: Boolean) {
        viewModelScope.launch { appDataStoreManager.setCustomTabsEnabled(checked) }
    }

    fun onProtectScreenSwitchCheckedChange(checked: Boolean) {
        viewModelScope.launch { appDataStoreManager.setProtectScreenEnabled(checked) }
    }

    fun onHistorySwitchCheckedChange(checked: Boolean) {
        viewModelScope.launch {
            appDataStoreManager.setHistoryEnabled(checked)
            // Turning the history off must leave nothing behind.
            if (!checked) historyDao.clear()
        }
    }

    fun onResolveShortLinksSwitchCheckedChange(checked: Boolean) {
        viewModelScope.launch { appDataStoreManager.setResolveShortLinksEnabled(checked) }
    }

    fun onShortenLinksSwitchCheckedChange(checked: Boolean) {
        viewModelScope.launch { appDataStoreManager.setShortenLinksEnabled(checked) }
    }

    fun onRememberPerSiteSwitchCheckedChange(checked: Boolean) {
        viewModelScope.launch {
            appDataStoreManager.setRememberPerSiteEnabled(checked)
            // Like the history, turning remembering off must leave nothing behind.
            if (!checked) siteParameterDao.clear()
        }
    }

    fun onAutoResetClick(autoReset: AutoReset) {
        viewModelScope.launch { appDataStoreManager.setAutoReset(autoReset) }
    }

    companion object {
        const val GITHUB_URL = "https://github.com/leon-cleaning-services/leon"
        const val CONTRIBUTORS_URL = "$GITHUB_URL?tab=readme-ov-file#contributors"
        const val ISSUES_URL = "$GITHUB_URL/issues"
        const val AUTHOR_URL = "https://svenjacobs.com"
        const val SPONSORS_URL = "https://github.com/sponsors/svenjacobs"
    }
}
