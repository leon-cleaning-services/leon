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

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

const val HISTORY_MAX_SIZE = 10

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY at DESC") fun entries(): Flow<List<HistoryEntry>>

    /**
     * Records [url] for the session [id]: updates the row in place while the user is still working
     * on the same shared text — ticking a change off must not push a second entry — and inserts a
     * new one otherwise. An updated row keeps its original [HistoryEntry.at]: that says when the
     * URL was cleaned, not when it was last edited.
     */
    @Transaction
    suspend fun record(id: String, url: String, at: Long, max: Int = HISTORY_MAX_SIZE) {
        if (updateUrl(id, url) == 0) {
            insert(HistoryEntry(id = id, url = url, at = at))
            trim(max)
        }
    }

    @Query("UPDATE history SET url = :url WHERE id = :id")
    suspend fun updateUrl(id: String, url: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(entry: HistoryEntry)

    @Query(
        "DELETE FROM history WHERE id NOT IN (SELECT id FROM history ORDER BY at DESC LIMIT :max)"
    )
    suspend fun trim(max: Int)

    @Query("DELETE FROM history WHERE id = :id") suspend fun delete(id: String)

    @Query("DELETE FROM history") suspend fun clear()
}
