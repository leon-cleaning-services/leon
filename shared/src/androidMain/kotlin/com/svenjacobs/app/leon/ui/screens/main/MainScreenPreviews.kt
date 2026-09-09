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
package com.svenjacobs.app.leon.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import com.svenjacobs.app.leon.ui.theme.AppTheme
import kotlinx.collections.immutable.persistentListOf

@Preview(showBackground = true)
@Composable
private fun SuccessBodyPreview() {
    AppTheme {
        SuccessBody(
            result =
                Result.Success(
                    originalText = "http://www.some.url?tracking=true",
                    cleanedText = "http://www.some.url",
                    urls = persistentListOf(),
                ),
            isUrlDecodeEnabled = false,
            isExtractUrlEnabled = false,
            onShareClick = {},
            onCopyToClipboardClick = {},
            onOpenClick = {},
            onResetClick = {},
            onUrlDecodeCheckedChange = {},
            onExtractUrlCheckedChange = {},
            onChangeToggled = { _, _ -> },
        )
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
private fun SuccessBodyWidePreview() {
    AppTheme {
        SuccessBody(
            result =
                Result.Success(
                    originalText = "http://www.some.url?tracking=true&utm_source=twitter",
                    cleanedText = "http://www.some.url",
                    urls = persistentListOf(),
                ),
            isUrlDecodeEnabled = false,
            isExtractUrlEnabled = false,
            onShareClick = {},
            onCopyToClipboardClick = {},
            onOpenClick = {},
            onResetClick = {},
            onUrlDecodeCheckedChange = {},
            onExtractUrlCheckedChange = {},
            onChangeToggled = { _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HowToBodyPreview() {
    AppTheme { HowToBody(onImportFromClipboardClick = {}, onSubmit = {}) }
}
