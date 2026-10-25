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

/** Where the text to clean came from. Each source can have its own action after clean. */
enum class InputSource {
    /**
     * Another app: text shared with Léon, text selected in another app (read-only `PROCESS_TEXT`)
     * and, on desktop, command line arguments.
     */
    OtherApp,

    /** A link opened from a browser or any other app that views URLs (`ACTION_VIEW`). */
    Browser,

    /** Text entered in Léon itself: typed into the input field or imported from the clipboard. */
    InApp,
}
