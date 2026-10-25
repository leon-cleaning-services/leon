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

import com.svenjacobs.app.leon.Platform
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.ui.model.ActionsAfterClean
import com.svenjacobs.app.leon.ui.model.InputSource
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsActionAfterCleanScreenViewModelTest :
    WordSpec(
        {
            fun platform(
                supportsBrowserRegistration: Boolean = true,
                registeredAsBrowser: Boolean = false,
            ): Platform = mockk {
                every { this@mockk.supportsBrowserRegistration } returns supportsBrowserRegistration
                every { isRegisteredAsBrowser() } returns registeredAsBrowser
            }

            fun appDataStoreManager(
                actionsAfterClean: ActionsAfterClean = ActionsAfterClean(),
            ): AppDataStoreManager = mockk {
                every { this@mockk.actionsAfterClean } returns flowOf(actionsAfterClean)
                coEvery { setActionAfterClean(any<ActionAfterClean>()) } just Runs
                coEvery { setActionAfterClean(any<InputSource>(), any()) } just Runs
                coEvery { setActionAfterCleanPerSource(any()) } just Runs
            }

            fun viewModel(
                appDataStoreManager: AppDataStoreManager = appDataStoreManager(),
                platform: Platform = platform(),
            ) = SettingsActionAfterCleanScreenViewModel(platform, appDataStoreManager)

            beforeEach { Dispatchers.setMain(UnconfinedTestDispatcher()) }

            afterEach { Dispatchers.resetMain() }

            "uiState" should
                {
                    "carry the actions after clean" {
                        runTest(UnconfinedTestDispatcher()) {
                            val actions =
                                ActionsAfterClean(
                                    otherApp = ActionAfterClean.OpenShareMenu,
                                    browser = ActionAfterClean.OpenUrl,
                                    inApp = ActionAfterClean.CopyToClipboard,
                                    perSource = true,
                                )
                            val viewModel = viewModel(appDataStoreManager(actions))
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.isLoading shouldBe false
                            viewModel.uiState.value.actionsAfterClean shouldBe actions
                        }
                    }

                    "carry whether Léon is registered as browser" {
                        runTest(UnconfinedTestDispatcher()) {
                            val viewModel =
                                viewModel(platform = platform(registeredAsBrowser = true))
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.browserEnabled shouldBe true
                        }
                    }

                    "carry whether the platform supports browser registration" {
                        runTest(UnconfinedTestDispatcher()) {
                            val viewModel =
                                viewModel(platform = platform(supportsBrowserRegistration = false))
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.supportsBrowserRegistration shouldBe false
                        }
                    }
                }

            "onActionAfterCleanClick" should
                {
                    "store the action for all sources" {
                        val appDataStoreManager = appDataStoreManager()
                        val viewModel = viewModel(appDataStoreManager)

                        viewModel.onActionAfterCleanClick(ActionAfterClean.CopyToClipboard)

                        coVerify(exactly = 1) {
                            appDataStoreManager.setActionAfterClean(
                                ActionAfterClean.CopyToClipboard,
                            )
                        }
                    }

                    "store the action for a single source" {
                        val appDataStoreManager = appDataStoreManager()
                        val viewModel = viewModel(appDataStoreManager)

                        viewModel.onActionAfterCleanClick(
                            InputSource.Browser,
                            ActionAfterClean.OpenUrl,
                        )

                        coVerify(exactly = 1) {
                            appDataStoreManager.setActionAfterClean(
                                InputSource.Browser,
                                ActionAfterClean.OpenUrl,
                            )
                        }
                    }
                }

            "onPerSourceCheckedChange" should
                {
                    "store the setting" {
                        val appDataStoreManager = appDataStoreManager()
                        val viewModel = viewModel(appDataStoreManager)

                        viewModel.onPerSourceCheckedChange(true)
                        viewModel.onPerSourceCheckedChange(false)

                        coVerifyOrder {
                            appDataStoreManager.setActionAfterCleanPerSource(true)
                            appDataStoreManager.setActionAfterCleanPerSource(false)
                        }
                    }
                }
        },
    )
