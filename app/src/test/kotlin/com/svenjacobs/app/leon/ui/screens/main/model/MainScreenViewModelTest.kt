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
package com.svenjacobs.app.leon.ui.screens.main.model

import com.svenjacobs.app.leon.core.domain.CleanerService
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest :
    WordSpec({
        lateinit var viewModel: MainScreenViewModel

        beforeEach {
            Dispatchers.setMain(UnconfinedTestDispatcher())

            val appDataStoreManager =
                mockk<AppDataStoreManager> {
                    every { urlDecodeEnabled } returns flowOf(false)
                    every { extractUrlEnabled } returns flowOf(false)
                    every { customTabsEnabled } returns flowOf(false)
                    every { actionAfterClean } returns flowOf(ActionAfterClean.OpenShareMenu)
                }

            val cleanerService =
                mockk<CleanerService> {
                    coEvery { clean(any(), any()) } answers
                        {
                            val text = firstArg<String>()
                            CleanerService.Result(
                                originalText = text,
                                cleanedText = text,
                                urls = persistentListOf(text),
                            )
                        }
                }

            viewModel =
                MainScreenViewModel(
                    appDataStoreManager = appDataStoreManager,
                    cleanerService = cleanerService,
                )
        }

        afterEach { Dispatchers.resetMain() }

        "setText" should
            {
                "assign a different inputId when the same text is submitted twice" {
                    runTest(UnconfinedTestDispatcher()) {
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com")
                        val firstId = viewModel.uiState.value.inputId

                        viewModel.setText("https://example.com")
                        val secondId = viewModel.uiState.value.inputId

                        firstId shouldNotBe null
                        secondId shouldNotBe null
                        secondId shouldNotBe firstId
                    }
                }
            }

        "consumeActionAfterClean" should
            {
                "return true on first call and false on a repeated call for the same inputId" {
                    viewModel.consumeActionAfterClean("id-1") shouldBe true
                    viewModel.consumeActionAfterClean("id-1") shouldBe false
                }

                "return true again for a new inputId, even with identical text (#775)" {
                    viewModel.consumeActionAfterClean("id-1") shouldBe true
                    viewModel.consumeActionAfterClean("id-2") shouldBe true
                }
            }
    })
