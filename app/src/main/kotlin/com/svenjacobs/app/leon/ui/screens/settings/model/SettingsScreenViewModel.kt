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
package com.svenjacobs.app.leon.ui.screens.settings.model

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.core.domain.inject.DomainContainer.AppContext
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.inject.AppContainer.AppDataStoreManager
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
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val browserEnabled: Boolean = false,
        val customTabsEnabled: Boolean = false,
        val protectScreenEnabled: Boolean = false,
        val actionAfterClean: ActionAfterClean = ActionAfterClean.DoNothing,
    )

    private val browserEnabled = MutableStateFlow(false)

    val uiState: StateFlow<UiState> =
        combine(
                browserEnabled,
                appDataStoreManager.customTabsEnabled,
                appDataStoreManager.protectScreenEnabled,
                appDataStoreManager.actionAfterClean,
            ) { browserEnabled, customTabsEnabled, protectScreenEnabled, actionAfterClean ->
                UiState(
                    isLoading = false,
                    browserEnabled = browserEnabled,
                    customTabsEnabled = customTabsEnabled,
                    protectScreenEnabled = protectScreenEnabled,
                    actionAfterClean = actionAfterClean ?: ActionAfterClean.DoNothing,
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

    fun onActionAfterCleanClick(actionAfterClean: ActionAfterClean) {
        viewModelScope.launch { appDataStoreManager.setActionAfterClean(actionAfterClean) }
    }

    private val packageManager: PackageManager
        get() = context.packageManager

    private val componentName: ComponentName
        get() = ComponentName(context.packageName, "${context.packageName}.$COMPONENT_NAME_CLASS")

    private companion object {
        const val COMPONENT_NAME_CLASS = "MainBrowserActivity"
    }
}
