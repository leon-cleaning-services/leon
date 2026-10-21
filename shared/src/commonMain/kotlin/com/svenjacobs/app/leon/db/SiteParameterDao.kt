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
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteParameterDao {

    @Query("SELECT * FROM site_parameter ORDER BY site, parameter")
    fun entries(): Flow<List<SiteParameter>>

    /** Stores [entry], replacing whatever was decided about the same parameter on that site. */
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(entry: SiteParameter)

    @Query("DELETE FROM site_parameter WHERE site = :site AND parameter = :parameter")
    suspend fun delete(site: String, parameter: String)

    @Query("DELETE FROM site_parameter WHERE site = :site") suspend fun deleteSite(site: String)

    @Query("DELETE FROM site_parameter") suspend fun clear()
}
