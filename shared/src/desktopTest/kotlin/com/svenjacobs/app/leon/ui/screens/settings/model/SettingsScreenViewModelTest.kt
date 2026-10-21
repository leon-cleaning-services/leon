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
import com.svenjacobs.app.leon.db.HistoryDao
import com.svenjacobs.app.leon.db.SiteParameterDao
import com.svenjacobs.app.leon.ui.model.AutoReset
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsScreenViewModelTest :
    WordSpec(
        {
            fun platform(): Platform = mockk {
                every { supportsBrowserRegistration } returns true
                every { supportsCustomTabs } returns true
                every { supportsProtectScreen } returns true
                every { isRegisteredAsBrowser() } returns false
            }

            fun appDataStoreManager(
                resolveShortLinksEnabled: Flow<Boolean> = flowOf(false),
                rememberPerSiteEnabled: Flow<Boolean> = flowOf(true),
            ): AppDataStoreManager = mockk {
                every { historyEnabled } returns flowOf(true)
                every { this@mockk.resolveShortLinksEnabled } returns resolveShortLinksEnabled
                every { this@mockk.rememberPerSiteEnabled } returns rememberPerSiteEnabled
                every { customTabsEnabled } returns flowOf(false)
                every { protectScreenEnabled } returns flowOf(false)
                every { actionAfterClean } returns flowOf(ActionAfterClean.DoNothing)
                every { autoReset } returns flowOf(AutoReset.Off)
                coEvery { setResolveShortLinksEnabled(any()) } just Runs
                coEvery { setRememberPerSiteEnabled(any()) } just Runs
            }

            fun siteParameterDao(): SiteParameterDao = mockk { coEvery { clear() } just Runs }

            fun viewModel(
                appDataStoreManager: AppDataStoreManager,
                siteParameterDao: SiteParameterDao = siteParameterDao(),
            ) =
                SettingsScreenViewModel(
                    platform = platform(),
                    appDataStoreManager = appDataStoreManager,
                    historyDao = mockk<HistoryDao>(),
                    siteParameterDao = siteParameterDao,
                )

            beforeEach { Dispatchers.setMain(UnconfinedTestDispatcher()) }

            afterEach { Dispatchers.resetMain() }

            "uiState" should
                {
                    "carry the resolve short links setting" {
                        runTest(UnconfinedTestDispatcher()) {
                            val enabled = MutableStateFlow(false)
                            val viewModel =
                                viewModel(appDataStoreManager(resolveShortLinksEnabled = enabled))
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.isLoading shouldBe false
                            viewModel.uiState.value.resolveShortLinksEnabled shouldBe false

                            enabled.value = true

                            viewModel.uiState.value.resolveShortLinksEnabled shouldBe true
                        }
                    }

                    "carry the remember per site setting" {
                        runTest(UnconfinedTestDispatcher()) {
                            val enabled = MutableStateFlow(true)
                            val viewModel =
                                viewModel(appDataStoreManager(rememberPerSiteEnabled = enabled))
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.rememberPerSiteEnabled shouldBe true

                            enabled.value = false

                            viewModel.uiState.value.rememberPerSiteEnabled shouldBe false
                        }
                    }
                }

            "onRememberPerSiteSwitchCheckedChange" should
                {
                    "store the setting" {
                        val appDataStoreManager = appDataStoreManager()
                        val viewModel = viewModel(appDataStoreManager)

                        viewModel.onRememberPerSiteSwitchCheckedChange(false)
                        viewModel.onRememberPerSiteSwitchCheckedChange(true)

                        coVerifyOrder {
                            appDataStoreManager.setRememberPerSiteEnabled(false)
                            appDataStoreManager.setRememberPerSiteEnabled(true)
                        }
                    }

                    "clear the remembered choices when turned off, and only then" {
                        val siteParameterDao = siteParameterDao()
                        val viewModel = viewModel(appDataStoreManager(), siteParameterDao)

                        viewModel.onRememberPerSiteSwitchCheckedChange(true)
                        coVerify(exactly = 0) { siteParameterDao.clear() }

                        viewModel.onRememberPerSiteSwitchCheckedChange(false)
                        coVerify(exactly = 1) { siteParameterDao.clear() }
                    }
                }

            "onResolveShortLinksSwitchCheckedChange" should
                {
                    "store the setting" {
                        val appDataStoreManager = appDataStoreManager()
                        val viewModel = viewModel(appDataStoreManager)

                        viewModel.onResolveShortLinksSwitchCheckedChange(true)
                        viewModel.onResolveShortLinksSwitchCheckedChange(false)

                        coVerifyOrder {
                            appDataStoreManager.setResolveShortLinksEnabled(true)
                            appDataStoreManager.setResolveShortLinksEnabled(false)
                        }
                    }
                }
        },
    )
