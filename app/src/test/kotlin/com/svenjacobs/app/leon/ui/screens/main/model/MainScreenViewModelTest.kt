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

import com.svenjacobs.app.leon.core.domain.Cleaner
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.core.domain.change.Change
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Echobox
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleAnalytics
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleSearch
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.ui.model.AutoReset
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
                    every { autoReset } returns flowOf(AutoReset.Off)
                    every { lastInput } returns flowOf(null)
                    coEvery { setLastInput(any(), any()) } just Runs
                }

            val cleaner =
                mockk<Cleaner> {
                    coEvery { clean(any(), any(), any(), any()) } answers
                        {
                            val text = firstArg<String>()
                            val url = requireNotNull(Url.parse(text))
                            Cleaner.Result(
                                originalText = text,
                                cleanedText = text,
                                urls =
                                    persistentListOf(
                                        Cleaner.CleanedUrl(
                                            original = url,
                                            cleaned = url,
                                            available = persistentListOf(),
                                            applied = persistentListOf(),
                                        )
                                    ),
                            )
                        }
                }

            viewModel =
                MainScreenViewModel(appDataStoreManager = appDataStoreManager, cleaner = cleaner)
        }

        afterEach { Dispatchers.resetMain() }

        "onChangeToggled" should
            {
                fun dataStore() =
                    mockk<AppDataStoreManager> {
                        every { urlDecodeEnabled } returns flowOf(false)
                        every { extractUrlEnabled } returns flowOf(false)
                        every { customTabsEnabled } returns flowOf(false)
                        every { actionAfterClean } returns flowOf(ActionAfterClean.DoNothing)
                        every { autoReset } returns flowOf(AutoReset.Off)
                        every { lastInput } returns flowOf(null)
                        coEvery { setLastInput(any(), any()) } just Runs
                    }

                /** A view model on a real [Cleaner], so that changes are actually proposed. */
                fun realViewModel(): MainScreenViewModel {
                    val repository =
                        mockk<SanitizerRepository> { coEvery { isEnabled(any()) } returns true }

                    return MainScreenViewModel(
                        appDataStoreManager =
                            mockk<AppDataStoreManager> {
                                every { urlDecodeEnabled } returns flowOf(false)
                                every { extractUrlEnabled } returns flowOf(false)
                                every { customTabsEnabled } returns flowOf(false)
                                every { actionAfterClean } returns
                                    flowOf(ActionAfterClean.DoNothing)
                                every { autoReset } returns flowOf(AutoReset.Off)
                                every { lastInput } returns flowOf(null)
                                coEvery { setLastInput(any(), any()) } just Runs
                            },
                        cleaner =
                            Cleaner(
                                sanitizers = persistentListOf(GoogleAnalytics),
                                repository = repository,
                            ),
                    )
                }

                "list a proposed removal as applied and attribute it to its sanitizer" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel = realViewModel()
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p?utm_source=x&page=2")

                        val result = viewModel.uiState.value.result as Result.Success
                        result.cleanedText shouldBe "https://example.com/p?page=2"

                        val removal = result.changes.first { it.sanitizerIds.isNotEmpty() }
                        removal.applied shouldBe true
                        removal.sanitizerIds shouldBe persistentListOf(GoogleAnalytics.id)

                        // The untouched parameter is offered as well, unchecked.
                        val kept = result.changes.first { it.sanitizerIds.isEmpty() }
                        kept.applied shouldBe false
                    }
                }

                "put a declined removal back into the cleaned URL" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel = realViewModel()
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p?utm_source=x&page=2")
                        val removal =
                            (viewModel.uiState.value.result as Result.Success).changes.first {
                                it.sanitizerIds.isNotEmpty()
                            }

                        viewModel.onChangeToggled(removal, apply = false)

                        val result = viewModel.uiState.value.result as Result.Success
                        result.cleanedText shouldBe "https://example.com/p?utm_source=x&page=2"
                        result.changes.first { it.sanitizerIds.isNotEmpty() }.applied shouldBe false
                    }
                }

                "remove a parameter which no sanitizer proposed" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel = realViewModel()
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p?utm_source=x&page=2")
                        val kept =
                            (viewModel.uiState.value.result as Result.Success).changes.first {
                                it.sanitizerIds.isEmpty()
                            }

                        viewModel.onChangeToggled(kept, apply = true)

                        (viewModel.uiState.value.result as Result.Success).cleanedText shouldBe
                            "https://example.com/p"
                    }
                }

                "collapse a removal several sanitizers propose into one row" {
                    runTest(UnconfinedTestDispatcher()) {
                        // Two sanitizers which happen to target the same parameter, on purpose —
                        // not a real pair from the catalog, so this test does not depend on which
                        // real sanitizers overlap today.
                        val first =
                            Sanitizer(
                                id = SanitizerId("fake1"),
                                name = "fake1",
                                rules = persistentListOf(Rule.RemoveParameters("utm_source")),
                            )
                        val second =
                            Sanitizer(
                                id = SanitizerId("fake2"),
                                name = "fake2",
                                rules = persistentListOf(Rule.RemoveParameters("utm_source")),
                            )
                        val viewModel =
                            MainScreenViewModel(
                                appDataStoreManager = dataStore(),
                                cleaner =
                                    Cleaner(
                                        sanitizers = persistentListOf(first, second),
                                        repository =
                                            mockk<SanitizerRepository> {
                                                coEvery { isEnabled(any()) } returns true
                                            },
                                    ),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p?utm_source=x")

                        val rows =
                            (viewModel.uiState.value.result as Result.Success).changes.filter {
                                it.sanitizerIds.isNotEmpty()
                            }

                        rows shouldHaveSize 1
                        rows.first().sanitizerIds shouldBe persistentListOf(first.id, second.id)

                        // Unchecking the row has to silence every sanitizer behind it.
                        viewModel.onChangeToggled(rows.first(), apply = false)

                        (viewModel.uiState.value.result as Result.Success).cleanedText shouldBe
                            "https://example.com/p?utm_source=x"
                    }
                }

                "offer the fragment for removal without applying it" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel = realViewModel()
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p?utm_source=x#reviews")

                        val result = viewModel.uiState.value.result as Result.Success
                        // The fragment survives cleaning: it addresses a section, it is not
                        // tracking.
                        result.cleanedText shouldBe "https://example.com/p#reviews"

                        val fragment =
                            result.changes.first {
                                it.action is Change.Action.SetComponent &&
                                    (it.action as Change.Action.SetComponent).component ==
                                        Change.Component.FRAGMENT
                            }
                        fragment.applied shouldBe false
                        fragment.sanitizerIds.shouldBeEmpty()

                        viewModel.onChangeToggled(fragment, apply = true)

                        (viewModel.uiState.value.result as Result.Success).cleanedText shouldBe
                            "https://example.com/p"
                    }
                }

                "not offer a fragment which is not there" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel = realViewModel()
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p?utm_source=x")

                        (viewModel.uiState.value.result as Result.Success).changes.none {
                            it.action is Change.Action.SetComponent
                        } shouldBe true
                    }
                }

                "check the fragment row when a sanitizer removes it" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel =
                            MainScreenViewModel(
                                appDataStoreManager = dataStore(),
                                cleaner =
                                    Cleaner(
                                        sanitizers = persistentListOf(Echobox),
                                        repository =
                                            mockk<SanitizerRepository> {
                                                coEvery { isEnabled(any()) } returns true
                                            },
                                    ),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/article#Echobox=123")

                        val result = viewModel.uiState.value.result as Result.Success
                        result.cleanedText shouldBe "https://example.com/article"

                        // One row, checked, attributed to the sanitizer - not a second offer.
                        val rows = result.changes
                        rows shouldHaveSize 1
                        rows.first().applied shouldBe true
                        rows.first().sanitizerIds shouldBe persistentListOf(Echobox.id)
                    }
                }

                "order rows by their position in the URL, not by applied state" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel = realViewModel()
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        // "page" precedes "utm_source" in the URL, but nothing proposes removing
                        // it — if rows were grouped by applied state it would sort after, not
                        // before, the checked "utm_source" row.
                        viewModel.setText("https://example.com/p?page=2&utm_source=x#reviews")

                        fun parameterOrder(result: Result.Success) =
                            result.changes.mapNotNull {
                                (it.action as? Change.Action.RemoveParameter)?.parameter?.name
                            }

                        parameterOrder(viewModel.uiState.value.result as Result.Success) shouldBe
                            listOf("page", "utm_source")

                        // Checking "page" must not move it past "utm_source".
                        val row = (viewModel.uiState.value.result as Result.Success).changes.first()
                        viewModel.onChangeToggled(row, apply = true)

                        parameterOrder(viewModel.uiState.value.result as Result.Success) shouldBe
                            listOf("page", "utm_source")
                    }
                }

                "put a URL replacement before the rows it reveals" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel =
                            MainScreenViewModel(
                                appDataStoreManager = dataStore(),
                                cleaner =
                                    Cleaner(
                                        sanitizers =
                                            persistentListOf(GoogleSearch, GoogleAnalytics),
                                        repository =
                                            mockk<SanitizerRepository> {
                                                coEvery { isEnabled(any()) } returns true
                                            },
                                    ),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText(
                            "https://www.google.com/url?q=https%3A%2F%2Fexample.com%2Fa" +
                                "%3Futm_source%3Dnews&usg=x"
                        )

                        val result = viewModel.uiState.value.result as Result.Success
                        val first = result.changes.first().action
                        first.shouldBeInstanceOf<Change.Action.Replace>()
                    }
                }

                "forget the selection when a new text arrives" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel = realViewModel()
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p?utm_source=x")
                        val removal =
                            (viewModel.uiState.value.result as Result.Success).changes.first()
                        viewModel.onChangeToggled(removal, apply = false)

                        viewModel.setText("https://other.com/q?utm_source=y")

                        (viewModel.uiState.value.result as Result.Success).cleanedText shouldBe
                            "https://other.com/q"
                    }
                }
            }

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

        "autoReset" should
            {
                fun viewModel(autoReset: AutoReset, lastInput: AppDataStoreManager.LastInput?) =
                    MainScreenViewModel(
                        appDataStoreManager =
                            mockk<AppDataStoreManager> {
                                every { urlDecodeEnabled } returns flowOf(false)
                                every { extractUrlEnabled } returns flowOf(false)
                                every { customTabsEnabled } returns flowOf(false)
                                every { actionAfterClean } returns
                                    flowOf(ActionAfterClean.DoNothing)
                                every { this@mockk.autoReset } returns flowOf(autoReset)
                                every { this@mockk.lastInput } returns flowOf(lastInput)
                                coEvery { setLastInput(any(), any()) } just Runs
                            },
                        cleaner =
                            Cleaner(
                                sanitizers = persistentListOf(GoogleAnalytics),
                                repository =
                                    mockk<SanitizerRepository> {
                                        coEvery { isEnabled(any()) } returns true
                                    },
                            ),
                    )

                "report the deadline of an input which is still fresh" {
                    runTest(UnconfinedTestDispatcher()) {
                        val at = System.currentTimeMillis()
                        val viewModel =
                            viewModel(
                                AutoReset.OneMinute,
                                AppDataStoreManager.LastInput(id = "id-1", at = at),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p", id = "id-1")

                        viewModel.uiState.value.result.shouldBeInstanceOf<Result.Success>()
                        viewModel.uiState.value.autoResetAt shouldBe at + 60_000L
                    }
                }

                "drop an input whose deadline has already passed" {
                    runTest(UnconfinedTestDispatcher()) {
                        // The process-death case: the share intent is redelivered, but the timeout
                        // ran out while the app was gone.
                        val viewModel =
                            viewModel(
                                AutoReset.OneMinute,
                                AppDataStoreManager.LastInput(
                                    id = "id-1",
                                    at = System.currentTimeMillis() - 120_000L,
                                ),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p", id = "id-1")

                        viewModel.uiState.value.result shouldBe Result.Empty
                        viewModel.uiState.value.autoResetAt shouldBe null
                    }
                }

                "ignore a stale timestamp which belongs to another input" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel =
                            viewModel(
                                AutoReset.OneMinute,
                                AppDataStoreManager.LastInput(
                                    id = "id-1",
                                    at = System.currentTimeMillis() - 120_000L,
                                ),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p", id = "id-2")

                        viewModel.uiState.value.result.shouldBeInstanceOf<Result.Success>()
                        viewModel.uiState.value.autoResetAt shouldBe null
                    }
                }

                "start a new deadline for a URL shared after a reset" {
                    runTest(UnconfinedTestDispatcher()) {
                        // A live data store, so that the timestamp written by setText is read back
                        // the way it is on a device.
                        val lastInput = MutableStateFlow<AppDataStoreManager.LastInput?>(null)
                        val viewModel =
                            MainScreenViewModel(
                                appDataStoreManager =
                                    mockk<AppDataStoreManager> {
                                        every { urlDecodeEnabled } returns flowOf(false)
                                        every { extractUrlEnabled } returns flowOf(false)
                                        every { customTabsEnabled } returns flowOf(false)
                                        every { actionAfterClean } returns
                                            flowOf(ActionAfterClean.DoNothing)
                                        every { autoReset } returns flowOf(AutoReset.OneMinute)
                                        every { this@mockk.lastInput } returns lastInput
                                        coEvery { setLastInput(any(), any()) } answers
                                            {
                                                lastInput.value =
                                                    AppDataStoreManager.LastInput(
                                                        id = firstArg(),
                                                        at = secondArg(),
                                                    )
                                            }
                                    },
                                cleaner =
                                    Cleaner(
                                        sanitizers = persistentListOf(GoogleAnalytics),
                                        repository =
                                            mockk<SanitizerRepository> {
                                                coEvery { isEnabled(any()) } returns true
                                            },
                                    ),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/a", id = "id-1")
                        val first = viewModel.uiState.value.autoResetAt
                        first shouldNotBe null

                        // What auto-reset does when the deadline passes.
                        viewModel.onResetClick()
                        viewModel.uiState.value.result shouldBe Result.Empty
                        viewModel.uiState.value.autoResetAt shouldBe null

                        viewModel.setText("https://example.com/b", id = "id-2")

                        viewModel.uiState.value.result.shouldBeInstanceOf<Result.Success>()
                        val second = viewModel.uiState.value.autoResetAt
                        second shouldNotBe null
                        (second!! >= first!!) shouldBe true
                    }
                }

                "never expire an input when auto-reset is off" {
                    runTest(UnconfinedTestDispatcher()) {
                        val viewModel =
                            viewModel(
                                AutoReset.Off,
                                AppDataStoreManager.LastInput(
                                    id = "id-1",
                                    at = System.currentTimeMillis() - 120_000L,
                                ),
                            )
                        backgroundScope.launch { viewModel.uiState.collect {} }

                        viewModel.setText("https://example.com/p", id = "id-1")

                        viewModel.uiState.value.result.shouldBeInstanceOf<Result.Success>()
                        viewModel.uiState.value.autoResetAt shouldBe null
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
