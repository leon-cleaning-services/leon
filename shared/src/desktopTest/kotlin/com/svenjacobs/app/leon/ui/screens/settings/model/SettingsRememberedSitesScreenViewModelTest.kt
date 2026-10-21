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

import com.svenjacobs.app.leon.db.SiteParameter
import com.svenjacobs.app.leon.db.SiteParameter.Decision
import com.svenjacobs.app.leon.db.SiteParameterDao
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
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
class SettingsRememberedSitesScreenViewModelTest :
    WordSpec(
        {
            val entries =
                listOf(
                    SiteParameter("amazon.com", "th", Decision.KEEP),
                    SiteParameter("example.com", "ref", Decision.REMOVE),
                    SiteParameter("example.com", "src", Decision.REMOVE),
                )

            fun dao(): SiteParameterDao = mockk {
                every { entries() } returns flowOf(entries)
                coEvery { delete(any(), any()) } just Runs
                coEvery { deleteSite(any()) } just Runs
                coEvery { clear() } just Runs
            }

            beforeEach { Dispatchers.setMain(UnconfinedTestDispatcher()) }

            afterEach { Dispatchers.resetMain() }

            "uiState" should
                {
                    "group the remembered parameters by site" {
                        runTest(UnconfinedTestDispatcher()) {
                            val viewModel = SettingsRememberedSitesScreenViewModel(dao())
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.uiState.value.isLoading shouldBe false
                            viewModel.uiState.value.sites.map { site ->
                                site.site to site.parameters.map { it.parameter }
                            } shouldBe
                                listOf(
                                    "amazon.com" to listOf("th"),
                                    "example.com" to listOf("ref", "src"),
                                )
                        }
                    }
                }

            "forgetting" should
                {
                    "delete a parameter, a site or everything" {
                        val dao = dao()
                        val viewModel = SettingsRememberedSitesScreenViewModel(dao)

                        viewModel.onForgetClick(entries[1])
                        viewModel.onForgetSiteClick("amazon.com")
                        viewModel.onForgetAllClick()

                        coVerify {
                            dao.delete("example.com", "ref")
                            dao.deleteSite("amazon.com")
                            dao.clear()
                        }
                    }
                }
        },
    )
