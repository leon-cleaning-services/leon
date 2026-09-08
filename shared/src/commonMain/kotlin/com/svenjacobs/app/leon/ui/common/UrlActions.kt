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
     * Whether Léon is currently the default browser. Opening a URL would just reopen Léon, so
     * callers should disable their "Open" action while this is `true`.
     */
    val isDefaultBrowser: Boolean

    /** Opens the platform's share sheet for [text]. */
    fun share(text: String, chooserTitle: String)

    /** Opens [url] in the browser, or in a Custom Tab when [customTabs] is `true`. */
    fun open(url: String, customTabs: Boolean, chooserTitle: String)
}

@Composable expect fun rememberUrlActions(): UrlActions
