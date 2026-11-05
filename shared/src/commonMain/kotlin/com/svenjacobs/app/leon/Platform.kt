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
package com.svenjacobs.app.leon

import com.svenjacobs.app.leon.ui.model.Browser

/** Platform specific capabilities and actions that have no common implementation. */
interface Platform {

    /** Whether this platform can register the app as a browser (open supported links). */
    val supportsBrowserRegistration: Boolean

    /** Whether this platform can open links in a Custom Tab rather than the full browser. */
    val supportsCustomTabs: Boolean

    /** Whether this platform can hide the app's content from the recent apps screen. */
    val supportsProtectScreen: Boolean

    /** Whether this platform can open links in a browser of the user's choice. */
    val supportsBrowserSelection: Boolean

    /** Whether the app is currently registered as a browser. */
    fun isRegisteredAsBrowser(): Boolean

    /** Registers, or unregisters, the app as a browser. */
    fun setRegisteredAsBrowser(enabled: Boolean)

    /**
     * Whether the app is currently the default browser, so that opening a link with the default
     * browser would just reopen the app.
     */
    fun isDefaultBrowser(): Boolean

    /** The installed browsers other than the app itself, sorted by their label. */
    suspend fun browsers(): List<Browser>
}
