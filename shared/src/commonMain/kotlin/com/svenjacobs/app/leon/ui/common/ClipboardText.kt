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

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.Clipboard

// There is no common way to construct a ClipEntry (CMP-7624), hence this expect/actual pair
// rather than a shared implementation on top of Clipboard.setClipEntry/getClipEntry.
expect suspend fun Clipboard.readText(): String?

expect suspend fun Clipboard.writeText(text: String)

suspend fun copyToClipboard(
    clipboard: Clipboard,
    snackbarHostState: SnackbarHostState,
    text: String,
    message: String,
) {
    clipboard.writeText(text)
    snackbarHostState.showSnackbar(message)
}
