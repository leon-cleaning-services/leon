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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.svenjacobs.app.leon.db.HistoryEntry
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews
import kotlinx.collections.immutable.persistentListOf

@Composable
@DayNightPreviews
private fun ContentPreview() {
    AppTheme {
        Content(
            isEnabled = true,
            isCustomTabsEnabled = false,
            entries =
                persistentListOf(
                    HistoryEntry(
                        id = "1",
                        url = "https://www.example.com/path?keep=123",
                        at = System.currentTimeMillis(),
                    ),
                    HistoryEntry(
                        id = "2",
                        url = "https://www.example.org/another/path?keep=456",
                        at = System.currentTimeMillis() - 3_600_000,
                    ),
                ),
            snackbarHostState = remember { SnackbarHostState() },
            onDeleteClick = {},
            onUndoDeleteClick = {},
            onClearAllClick = {},
        )
    }
}

@Composable
@DayNightPreviews
private fun ContentEmptyPreview() {
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

@Composable
@DayNightPreviews
private fun SwipeBackgroundPreview() {
    AppTheme {
        Column {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).height(80.dp)) {
                SwipeBackground(SwipeToDismissBoxValue.StartToEnd)
            }
            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                SwipeBackground(SwipeToDismissBoxValue.EndToStart)
            }
        }
    }
}
