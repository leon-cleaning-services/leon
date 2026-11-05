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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

private class AndroidUrlActions(private val context: Context) : UrlActions {

    override val canShare = true

    // Not cached: whether Léon is the default browser can change while the app is open (the user
    // may flip it in Settings), and every caller reads this fresh on each recomposition, exactly
    // as the old `isDefaultBrowser(LocalContext.current)` call site did.
    override val isDefaultBrowser: Boolean
        get() = context.isDefaultBrowser()

    override fun share(text: String, chooserTitle: String) {
        val intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                addCategory(Intent.CATEGORY_DEFAULT)
                putExtra(Intent.EXTRA_TEXT, text)
            }

        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }

    override fun open(
        url: String,
        customTabs: Boolean,
        browserPackage: String?,
        chooserTitle: String,
    ): OpenResult {
        if (browserPackage != null && openWith(browserPackage, url, customTabs)) {
            return OpenResult.Opened
        }
        if (context.isDefaultBrowser()) return OpenResult.WouldReopenLeon

        if (customTabs) {
            customTabsIntent().launchUrl(context, url.toUri())
        } else {
            val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return OpenResult.Opened
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val chooser =
                Intent.createChooser(intent, chooserTitle)
                    // While Léon is registered as a browser without being the default one, the
                    // chooser would offer Léon itself, which would just reopen it.
                    .putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, context.ownActivities(intent))

            context.startActivity(chooser)
        }

        return OpenResult.Opened
    }

    /**
     * Opens [url] with the browser [packageName] straight away, without a chooser. A Custom Tab is
     * a plain view intent with extras, so a browser without Custom Tabs support simply opens the
     * link in full. Returns `false` when the browser could not be started, usually because it is
     * not installed anymore.
     */
    private fun openWith(packageName: String, url: String, customTabs: Boolean): Boolean {
        val uri = runCatching { url.toUri() }.getOrNull() ?: return false
        val intent = if (customTabs) customTabsIntent().intent else Intent(Intent.ACTION_VIEW)
        intent.data = uri
        intent.setPackage(packageName)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        return runCatching { context.startActivity(intent) }.isSuccess
    }

    private fun customTabsIntent(): CustomTabsIntent =
        CustomTabsIntent.Builder().setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM).build()
}

@Composable
actual fun rememberUrlActions(): UrlActions {
    val context = LocalContext.current
    return remember(context) { AndroidUrlActions(context) }
}
