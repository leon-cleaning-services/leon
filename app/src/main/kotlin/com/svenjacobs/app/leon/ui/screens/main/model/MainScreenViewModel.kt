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
import com.svenjacobs.app.leon.core.domain.CleanerService
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.inject.AppContainer.AppDataStoreManager
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import java.util.UUID
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val appDataStoreManager: AppDataStoreManager = AppDataStoreManager,
    private val cleanerService: CleanerService = CleanerService(),
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
            ) : Result

            data object Error : Result
        }
    }

    /** Text of a single incoming intent, identified by a stable [id]. */
    private data class Input(val id: String, val text: String)

    private val input = MutableStateFlow<Input?>(null)

    /** Id of the input for which the action after clean has already been performed. */
    private var handledActionInputId: String? = null

    val uiState =
        combine(
                input,
                appDataStoreManager.urlDecodeEnabled,
                appDataStoreManager.extractUrlEnabled,
                appDataStoreManager.customTabsEnabled,
                appDataStoreManager.actionAfterClean,
            ) { input, urlDecodeEnabled, extractUrlEnabled, isCustomTabsEnabled, actionAfterClean ->
                val result =
                    input?.let {
                        clean(
                            text = it.text,
                            decodeUrl = urlDecodeEnabled,
                            extractUrl = extractUrlEnabled,
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
        input.value = text?.let { Input(id = id, text = it) }
    }

    fun onResetClick() {
        input.value = null
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

    private suspend fun clean(text: String, decodeUrl: Boolean, extractUrl: Boolean): Result =
        try {
            cleanerService.clean(text = text, decodeUrl = decodeUrl).let { result ->
                Result.Success(
                    originalText = result.originalText,
                    cleanedText =
                        when {
                            extractUrl -> result.urls.firstOrNull().orEmpty()
                            else -> result.cleanedText
                        },
                    urls = result.urls,
                )
            }
        } catch (e: Exception) {
            Result.Error
        }
}
