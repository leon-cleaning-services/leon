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
package com.svenjacobs.app.leon.ui.screens.history.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.db.HistoryDao
import com.svenjacobs.app.leon.db.HistoryEntry
import com.svenjacobs.app.leon.inject.AppContainer.AppDataStoreManager
import com.svenjacobs.app.leon.inject.AppContainer.HistoryDao
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryScreenViewModel(
    private val appDataStoreManager: AppDataStoreManager = AppDataStoreManager,
    private val historyDao: HistoryDao = HistoryDao,
) : ViewModel() {

    data class UiState(
        val isEnabled: Boolean = true,
        val isCustomTabsEnabled: Boolean = false,
        val entries: ImmutableList<HistoryEntry> = persistentListOf(),
    )

    val uiState: StateFlow<UiState> =
        combine(
                appDataStoreManager.historyEnabled,
                appDataStoreManager.customTabsEnabled,
                historyDao.entries(),
            ) { isEnabled, isCustomTabsEnabled, entries ->
                UiState(
                    isEnabled = isEnabled,
                    isCustomTabsEnabled = isCustomTabsEnabled,
                    entries = entries.toImmutableList(),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState(),
            )

    fun onDeleteClick(id: String) {
        viewModelScope.launch { historyDao.delete(id) }
    }

    fun onClearAllClick() {
        viewModelScope.launch { historyDao.clear() }
    }
}
