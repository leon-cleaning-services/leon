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
package com.svenjacobs.app.leon.db

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.svenjacobs.app.leon.core.domain.preference.SitePreference
import com.svenjacobs.app.leon.db.SiteParameter.Decision
import com.svenjacobs.app.leon.preference.SitePreferenceStore
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class SiteParameterDaoTest :
    WordSpec(
        {
            lateinit var db: AppDatabase
            lateinit var dao: SiteParameterDao

            beforeEach {
                db =
                    Room.inMemoryDatabaseBuilder<AppDatabase>()
                        .setDriver(BundledSQLiteDriver())
                        .setQueryCoroutineContext(Dispatchers.IO)
                        .build()
                dao = db.siteParameterDao()
            }

            afterEach { db.close() }

            "upsert" should
                {
                    "replace the decision about the same parameter on the same site" {
                        runTest {
                            dao.upsert(SiteParameter("example.com", "ref", Decision.REMOVE))
                            dao.upsert(SiteParameter("example.com", "ref", Decision.KEEP))

                            dao.entries().first() shouldBe
                                listOf(SiteParameter("example.com", "ref", Decision.KEEP))
                        }
                    }

                    "keep the same parameter apart on different sites" {
                        runTest {
                            dao.upsert(SiteParameter("example.com", "ref", Decision.REMOVE))
                            dao.upsert(SiteParameter("other.com", "ref", Decision.KEEP))

                            dao.entries().first().size shouldBe 2
                        }
                    }
                }

            "delete" should
                {
                    "remove only that parameter of that site" {
                        runTest {
                            dao.upsert(SiteParameter("example.com", "a", Decision.REMOVE))
                            dao.upsert(SiteParameter("example.com", "b", Decision.REMOVE))
                            dao.upsert(SiteParameter("other.com", "a", Decision.REMOVE))

                            dao.delete("example.com", "a")

                            dao.entries().first() shouldBe
                                listOf(
                                    SiteParameter("example.com", "b", Decision.REMOVE),
                                    SiteParameter("other.com", "a", Decision.REMOVE),
                                )
                        }
                    }

                    "remove a whole site" {
                        runTest {
                            dao.upsert(SiteParameter("example.com", "a", Decision.REMOVE))
                            dao.upsert(SiteParameter("example.com", "b", Decision.KEEP))
                            dao.upsert(SiteParameter("other.com", "a", Decision.REMOVE))

                            dao.deleteSite("example.com")

                            dao.entries().first() shouldBe
                                listOf(SiteParameter("other.com", "a", Decision.REMOVE))
                        }
                    }
                }

            "SitePreferenceStore" should
                {
                    "group the decisions by site" {
                        runTest {
                            val store = SitePreferenceStore(dao)

                            store.remove("example.com", "a")
                            store.remove("example.com", "b")
                            store.keep("example.com", "c")
                            store.keep("other.com", "a")
                            store.forget("example.com", "b")

                            store.preferences.first() shouldBe
                                mapOf(
                                    "example.com" to
                                        SitePreference(
                                            remove = persistentSetOf("a"),
                                            keep = persistentSetOf("c"),
                                        ),
                                    "other.com" to SitePreference(keep = persistentSetOf("a")),
                                )
                        }
                    }
                }
        },
    )
