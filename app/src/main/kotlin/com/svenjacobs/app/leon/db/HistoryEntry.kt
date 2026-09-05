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

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntry(
    /** The id of the input this URL was cleaned from — see MainActivity's EXTRA_SOURCE_TEXT_ID. */
    @PrimaryKey val id: String,
    val url: String,
    /** When the URL was cleaned, epoch millis. */
    val at: Long,
)
