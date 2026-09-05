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

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry
import androidx.core.net.toUri

fun shareText(context: Context, text: String, chooserTitle: String) {
    val intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra(Intent.EXTRA_TEXT, text)
        }

    context.startActivity(Intent.createChooser(intent, chooserTitle))
}

/**
 * Returns without doing anything when Léon is the default browser — opening would just reopen Léon.
 */
fun openUrl(context: Context, url: String, customTabs: Boolean, chooserTitle: String) {
    if (isDefaultBrowser(context)) return

    if (customTabs) {
        val intent =
            CustomTabsIntent.Builder().setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM).build()

        intent.launchUrl(context, url.toUri())
    } else {
        val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return
        val intent = Intent(Intent.ACTION_VIEW, uri)

        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}

suspend fun copyToClipboard(
    clipboard: Clipboard,
    snackbarHostState: SnackbarHostState,
    text: String,
    message: String,
) {
    clipboard.setClipEntry(ClipData.newPlainText(text, text).toClipEntry())
    snackbarHostState.showSnackbar(message)
}
