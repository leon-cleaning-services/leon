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
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.ui.model.ActionsAfterClean
import com.svenjacobs.app.leon.ui.model.InputSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Lets the user choose what happens with a URL once it has been cleaned, optionally per source. */
@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class SettingsActionAfterCleanScreenViewModel(
    private val platform: Platform,
    private val appDataStoreManager: AppDataStoreManager,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val actionsAfterClean: ActionsAfterClean = ActionsAfterClean(),
        val browserEnabled: Boolean = false,
        val supportsBrowserRegistration: Boolean = false,
    )

    val uiState: StateFlow<UiState> =
        appDataStoreManager.actionsAfterClean
            .map { actionsAfterClean ->
                UiState(
                    isLoading = false,
                    actionsAfterClean = actionsAfterClean,
                    browserEnabled = platform.isRegisteredAsBrowser(),
                    supportsBrowserRegistration = platform.supportsBrowserRegistration,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue =
                    UiState(supportsBrowserRegistration = platform.supportsBrowserRegistration),
            )

    fun onActionAfterCleanClick(actionAfterClean: ActionAfterClean) {
        viewModelScope.launch { appDataStoreManager.setActionAfterClean(actionAfterClean) }
    }

    fun onActionAfterCleanClick(source: InputSource, actionAfterClean: ActionAfterClean) {
        viewModelScope.launch { appDataStoreManager.setActionAfterClean(source, actionAfterClean) }
    }

    fun onPerSourceCheckedChange(checked: Boolean) {
        viewModelScope.launch { appDataStoreManager.setActionAfterCleanPerSource(checked) }
    }
}
