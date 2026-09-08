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
package com.svenjacobs.app.leon.ui.model

/**
 * How long after a URL was cleaned the main screen returns to its initial state.
 *
 * The names are persisted as DataStore values, so they must never be renamed.
 *
 * @param minutes `null` when auto-reset is disabled.
 */
enum class AutoReset(val minutes: Int?) {
    Off(null),
    OneMinute(1),
    FiveMinutes(5),
    TenMinutes(10),
    ThirtyMinutes(30),
    SixtyMinutes(60),
}
