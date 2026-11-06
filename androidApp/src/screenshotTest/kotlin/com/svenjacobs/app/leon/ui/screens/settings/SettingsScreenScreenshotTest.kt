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

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import com.svenjacobs.app.leon.ui.model.AutoReset
import com.svenjacobs.app.leon.ui.model.Browser
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.FormFactorPreviews
import kotlinx.collections.immutable.persistentListOf

/** A browser without Custom Tabs support, made up so as to claim nothing about a real one. */
private val BrowserWithoutCustomTabs =
    Browser(
        packageName = "com.example.browser",
        label = "Example Browser",
        supportsCustomTabs = false,
    )

@Composable
private fun ScreenshotContent(selectedBrowser: Browser?, customTabsEnabled: Boolean) {
    AppTheme {
        Content(
            isLoading = false,
            browserEnabled = false,
            customTabsEnabled = customTabsEnabled,
            protectScreenEnabled = false,
            historyEnabled = true,
            resolveShortLinksEnabled = false,
            shortenLinksEnabled = false,
            rememberPerSiteEnabled = true,
            autoReset = AutoReset.Off,
            browsers =
                persistentListOf(
                    Browser(
                        packageName = "com.android.chrome",
                        label = "Chrome",
                        supportsCustomTabs = true,
                    ),
                    BrowserWithoutCustomTabs,
                    Browser(
                        packageName = "org.mozilla.firefox",
                        label = "Firefox",
                        supportsCustomTabs = true,
                    ),
                ),
            selectedBrowser = selectedBrowser,
            isDefaultBrowser = false,
            isBrowserDialogShown = false,
            onActionAfterCleanClick = {},
            onSanitizersClick = {},
            onLicensesClick = {},
            onRememberedSitesClick = {},
            onBrowserSwitchCheckedChange = {},
            onBrowserClick = {},
            onBrowserSelect = {},
            onBrowserDialogDismiss = {},
            onCustomTabsSwitchCheckedChange = {},
            onProtectScreenSwitchCheckedChange = {},
            onHistorySwitchCheckedChange = {},
            onResolveShortLinksSwitchCheckedChange = {},
            onShortenLinksSwitchCheckedChange = {},
            onRememberPerSiteSwitchCheckedChange = {},
            onAutoResetClick = {},
        )
    }
}

@PreviewTest
@FormFactorPreviews
@Composable
private fun ContentScreenshot() {
    ScreenshotContent(selectedBrowser = null, customTabsEnabled = false)
}

@PreviewTest
@FormFactorPreviews
@Composable
private fun ContentCustomTabsUnsupportedScreenshot() {
    ScreenshotContent(selectedBrowser = BrowserWithoutCustomTabs, customTabsEnabled = true)
}
