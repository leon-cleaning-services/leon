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
package com.svenjacobs.app.leon.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews

/** An ordinary, a short link and a shortening sanitizer, as the list in Settings shows them. */
@Composable
private fun SanitizerItems(resolveShortLinksEnabled: Boolean, shortenLinksEnabled: Boolean) {
    AppTheme {
        Card {
            Column {
                SanitizerItem(
                    name = "TikTok",
                    isEnabled = true,
                    resolvesShortLinks = false,
                    resolveShortLinksEnabled = resolveShortLinksEnabled,
                    shortens = false,
                    shortenLinksEnabled = shortenLinksEnabled,
                    onCheckedChange = {},
                )
                SanitizerItem(
                    name = "TikTok Short Link",
                    isEnabled = true,
                    resolvesShortLinks = true,
                    resolveShortLinksEnabled = resolveShortLinksEnabled,
                    shortens = false,
                    shortenLinksEnabled = shortenLinksEnabled,
                    onCheckedChange = {},
                )
                SanitizerItem(
                    name = "Shorten YouTube links",
                    isEnabled = true,
                    resolvesShortLinks = false,
                    resolveShortLinksEnabled = resolveShortLinksEnabled,
                    shortens = true,
                    shortenLinksEnabled = shortenLinksEnabled,
                    onCheckedChange = {},
                )
            }
        }
    }
}

@DayNightPreviews
@Composable
private fun SanitizerItemShortLinksOffPreview() {
    SanitizerItems(resolveShortLinksEnabled = false, shortenLinksEnabled = false)
}

@DayNightPreviews
@Composable
private fun SanitizerItemShortLinksOnPreview() {
    SanitizerItems(resolveShortLinksEnabled = true, shortenLinksEnabled = true)
}
