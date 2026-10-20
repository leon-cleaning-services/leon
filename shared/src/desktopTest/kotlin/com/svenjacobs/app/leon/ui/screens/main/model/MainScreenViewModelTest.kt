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
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Tiktok
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.TiktokShortLink
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution
import com.svenjacobs.app.leon.core.domain.shortlink.ShortLinkResolver
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.db.HistoryDao
import com.svenjacobs.app.leon.network.RESOLVE_ALL_BUDGET_MILLIS
import com.svenjacobs.app.leon.ui.model.AutoReset
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkState
import com.svenjacobs.app.leon.ui.shortlink.model.isSettled
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest :
    WordSpec(
        {
            lateinit var viewModel: MainScreenViewModel

            /**
             * A mocked [AppDataStoreManager] with every flow defaulted, so a test overrides only
             * what it needs.
             */
            fun appDataStoreManager(
                autoReset: AutoReset = AutoReset.Off,
                lastInput: AppDataStoreManager.LastInput? = null,
                actionAfterClean: ActionAfterClean = ActionAfterClean.DoNothing,
                historyEnabled: Boolean = true,
                resolveShortLinksEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false),
            ): AppDataStoreManager = mockk {
                every { urlDecodeEnabled } returns flowOf(false)
                every { extractUrlEnabled } returns flowOf(false)
                every { customTabsEnabled } returns flowOf(false)
                every { this@mockk.actionAfterClean } returns flowOf(actionAfterClean)
                every { this@mockk.autoReset } returns flowOf(autoReset)
                every { this@mockk.lastInput } returns flowOf(lastInput)
                every { this@mockk.historyEnabled } returns flowOf(historyEnabled)
                every { this@mockk.resolveShortLinksEnabled } returns resolveShortLinksEnabled
                coEvery { setResolveShortLinksEnabled(any()) } answers
                    {
                        resolveShortLinksEnabled.value = firstArg()
                    }
                coEvery { setLastInput(any(), any()) } just Runs
            }

            fun historyDao(): HistoryDao = mockk {
                coEvery { record(any(), any(), any(), any()) } just Runs
            }

            beforeEach {
                Dispatchers.setMain(UnconfinedTestDispatcher())

                val appDataStoreManager =
                    appDataStoreManager(actionAfterClean = ActionAfterClean.OpenShareMenu)

                val cleaner =
                    mockk<Cleaner> {
                        coEvery { shortLinks(any()) } returns emptyList()
                        coEvery { clean(any(), any(), any(), any(), any()) } answers
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
                                                display = url,
                                                available = persistentListOf(),
                                                applied = persistentListOf(),
                                            ),
                                        ),
                                )
                            }
                    }

                viewModel =
                    MainScreenViewModel(
                        appDataStoreManager = appDataStoreManager,
                        cleaner = cleaner,
                        historyDao = historyDao(),
                        shortLinkResolver = FakeShortLinkResolver(),
                    )
            }

            afterEach { Dispatchers.resetMain() }

            "onChangeToggled" should
                {
                    fun dataStore() = appDataStoreManager()

                    /** A view model on a real [Cleaner], so that changes are actually proposed. */
                    fun realViewModel(): MainScreenViewModel {
                        val repository =
                            mockk<SanitizerRepository> { coEvery { isEnabled(any()) } returns true }

                        return MainScreenViewModel(
                            appDataStoreManager = appDataStoreManager(),
                            cleaner =
                                Cleaner(
                                    sanitizers = persistentListOf(GoogleAnalytics),
                                    repository = repository,
                                ),
                            historyDao = historyDao(),
                            shortLinkResolver = FakeShortLinkResolver(),
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
                            result.changes.first { it.sanitizerIds.isNotEmpty() }.applied shouldBe
                                false
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
                            // Two sanitizers which happen to target the same parameter, on purpose
                            // —
                            // not a real pair from the catalog, so this test does not depend on
                            // which
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
                                    historyDao = historyDao(),
                                    shortLinkResolver = FakeShortLinkResolver(),
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
                                    historyDao = historyDao(),
                                    shortLinkResolver = FakeShortLinkResolver(),
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

                            // "page" precedes "utm_source" in the URL, but nothing proposes
                            // removing
                            // it — if rows were grouped by applied state it would sort after, not
                            // before, the checked "utm_source" row.
                            viewModel.setText("https://example.com/p?page=2&utm_source=x#reviews")

                            fun parameterOrder(result: Result.Success) =
                                result.changes.mapNotNull {
                                    (it.action as? Change.Action.RemoveParameter)?.parameter?.name
                                }

                            parameterOrder(
                                viewModel.uiState.value.result as Result.Success,
                            ) shouldBe listOf("page", "utm_source")

                            // Checking "page" must not move it past "utm_source".
                            val row =
                                (viewModel.uiState.value.result as Result.Success).changes.first()
                            viewModel.onChangeToggled(row, apply = true)

                            parameterOrder(
                                viewModel.uiState.value.result as Result.Success,
                            ) shouldBe listOf("page", "utm_source")
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
                                    historyDao = historyDao(),
                                    shortLinkResolver = FakeShortLinkResolver(),
                                )
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(
                                "https://www.google.com/url?q=https%3A%2F%2Fexample.com%2Fa" +
                                    "%3Futm_source%3Dnews&usg=x",
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
                                appDataStoreManager(autoReset = autoReset, lastInput = lastInput),
                            cleaner =
                                Cleaner(
                                    sanitizers = persistentListOf(GoogleAnalytics),
                                    repository =
                                        mockk<SanitizerRepository> {
                                            coEvery { isEnabled(any()) } returns true
                                        },
                                ),
                            historyDao = historyDao(),
                            shortLinkResolver = FakeShortLinkResolver(),
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
                            // The process-death case: the share intent is redelivered, but the
                            // timeout
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
                            // A live data store, so that the timestamp written by setText is read
                            // back
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
                                            every { historyEnabled } returns flowOf(true)
                                            every { resolveShortLinksEnabled } returns flowOf(false)
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
                                    historyDao = historyDao(),
                                    shortLinkResolver = FakeShortLinkResolver(),
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

            "history" should
                {
                    /** A view model on a real [Cleaner], recording to [dao]. */
                    fun viewModel(dao: HistoryDao, historyEnabled: Boolean = true) =
                        MainScreenViewModel(
                            appDataStoreManager =
                                appDataStoreManager(historyEnabled = historyEnabled),
                            cleaner =
                                Cleaner(
                                    sanitizers = persistentListOf(GoogleAnalytics),
                                    repository =
                                        mockk<SanitizerRepository> {
                                            coEvery { isEnabled(any()) } returns true
                                        },
                                ),
                            historyDao = dao,
                            shortLinkResolver = FakeShortLinkResolver(),
                        )

                    "record a new input's cleaned URL exactly once" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val viewModel = viewModel(dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(
                                "https://example.com/p?utm_source=x",
                                id = "id-1",
                            )

                            coVerify(exactly = 1) {
                                dao.record(
                                    id = "id-1",
                                    url = "https://example.com/p",
                                    at = any(),
                                    max = any(),
                                )
                            }
                        }
                    }

                    "record again with the new URL when a change is toggled" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val viewModel = viewModel(dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(
                                "https://example.com/p?utm_source=x",
                                id = "id-1",
                            )
                            val removal =
                                (viewModel.uiState.value.result as Result.Success).changes.first {
                                    it.sanitizerIds.isNotEmpty()
                                }
                            viewModel.onChangeToggled(removal, apply = false)

                            coVerify(exactly = 1) {
                                dao.record(
                                    id = "id-1",
                                    url = "https://example.com/p?utm_source=x",
                                    at = any(),
                                    max = any(),
                                )
                            }
                        }
                    }

                    "record a second setText under a different id" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val viewModel = viewModel(dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText("https://example.com/a", id = "id-1")
                            viewModel.setText("https://example.com/b", id = "id-2")

                            coVerify(exactly = 1) {
                                dao.record(
                                    id = "id-1",
                                    url = "https://example.com/a",
                                    at = any(),
                                    max = any(),
                                )
                            }
                            coVerify(exactly = 1) {
                                dao.record(
                                    id = "id-2",
                                    url = "https://example.com/b",
                                    at = any(),
                                    max = any(),
                                )
                            }
                        }
                    }

                    "not record when history is disabled" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val viewModel = viewModel(dao, historyEnabled = false)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText("https://example.com/p?utm_source=x", id = "id-1")

                            coVerify(exactly = 0) { dao.record(any(), any(), any(), any()) }
                        }
                    }

                    "not record text that yields no URL" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val viewModel = viewModel(dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText("just some text, no link here", id = "id-1")

                            coVerify(exactly = 0) { dao.record(any(), any(), any(), any()) }
                        }
                    }
                }

            "short links" should
                {
                    val shortLink = "https://www.tiktok.com/t/ZT8abc123/?_t=8abc"
                    val shortLinkUrl = Url.parse(shortLink)!!
                    // What the TikTok sanitizer leaves of the short link when it is not resolved.
                    val offline = "https://www.tiktok.com/t/ZT8abc123/"
                    val target =
                        Url.parse(
                            "https://www.tiktok.com/@user/video/7300000000000000000?_r=1&u_code=x",
                        )!!
                    val cleanedTarget = "https://www.tiktok.com/@user/video/7300000000000000000"

                    val otherShortLink = "https://vm.tiktok.com/ZMabc123/"
                    val otherShortLinkUrl = Url.parse(otherShortLink)!!
                    val otherTarget =
                        Url.parse("https://www.tiktok.com/@other/video/7400000000000000000")!!

                    /** A view model on a real [Cleaner] which knows TikTok and its short links. */
                    fun viewModel(
                        resolver: ShortLinkResolver,
                        enabled: MutableStateFlow<Boolean> = MutableStateFlow(true),
                        dao: HistoryDao = historyDao(),
                        shortLinkSanitizerEnabled: Boolean = true,
                    ) =
                        MainScreenViewModel(
                            appDataStoreManager =
                                appDataStoreManager(resolveShortLinksEnabled = enabled),
                            cleaner =
                                Cleaner(
                                    sanitizers = persistentListOf(Tiktok, TiktokShortLink),
                                    repository =
                                        mockk<SanitizerRepository> {
                                            coEvery { isEnabled(any()) } returns true
                                            coEvery { isEnabled(TiktokShortLink.id) } returns
                                                shortLinkSanitizerEnabled
                                        },
                                ),
                            historyDao = dao,
                            shortLinkResolver = resolver,
                        )

                    fun MainScreenViewModel.success() = uiState.value.result as Result.Success

                    "not contact anything while the setting is off" {
                        runTest(UnconfinedTestDispatcher()) {
                            val resolver = FakeShortLinkResolver()
                            val viewModel = viewModel(resolver, MutableStateFlow(false))
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText("https://example.com/p")
                            viewModel.setText(shortLink)

                            resolver.calls.shouldBeEmpty()
                        }
                    }

                    "hint at a short link while the setting is off, and count the result final" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val viewModel =
                                viewModel(FakeShortLinkResolver(), MutableStateFlow(false), dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink, id = "id-1")

                            val state = viewModel.uiState.value
                            state.shortLink shouldBe ShortLinkState.Disabled
                            viewModel.success().cleanedText shouldBe offline
                            // The user turned resolving off, so the action after clean may run
                            // with the short link cleaned as it is.
                            state.shortLink.isSettled shouldBe true
                            viewModel.consumeActionAfterClean("id-1") shouldBe true
                            coVerify(exactly = 1) {
                                dao.record(id = "id-1", url = offline, at = any(), max = any())
                            }
                        }
                    }

                    "show the offline result while resolving, then apply the target" {
                        runTest(UnconfinedTestDispatcher()) {
                            val gate = CompletableDeferred<Resolution>()
                            val resolver = FakeShortLinkResolver { gate.await() }
                            val dao = historyDao()
                            val viewModel = viewModel(resolver, dao = dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink, id = "id-1")

                            val resolving = viewModel.uiState.value.shortLink
                            resolving shouldBe
                                ShortLinkState.Resolving(persistentListOf(shortLinkUrl))
                            (resolving as ShortLinkState.Resolving).hosts shouldBe
                                listOf("www.tiktok.com")
                            resolving.isSettled shouldBe false
                            viewModel.success().cleanedText shouldBe offline
                            // Neither recorded nor shared while the result is not final.
                            coVerify(exactly = 0) { dao.record(any(), any(), any(), any()) }

                            gate.complete(Resolution.Resolved(target))

                            viewModel.uiState.value.shortLink shouldBe ShortLinkState.Idle
                            val result = viewModel.success()
                            result.cleanedText shouldBe cleanedTarget
                            val replace = result.changes.first()
                            replace.action shouldBe
                                Change.Action.Replace(from = shortLinkUrl, to = target)
                            replace.sanitizerIds shouldBe persistentListOf(TiktokShortLink.id)
                            replace.applied shouldBe true
                            resolver.calls shouldBe listOf(shortLinkUrl)
                            coVerify(exactly = 1) {
                                dao.record(
                                    id = "id-1",
                                    url = cleanedTarget,
                                    at = any(),
                                    max = any(),
                                )
                            }
                            coVerify(exactly = 0) {
                                dao.record(id = any(), url = offline, at = any(), max = any())
                            }
                        }
                    }

                    "keep the short link when its resolution is declined, without resolving again" {
                        runTest(UnconfinedTestDispatcher()) {
                            val resolver = FakeShortLinkResolver { Resolution.Resolved(target) }
                            val viewModel = viewModel(resolver)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink)
                            val replace = viewModel.success().changes.first()

                            viewModel.onChangeToggled(replace, apply = false)
                            viewModel.success().cleanedText shouldBe offline
                            viewModel.success().changes.first().applied shouldBe false

                            viewModel.onChangeToggled(replace, apply = true)
                            viewModel.success().cleanedText shouldBe cleanedTarget

                            resolver.calls shouldHaveSize 1
                        }
                    }

                    "clean offline after cancelling, without any further request" {
                        runTest(UnconfinedTestDispatcher()) {
                            var cancelled = false
                            val resolver = FakeShortLinkResolver {
                                try {
                                    awaitCancellation()
                                } finally {
                                    cancelled = true
                                }
                            }
                            val viewModel = viewModel(resolver)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink, id = "id-1")
                            viewModel.uiState.value.shortLink.shouldBeInstanceOf<
                                ShortLinkState.Resolving,
                            >()

                            viewModel.cancelShortLinkResolving()

                            cancelled shouldBe true
                            viewModel.uiState.value.shortLink shouldBe ShortLinkState.Idle
                            viewModel.success().cleanedText shouldBe offline

                            viewModel.onChangeToggled(
                                viewModel.success().changes.first(),
                                apply = true,
                            )
                            resolver.calls shouldHaveSize 1
                        }
                    }

                    "report a failure, and hold back the result until the user decides" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val resolver = FakeShortLinkResolver {
                                Resolution.Failed(Resolution.Reason.Offline)
                            }
                            val viewModel = viewModel(resolver, dao = dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink, id = "id-1")

                            val failed = viewModel.uiState.value.shortLink
                            failed shouldBe
                                ShortLinkState.Failed(
                                    persistentMapOf(shortLinkUrl to Resolution.Reason.Offline),
                                )
                            failed.isSettled shouldBe false
                            viewModel.success().cleanedText shouldBe offline
                            coVerify(exactly = 0) { dao.record(any(), any(), any(), any()) }
                        }
                    }

                    "continue without resolving: keep the offline result, settled" {
                        runTest(UnconfinedTestDispatcher()) {
                            val dao = historyDao()
                            val resolver = FakeShortLinkResolver {
                                Resolution.Failed(Resolution.Reason.HttpError(403))
                            }
                            val viewModel = viewModel(resolver, dao = dao)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink, id = "id-1")
                            viewModel.continueWithoutResolving()

                            viewModel.uiState.value.shortLink shouldBe ShortLinkState.Idle
                            viewModel.uiState.value.shortLink.isSettled shouldBe true
                            viewModel.success().cleanedText shouldBe offline
                            resolver.calls shouldHaveSize 1
                            coVerify(exactly = 1) {
                                dao.record(id = "id-1", url = offline, at = any(), max = any())
                            }
                        }
                    }

                    "apply partial successes, and retry only the short links which failed" {
                        runTest(UnconfinedTestDispatcher()) {
                            var otherAttempts = 0
                            val resolver = FakeShortLinkResolver { url ->
                                when (url) {
                                    shortLinkUrl -> Resolution.Resolved(target)
                                    else ->
                                        if (otherAttempts++ == 0) {
                                            Resolution.Failed(Resolution.Reason.Timeout)
                                        } else {
                                            Resolution.Resolved(otherTarget)
                                        }
                                }
                            }
                            val viewModel = viewModel(resolver)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText("$shortLink and $otherShortLink")

                            viewModel.uiState.value.shortLink shouldBe
                                ShortLinkState.Failed(
                                    persistentMapOf(otherShortLinkUrl to Resolution.Reason.Timeout),
                                )
                            // The success is applied already, underneath the error.
                            viewModel.success().cleanedText shouldBe
                                "$cleanedTarget and $otherShortLink"

                            viewModel.retryShortLinkResolving()

                            viewModel.uiState.value.shortLink shouldBe ShortLinkState.Idle
                            viewModel.success().cleanedText shouldBe
                                "$cleanedTarget and $otherTarget"
                            resolver.calls shouldBe
                                listOf(shortLinkUrl, otherShortLinkUrl, otherShortLinkUrl)
                        }
                    }

                    "resolve straight away once enabled from the hint" {
                        runTest(UnconfinedTestDispatcher()) {
                            val enabled = MutableStateFlow(false)
                            val resolver = FakeShortLinkResolver { Resolution.Resolved(target) }
                            val viewModel = viewModel(resolver, enabled)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink)
                            viewModel.uiState.value.shortLink shouldBe ShortLinkState.Disabled

                            viewModel.enableShortLinkResolving()

                            enabled.value shouldBe true
                            viewModel.uiState.value.shortLink shouldBe ShortLinkState.Idle
                            viewModel.success().cleanedText shouldBe cleanedTarget
                            resolver.calls shouldHaveSize 1
                        }
                    }

                    "give up on a short link which takes longer than the budget" {
                        runTest(UnconfinedTestDispatcher()) {
                            val resolver = FakeShortLinkResolver {
                                delay(RESOLVE_ALL_BUDGET_MILLIS * 2)
                                Resolution.Resolved(target)
                            }
                            val viewModel = viewModel(resolver)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink)
                            advanceTimeBy(RESOLVE_ALL_BUDGET_MILLIS - 1)
                            viewModel.uiState.value.shortLink.shouldBeInstanceOf<
                                ShortLinkState.Resolving,
                            >()

                            advanceTimeBy(2)

                            viewModel.uiState.value.shortLink shouldBe
                                ShortLinkState.Failed(
                                    persistentMapOf(shortLinkUrl to Resolution.Reason.Timeout),
                                )
                        }
                    }

                    "neither hint nor resolve when the vendor's short link sanitizer is off" {
                        runTest(UnconfinedTestDispatcher()) {
                            for (enabled in listOf(true, false)) {
                                val resolver = FakeShortLinkResolver()
                                val viewModel =
                                    viewModel(
                                        resolver,
                                        MutableStateFlow(enabled),
                                        shortLinkSanitizerEnabled = false,
                                    )
                                val collecting = launch { viewModel.uiState.collect {} }

                                viewModel.setText(shortLink)

                                viewModel.uiState.value.shortLink shouldBe ShortLinkState.Idle
                                viewModel.success().cleanedText shouldBe offline
                                resolver.calls.shouldBeEmpty()
                                collecting.cancel()
                            }
                        }
                    }

                    "not resolve again when the screen resubscribes" {
                        runTest(UnconfinedTestDispatcher()) {
                            val resolver = FakeShortLinkResolver { Resolution.Resolved(target) }
                            val viewModel = viewModel(resolver)
                            val collecting = launch { viewModel.uiState.collect {} }

                            viewModel.setText(shortLink)
                            viewModel.success().cleanedText shouldBe cleanedTarget

                            // Long enough for the state to stop being shared, as when the app is
                            // in the background.
                            collecting.cancel()
                            advanceTimeBy(10_000)
                            backgroundScope.launch { viewModel.uiState.collect {} }

                            viewModel.success().cleanedText shouldBe cleanedTarget
                            resolver.calls shouldHaveSize 1
                        }
                    }
                }
        },
    )

/** A [ShortLinkResolver] which records every request and answers it through [answer]. */
private class FakeShortLinkResolver(
    private val answer: suspend (Url) -> Resolution = { error("Unexpected request for $it") },
) : ShortLinkResolver {

    val calls = mutableListOf<Url>()

    override suspend fun resolve(url: Url): Resolution {
        calls += url
        return answer(url)
    }
}
