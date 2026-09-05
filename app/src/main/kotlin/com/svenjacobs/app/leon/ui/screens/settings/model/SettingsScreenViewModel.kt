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

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.db.HistoryDao
import com.svenjacobs.app.leon.inject.AppContainer.AppContext
import com.svenjacobs.app.leon.inject.AppContainer.AppDataStoreManager
import com.svenjacobs.app.leon.inject.AppContainer.HistoryDao
import com.svenjacobs.app.leon.ui.model.AutoReset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class SettingsScreenViewModel(
    private val context: Context = AppContext,
    private val appDataStoreManager: AppDataStoreManager = AppDataStoreManager,
    private val historyDao: HistoryDao = HistoryDao,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val browserEnabled: Boolean = false,
        val customTabsEnabled: Boolean = false,
        val protectScreenEnabled: Boolean = false,
        val historyEnabled: Boolean = true,
        val actionAfterClean: ActionAfterClean = ActionAfterClean.DoNothing,
        val autoReset: AutoReset = AutoReset.Off,
    )

    private val browserEnabled = MutableStateFlow(false)

    /**
     * The browser and history switches, combined ahead of the outer [combine] so that it stays
     * within its five-flow limit.
     */
    private data class Toggles(val browserEnabled: Boolean, val historyEnabled: Boolean)

    val uiState: StateFlow<UiState> =
        combine(
                combine(browserEnabled, appDataStoreManager.historyEnabled) {
                    browserEnabled,
                    historyEnabled ->
                    Toggles(browserEnabled, historyEnabled)
                },
                appDataStoreManager.customTabsEnabled,
                appDataStoreManager.protectScreenEnabled,
                appDataStoreManager.actionAfterClean,
                appDataStoreManager.autoReset,
            ) { toggles, customTabsEnabled, protectScreenEnabled, actionAfterClean, autoReset ->
                UiState(
                    isLoading = false,
                    browserEnabled = toggles.browserEnabled,
                    customTabsEnabled = customTabsEnabled,
                    protectScreenEnabled = protectScreenEnabled,
                    historyEnabled = toggles.historyEnabled,
                    actionAfterClean = actionAfterClean ?: ActionAfterClean.DoNothing,
                    autoReset = autoReset ?: AutoReset.Off,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState(),
            )

    init {
        val enabledSetting = packageManager.getComponentEnabledSetting(componentName)
        browserEnabled.value = enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }

    fun onBrowserSwitchCheckedChange(checked: Boolean) {
        browserEnabled.value = checked
        packageManager.setComponentEnabledSetting(
            componentName,
            if (checked) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            },
            PackageManager.DONT_KILL_APP,
        )
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

    fun onActionAfterCleanClick(actionAfterClean: ActionAfterClean) {
        viewModelScope.launch { appDataStoreManager.setActionAfterClean(actionAfterClean) }
    }

    fun onAutoResetClick(autoReset: AutoReset) {
        viewModelScope.launch { appDataStoreManager.setAutoReset(autoReset) }
    }

    private val packageManager: PackageManager
        get() = context.packageManager

    private val componentName: ComponentName
        get() = ComponentName(context.packageName, "${context.packageName}.$COMPONENT_NAME_CLASS")

    companion object {
        private const val COMPONENT_NAME_CLASS = "MainBrowserActivity"
        const val GITHUB_URL = "https://github.com/leon-cleaning-services/leon"
        const val CONTRIBUTORS_URL = "$GITHUB_URL?tab=readme-ov-file#contributors"
        const val ISSUES_URL = "$GITHUB_URL/issues"
        const val AUTHOR_URL = "https://svenjacobs.com"
        const val SPONSORS_URL = "https://github.com/sponsors/svenjacobs"
    }
}
