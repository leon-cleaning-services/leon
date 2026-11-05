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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.default_browser
import com.svenjacobs.app.leon.shared.resources.default_browser_unavailable
import com.svenjacobs.app.leon.shared.resources.open_links_with
import com.svenjacobs.app.leon.ui.common.rememberAppIcon
import com.svenjacobs.app.leon.ui.model.Browser
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * Lets the user choose the browser links are opened with: the default browser, which always comes
 * first, or any of the installed [browsers]. Choosing one closes the dialog.
 *
 * @param selectedBrowser The browser chosen so far, or `null` for the default browser.
 * @param isDefaultBrowser Whether Léon is the default browser. Opening a link with the default
 *   browser would then just reopen Léon, so it cannot be chosen.
 * @param onSelect Called with the chosen browser, `null` being the default browser.
 */
@Composable
fun BrowserDialog(
    browsers: ImmutableList<Browser>,
    selectedBrowser: Browser?,
    isDefaultBrowser: Boolean,
    onSelect: (Browser?) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChoiceDialog(
        modifier = modifier,
        title = stringResource(Res.string.open_links_with),
        onDismissRequest = onDismissRequest,
    ) {
        ChoiceRow(
            text = stringResource(Res.string.default_browser),
            supportingText =
                if (isDefaultBrowser) stringResource(Res.string.default_browser_unavailable)
                else null,
            selected = selectedBrowser == null,
            enabled = !isDefaultBrowser,
            onClick = { onSelect(null) },
            // Not one particular app, so a generic symbol rather than an app's icon.
            icon = { enabled ->
                Icon(
                    modifier = Modifier.size(ChoiceIconSize),
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = null,
                    tint = choiceContentColor(enabled),
                )
            },
        )

        browsers.forEach { browser ->
            ChoiceRow(
                text = browser.label,
                selected = browser.packageName == selectedBrowser?.packageName,
                onClick = { onSelect(browser) },
                icon = { AppIcon(packageName = browser.packageName) },
            )
        }
    }
}

/**
 * The icon of the browser [packageName]. The space stays empty while it loads, or when the browser
 * has been uninstalled since the list was made.
 */
@Composable
private fun AppIcon(packageName: String, modifier: Modifier = Modifier) {
    val icon = rememberAppIcon(packageName = packageName, size = ChoiceIconSize) ?: return

    Image(modifier = modifier.size(ChoiceIconSize), bitmap = icon, contentDescription = null)
}
