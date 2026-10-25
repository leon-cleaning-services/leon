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
package com.svenjacobs.app.leon.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.ui.model.ActionsAfterClean
import com.svenjacobs.app.leon.ui.model.InputSource
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AppDataStoreManagerTest :
    WordSpec(
        {
            val legacyKey = stringPreferencesKey("action_after_clean")
            val browserKey = stringPreferencesKey("action_after_clean_browser")

            /** Runs [block] against a manager backed by a real data store on a temporary file. */
            fun withManager(block: suspend (AppDataStoreManager, DataStore<Preferences>) -> Unit) {
                val dir = createTempDirectory("leon-datastore").toFile()
                val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
                try {
                    val dataStore =
                        PreferenceDataStoreFactory.create(scope = scope) {
                            File(dir, "settings.preferences_pb")
                        }
                    runBlocking { block(AppDataStoreManager(dataStore), dataStore) }
                } finally {
                    scope.cancel()
                    dir.deleteRecursively()
                }
            }

            suspend fun AppDataStoreManager.current() = actionsAfterClean.first()

            "actionsAfterClean" should
                {
                    "default to DoNothing without per source" {
                        withManager { manager, _ -> manager.current() shouldBe ActionsAfterClean() }
                    }

                    "use a legacy value for every source" {
                        withManager { manager, dataStore ->
                            dataStore.edit { it[legacyKey] = "OpenUrl" }

                            manager.current() shouldBe
                                ActionsAfterClean(
                                    otherApp = ActionAfterClean.OpenUrl,
                                    browser = ActionAfterClean.OpenUrl,
                                    inApp = ActionAfterClean.OpenUrl,
                                )
                        }
                    }

                    "prefer the own value of a source over the legacy one" {
                        withManager { manager, dataStore ->
                            dataStore.edit { it[legacyKey] = "OpenUrl" }
                            manager.setActionAfterClean(
                                InputSource.Browser,
                                ActionAfterClean.OpenShareMenu,
                            )

                            manager.current().browser shouldBe ActionAfterClean.OpenShareMenu
                            manager.current().inApp shouldBe ActionAfterClean.OpenUrl
                        }
                    }

                    "fall back to DoNothing for an unknown value" {
                        withManager { manager, dataStore ->
                            dataStore.edit { it[legacyKey] = "Bogus" }

                            manager.current() shouldBe ActionsAfterClean()
                        }
                    }

                    "fall back to the legacy value for an unknown value of a source" {
                        withManager { manager, dataStore ->
                            dataStore.edit {
                                it[legacyKey] = "CopyToClipboard"
                                it[browserKey] = "Bogus"
                            }

                            manager.current().browser shouldBe ActionAfterClean.CopyToClipboard
                        }
                    }
                }

            "setActionAfterClean" should
                {
                    "set every source" {
                        withManager { manager, _ ->
                            manager.setActionAfterClean(ActionAfterClean.CopyToClipboard)

                            manager.current() shouldBe
                                ActionsAfterClean(
                                    otherApp = ActionAfterClean.CopyToClipboard,
                                    browser = ActionAfterClean.CopyToClipboard,
                                    inApp = ActionAfterClean.CopyToClipboard,
                                )
                        }
                    }

                    "set only the given source" {
                        withManager { manager, _ ->
                            manager.setActionAfterClean(InputSource.InApp, ActionAfterClean.OpenUrl)

                            manager.current() shouldBe
                                ActionsAfterClean(inApp = ActionAfterClean.OpenUrl)
                        }
                    }

                    "set the other app source through the legacy key" {
                        withManager { manager, dataStore ->
                            manager.setActionAfterClean(
                                InputSource.OtherApp,
                                ActionAfterClean.OpenUrl,
                            )

                            dataStore.data.first()[legacyKey] shouldBe "OpenUrl"
                        }
                    }
                }

            "setActionAfterCleanPerSource" should
                {
                    "store the flag and keep the values when turned on" {
                        withManager { manager, _ ->
                            manager.setActionAfterClean(ActionAfterClean.OpenUrl)
                            manager.setActionAfterClean(
                                InputSource.Browser,
                                ActionAfterClean.DoNothing,
                            )
                            manager.setActionAfterCleanPerSource(true)

                            val actions = manager.current()
                            actions.perSource shouldBe true
                            actions.forSource(InputSource.OtherApp) shouldBe
                                ActionAfterClean.OpenUrl
                            actions.forSource(InputSource.Browser) shouldBe
                                ActionAfterClean.DoNothing
                        }
                    }

                    "copy the other app action to the other sources when turned off" {
                        withManager { manager, _ ->
                            manager.setActionAfterCleanPerSource(true)
                            manager.setActionAfterClean(
                                InputSource.OtherApp,
                                ActionAfterClean.OpenShareMenu,
                            )
                            manager.setActionAfterClean(
                                InputSource.Browser,
                                ActionAfterClean.OpenUrl,
                            )
                            manager.setActionAfterClean(
                                InputSource.InApp,
                                ActionAfterClean.CopyToClipboard,
                            )

                            manager.setActionAfterCleanPerSource(false)

                            manager.current() shouldBe
                                ActionsAfterClean(
                                    otherApp = ActionAfterClean.OpenShareMenu,
                                    browser = ActionAfterClean.OpenShareMenu,
                                    inApp = ActionAfterClean.OpenShareMenu,
                                    perSource = false,
                                )
                        }
                    }

                    "copy a legacy value to the other sources when turned off" {
                        withManager { manager, dataStore ->
                            dataStore.edit { it[legacyKey] = "OpenUrl" }
                            manager.setActionAfterClean(
                                InputSource.Browser,
                                ActionAfterClean.DoNothing,
                            )

                            manager.setActionAfterCleanPerSource(false)

                            manager.current().browser shouldBe ActionAfterClean.OpenUrl
                        }
                    }
                }
        },
    )
