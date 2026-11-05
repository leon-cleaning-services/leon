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
 * An installed browser which Léon can open links with.
 *
 * @param packageName Identifies the browser; it is what the user's choice is stored as.
 * @param label The browser's name, as the user knows it.
 * @param supportsCustomTabs Whether the browser can open links in a Custom Tab. One that cannot
 *   opens them in the browser app instead, even while Custom Tabs are turned on.
 */
data class Browser(val packageName: String, val label: String, val supportsCustomTabs: Boolean)
