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

import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository.SanitizerState
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Tiktok
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.TiktokShortLink
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.job
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsSanitizersScreenViewModelTest :
    WordSpec(
        {
            beforeEach { Dispatchers.setMain(UnconfinedTestDispatcher()) }

            afterEach { Dispatchers.resetMain() }

            "uiState" should
                {
                    "mark the sanitizers which resolve short links, and the global setting" {
                        runTest(UnconfinedTestDispatcher()) {
                            val enabled = MutableStateFlow(false)
                            val repository =
                                mockk<SanitizerRepository> {
                                    every { state } returns
                                        flowOf(
                                            persistentListOf(
                                                SanitizerState(Tiktok.id, enabled = true),
                                                SanitizerState(TiktokShortLink.id, enabled = true),
                                            ),
                                        )
                                }
                            val appDataStoreManager =
                                mockk<AppDataStoreManager> {
                                    every { resolveShortLinksEnabled } returns enabled
                                }
                            val viewModel =
                                SettingsSanitizersScreenViewModel(
                                    sanitizers = persistentListOf(Tiktok, TiktokShortLink),
                                    repository = repository,
                                    appDataStoreManager = appDataStoreManager,
                                )
                            // Names are resolved from Compose resources, which load off the test
                            // dispatcher, hence waiting for the list rather than reading `value`.
                            val state = viewModel.uiState.first { it.sanitizers.isNotEmpty() }

                            val resolvesShortLinks =
                                state.sanitizers.associate {
                                    it.id to it.resolvesShortLinks
                                }
                            resolvesShortLinks shouldBe
                                mapOf(Tiktok.id to false, TiktokShortLink.id to true)
                            state.resolveShortLinksEnabled shouldBe false

                            enabled.value = true

                            viewModel.uiState
                                .first { it.resolveShortLinksEnabled }
                                .sanitizers
                                .size shouldBe 2

                            // The names load on another thread and resume on the main dispatcher;
                            // wait for that here rather than let it land in the next spec.
                            viewModel.viewModelScope.coroutineContext.job.cancelAndJoin()
                        }
                    }
                }
        },
    )
