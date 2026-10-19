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
package com.svenjacobs.app.leon.ui.shortlink.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Drives the dialog that introduces short link resolving once, on the first start after it became
 * available — for new and existing installs alike.
 */
@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class ShortLinkDialogViewModel(private val appDataStoreManager: AppDataStoreManager) : ViewModel() {

    /**
     * @property isVisible whether the dialog is to be shown. It stays `false` until the stored flag
     *   has been read, so that a user who has already seen the dialog never sees it flash.
     * @property resolveShortLinksEnabled the value of the dialog's switch: the stored setting until
     *   the user flips it
     */
    data class UiState(
        val isVisible: Boolean = false,
        val resolveShortLinksEnabled: Boolean = false,
    )

    /**
     * The switch's value while the dialog is open, `null` while it still shows the stored setting.
     * It is only written once the dialog closes: switching resolving on right away would start
     * resolving a short link on the main screen, and its progress dialog would open on top of this
     * one.
     */
    private val pendingChecked = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<UiState> =
        combine(
                appDataStoreManager.shortLinkDialogShown,
                appDataStoreManager.resolveShortLinksEnabled,
                pendingChecked,
            ) { shown, resolveShortLinksEnabled, pendingChecked ->
                UiState(
                    isVisible = !shown,
                    resolveShortLinksEnabled = pendingChecked ?: resolveShortLinksEnabled,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState(),
            )

    /** Only updates the switch; the setting is written by [onConfirm]. */
    fun onCheckedChange(checked: Boolean) {
        pendingChecked.value = checked
    }

    /**
     * Confirming the dialog, or dismissing it, both count as having seen it, and write the switch's
     * value to the setting if the user flipped it. The dialog is closed first, so that it is gone
     * before resolving can start.
     */
    fun onConfirm() {
        val checked = pendingChecked.value
        viewModelScope.launch {
            appDataStoreManager.setShortLinkDialogShown(true)
            checked?.let { appDataStoreManager.setResolveShortLinksEnabled(it) }
        }
    }
}
