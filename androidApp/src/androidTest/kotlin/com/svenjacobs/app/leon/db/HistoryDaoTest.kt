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

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: HistoryDao

    @Before
    fun setUp() {
        db =
            Room.inMemoryDatabaseBuilder<AppDatabase>(
                    ApplicationProvider.getApplicationContext<Context>()
                )
                .setDriver(AndroidSQLiteDriver())
                .build()
        dao = db.historyDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun recordingTwoIdsPutsTheNewerFirst() = runTest {
        dao.record(id = "id-1", url = "https://example.com/a", at = 1_000L)
        dao.record(id = "id-2", url = "https://example.com/b", at = 2_000L)

        val entries = dao.entries().first()

        assertEquals(listOf("id-2", "id-1"), entries.map { it.id })
    }

    @Test
    fun recordingTheSameIdTwiceUpdatesTheUrlAndKeepsTheOriginalAt() = runTest {
        dao.record(id = "id-1", url = "https://example.com/a", at = 1_000L)
        dao.record(id = "id-1", url = "https://example.com/a-edited", at = 2_000L)

        val entries = dao.entries().first()

        assertEquals(1, entries.size)
        assertEquals("https://example.com/a-edited", entries.first().url)
        assertEquals(1_000L, entries.first().at)
    }

    @Test
    fun recordingElevenIdsLeavesTenWithTheOldestGone() = runTest {
        repeat(11) { i ->
            dao.record(id = "id-$i", url = "https://example.com/$i", at = i.toLong())
        }

        val entries = dao.entries().first()

        assertEquals(HISTORY_MAX_SIZE, entries.size)
        assertEquals(false, entries.any { it.id == "id-0" })
        assertEquals(true, entries.any { it.id == "id-10" })
    }
}
