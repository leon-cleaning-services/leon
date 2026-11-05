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
package com.svenjacobs.app.leon.ui.common.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.cancel
import com.svenjacobs.app.leon.shared.resources.choose_browser
import com.svenjacobs.app.leon.shared.resources.reopen_leon_text
import com.svenjacobs.app.leon.shared.resources.reopen_leon_title
import org.jetbrains.compose.resources.stringResource

/**
 * Explains why a link was not opened: it would have gone to the default browser, which is Léon
 * itself. Offers to choose the browser links are opened with instead.
 *
 * @see com.svenjacobs.app.leon.ui.common.OpenResult.WouldReopenLeon
 */
@Composable
fun ReopenLeonDialog(
    onChooseBrowserClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.reopen_leon_title)) },
        text = { Text(stringResource(Res.string.reopen_leon_text)) },
        confirmButton = {
            TextButton(onClick = onChooseBrowserClick) {
                Text(stringResource(Res.string.choose_browser))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(Res.string.cancel)) }
        },
    )
}
