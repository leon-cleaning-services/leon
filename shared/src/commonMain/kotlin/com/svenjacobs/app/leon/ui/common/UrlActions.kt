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
package com.svenjacobs.app.leon.ui.common

import androidx.compose.runtime.Composable

/** Platform specific ways of sharing and opening a URL. */
interface UrlActions {

    /** Whether this platform can hand text off to another app. */
    val canShare: Boolean

    /**
     * Whether Léon is currently the default browser. Opening a URL with the default browser would
     * just reopen Léon.
     */
    val isDefaultBrowser: Boolean

    /** Opens the platform's share sheet for [text]. */
    fun share(text: String, chooserTitle: String)

    /**
     * Opens [url] in the browser, or in a Custom Tab when [customTabs] is `true`.
     *
     * @param browserPackage The browser the user chose, or `null` for the default browser. A
     *   browser that is gone by now falls back to the default browser.
     * @return [OpenResult.WouldReopenLeon], without opening anything, when the default browser had
     *   to be used and that is Léon itself. The caller should explain that to the user.
     */
    fun open(
        url: String,
        customTabs: Boolean,
        browserPackage: String?,
        chooserTitle: String,
    ): OpenResult
}

/** What became of a call to [UrlActions.open]. */
enum class OpenResult {
    /** The URL was handed to a browser, or the URL was malformed and silently dropped. */
    Opened,

    /**
     * Nothing was opened, because the URL would have gone to the default browser, which is Léon
     * itself — either no browser was chosen, or the chosen one is not installed anymore.
     */
    WouldReopenLeon,
}

@Composable expect fun rememberUrlActions(): UrlActions
