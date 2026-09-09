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
package com.svenjacobs.app.leon.ui.screens.history

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.android.tools.screenshot.PreviewTest
import com.svenjacobs.app.leon.db.HistoryEntry
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.FormFactorPreviews
import kotlinx.collections.immutable.persistentListOf

@PreviewTest
@FormFactorPreviews
@Composable
private fun ContentScreenshot() {
    AppTheme {
        Content(
            isEnabled = true,
            isCustomTabsEnabled = false,
            entries =
                persistentListOf(
                    // Fixed timestamps, not `System.currentTimeMillis()`: a screenshot test
                    // renders the formatted date/time into the reference image, so a moving
                    // clock would make the test flaky whenever a minute ticks over between
                    // recording and validating.
                    HistoryEntry(
                        id = "1",
                        url = "https://www.example.com/path?keep=123",
                        at = 1_700_000_000_000L,
                    ),
                    HistoryEntry(
                        id = "2",
                        url = "https://www.example.org/another/path?keep=456",
                        at = 1_700_000_000_000L - 3_600_000,
                    ),
                ),
            snackbarHostState = remember { SnackbarHostState() },
            onDeleteClick = {},
            onUndoDeleteClick = {},
            onClearAllClick = {},
        )
    }
}

@PreviewTest
@FormFactorPreviews
@Composable
private fun ContentEmptyScreenshot() {
    AppTheme {
        Content(
            isEnabled = true,
            isCustomTabsEnabled = false,
            entries = persistentListOf(),
            snackbarHostState = remember { SnackbarHostState() },
            onDeleteClick = {},
            onUndoDeleteClick = {},
            onClearAllClick = {},
        )
    }
}
