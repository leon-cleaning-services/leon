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
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class HistoryDaoTest :
    WordSpec({
        lateinit var db: AppDatabase
        lateinit var dao: HistoryDao

        beforeEach {
            db =
                Room.inMemoryDatabaseBuilder<AppDatabase>()
                    .setDriver(BundledSQLiteDriver())
                    .setQueryCoroutineContext(Dispatchers.IO)
                    .build()
            dao = db.historyDao()
        }

        afterEach { db.close() }

        "record" should
            {
                "insert a new entry for a new id" {
                    runTest {
                        dao.record(id = "id-1", url = "https://example.com/a", at = 1_000L)

                        val entries = dao.entries().first()

                        entries.map { it.id } shouldBe listOf("id-1")
                    }
                }

                "update the url in place for a known id and keep the original at" {
                    runTest {
                        dao.record(id = "id-1", url = "https://example.com/a", at = 1_000L)
                        dao.record(id = "id-1", url = "https://example.com/a-edited", at = 2_000L)

                        val entries = dao.entries().first()

                        entries.size shouldBe 1
                        entries.first().url shouldBe "https://example.com/a-edited"
                        entries.first().at shouldBe 1_000L
                    }
                }

                "trim to HISTORY_MAX_SIZE oldest-first" {
                    runTest {
                        repeat(HISTORY_MAX_SIZE + 1) { i ->
                            dao.record(
                                id = "id-$i",
                                url = "https://example.com/$i",
                                at = i.toLong(),
                            )
                        }

                        val entries = dao.entries().first()

                        entries.size shouldBe HISTORY_MAX_SIZE
                        entries.any { it.id == "id-0" } shouldBe false
                        entries.any { it.id == "id-$HISTORY_MAX_SIZE" } shouldBe true
                    }
                }
            }

        "delete" should
            {
                "remove only the entry with the given id" {
                    runTest {
                        dao.record(id = "id-1", url = "https://example.com/a", at = 1_000L)
                        dao.record(id = "id-2", url = "https://example.com/b", at = 2_000L)

                        dao.delete("id-1")

                        dao.entries().first().map { it.id } shouldBe listOf("id-2")
                    }
                }
            }

        "clear" should
            {
                "remove every entry" {
                    runTest {
                        dao.record(id = "id-1", url = "https://example.com/a", at = 1_000L)
                        dao.record(id = "id-2", url = "https://example.com/b", at = 2_000L)

                        dao.clear()

                        dao.entries().first() shouldBe emptyList()
                    }
                }
            }

        "entries" should
            {
                "order by at descending, newest first" {
                    runTest {
                        dao.record(id = "id-1", url = "https://example.com/a", at = 1_000L)
                        dao.record(id = "id-2", url = "https://example.com/b", at = 2_000L)

                        dao.entries().first().map { it.id } shouldBe listOf("id-2", "id-1")
                    }
                }
            }
    })
