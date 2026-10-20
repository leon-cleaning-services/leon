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

import com.svenjacobs.app.leon.datastore.AppDataStoreManager
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ShortLinkDialogViewModelTest :
    WordSpec(
        {
            fun appDataStoreManager(
                shortLinkDialogShown: Flow<Boolean> = flowOf(false),
                resolveShortLinksEnabled: Flow<Boolean> = flowOf(false),
            ): AppDataStoreManager = mockk {
                every { this@mockk.shortLinkDialogShown } returns shortLinkDialogShown
                every { this@mockk.resolveShortLinksEnabled } returns resolveShortLinksEnabled
                coEvery { setShortLinkDialogShown(any()) } just Runs
                coEvery { setResolveShortLinksEnabled(any()) } just Runs
            }

            beforeEach { Dispatchers.setMain(UnconfinedTestDispatcher()) }

            afterEach { Dispatchers.resetMain() }

            "uiState" should
                {
                    "not show the dialog while the stored flag is still loading" {
                        runTest(UnconfinedTestDispatcher()) {
                            val viewModel =
                                ShortLinkDialogViewModel(
                                    appDataStoreManager(shortLinkDialogShown = emptyFlow()),
                                )
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.isVisible shouldBe false
                        }
                    }

                    "show the dialog when it has not been seen yet" {
                        runTest(UnconfinedTestDispatcher()) {
                            val viewModel = ShortLinkDialogViewModel(appDataStoreManager())
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.isVisible shouldBe true
                            viewModel.uiState.value.resolveShortLinksEnabled shouldBe false
                        }
                    }

                    "not show the dialog once it has been seen" {
                        runTest(UnconfinedTestDispatcher()) {
                            val viewModel =
                                ShortLinkDialogViewModel(
                                    appDataStoreManager(shortLinkDialogShown = flowOf(true)),
                                )
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.isVisible shouldBe false
                        }
                    }

                    "reflect the current setting" {
                        runTest(UnconfinedTestDispatcher()) {
                            val enabled = MutableStateFlow(false)
                            val viewModel =
                                ShortLinkDialogViewModel(
                                    appDataStoreManager(resolveShortLinksEnabled = enabled),
                                )
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            enabled.value = true

                            viewModel.uiState.value.resolveShortLinksEnabled shouldBe true
                        }
                    }
                }

            "onCheckedChange" should
                {
                    "only update the switch, writing nothing yet" {
                        runTest(UnconfinedTestDispatcher()) {
                            val appDataStoreManager = appDataStoreManager()
                            val viewModel = ShortLinkDialogViewModel(appDataStoreManager)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.onCheckedChange(true)

                            viewModel.uiState.value.resolveShortLinksEnabled shouldBe true
                            viewModel.uiState.value.isVisible shouldBe true
                            coVerify(exactly = 0) {
                                appDataStoreManager.setResolveShortLinksEnabled(any())
                            }
                            coVerify(exactly = 0) {
                                appDataStoreManager.setShortLinkDialogShown(any())
                            }
                        }
                    }
                }

            "onConfirm" should
                {
                    "remember that the dialog has been seen" {
                        val appDataStoreManager = appDataStoreManager()
                        val viewModel = ShortLinkDialogViewModel(appDataStoreManager)

                        viewModel.onConfirm()

                        coVerify { appDataStoreManager.setShortLinkDialogShown(true) }
                        coVerify(exactly = 0) {
                            appDataStoreManager.setResolveShortLinksEnabled(any())
                        }
                    }

                    "write the switch's value once the dialog is closed" {
                        val appDataStoreManager = appDataStoreManager()
                        val viewModel = ShortLinkDialogViewModel(appDataStoreManager)

                        viewModel.onCheckedChange(true)
                        viewModel.onConfirm()

                        coVerifyOrder {
                            appDataStoreManager.setShortLinkDialogShown(true)
                            appDataStoreManager.setResolveShortLinksEnabled(true)
                        }
                    }
                }
        },
    )
