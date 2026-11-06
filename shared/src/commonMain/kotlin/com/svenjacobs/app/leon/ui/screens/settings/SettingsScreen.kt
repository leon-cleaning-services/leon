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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.about
import com.svenjacobs.app.leon.shared.resources.action_after_clean
import com.svenjacobs.app.leon.shared.resources.auto_reset_after
import com.svenjacobs.app.leon.shared.resources.custom_tabs_unsupported
import com.svenjacobs.app.leon.shared.resources.default_browser
import com.svenjacobs.app.leon.shared.resources.history_enabled
import com.svenjacobs.app.leon.shared.resources.history_enabled_summary
import com.svenjacobs.app.leon.shared.resources.licenses
import com.svenjacobs.app.leon.shared.resources.open_in_custom_tabs
import com.svenjacobs.app.leon.shared.resources.open_in_custom_tabs_summary
import com.svenjacobs.app.leon.shared.resources.open_links_with
import com.svenjacobs.app.leon.shared.resources.protect_screen
import com.svenjacobs.app.leon.shared.resources.protect_screen_summary
import com.svenjacobs.app.leon.shared.resources.register_as_browser
import com.svenjacobs.app.leon.shared.resources.register_as_browser_summary
import com.svenjacobs.app.leon.shared.resources.remember_per_site
import com.svenjacobs.app.leon.shared.resources.remember_per_site_summary
import com.svenjacobs.app.leon.shared.resources.remembered_sites
import com.svenjacobs.app.leon.shared.resources.resolve_short_links
import com.svenjacobs.app.leon.shared.resources.resolve_short_links_summary
import com.svenjacobs.app.leon.shared.resources.sanitizers
import com.svenjacobs.app.leon.shared.resources.shorten_links
import com.svenjacobs.app.leon.shared.resources.shorten_links_summary
import com.svenjacobs.app.leon.ui.common.rememberUrlActions
import com.svenjacobs.app.leon.ui.common.views.SwitchRow
import com.svenjacobs.app.leon.ui.model.AutoReset
import com.svenjacobs.app.leon.ui.model.Browser
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsScreenViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    onNavigateToSettingsActionAfterClean: () -> Unit,
    onNavigateToSettingsSanitizers: () -> Unit,
    onNavigateToSettingsLicenses: () -> Unit,
    onNavigateToSettingsRememberedSites: () -> Unit,
    chooseBrowserRequested: Boolean,
    onChooseBrowserRequestHandled: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Another screen sent the user here to choose a browser, because opening a link would have
    // reopened Léon.
    LaunchedEffect(chooseBrowserRequested) {
        if (chooseBrowserRequested) {
            viewModel.onBrowserClick()
            onChooseBrowserRequestHandled()
        }
    }

    Content(
        modifier = modifier,
        isLoading = uiState.isLoading,
        browserEnabled = uiState.browserEnabled,
        customTabsEnabled = uiState.customTabsEnabled,
        protectScreenEnabled = uiState.protectScreenEnabled,
        historyEnabled = uiState.historyEnabled,
        resolveShortLinksEnabled = uiState.resolveShortLinksEnabled,
        shortenLinksEnabled = uiState.shortenLinksEnabled,
        rememberPerSiteEnabled = uiState.rememberPerSiteEnabled,
        autoReset = uiState.autoReset,
        browsers = uiState.browsers,
        selectedBrowser = uiState.selectedBrowser,
        isDefaultBrowser = uiState.isDefaultBrowser,
        isBrowserDialogShown = uiState.isBrowserDialogShown,
        supportsBrowserRegistration = uiState.supportsBrowserRegistration,
        supportsBrowserSelection = uiState.supportsBrowserSelection,
        supportsCustomTabs = uiState.supportsCustomTabs,
        supportsProtectScreen = uiState.supportsProtectScreen,
        onActionAfterCleanClick = onNavigateToSettingsActionAfterClean,
        onSanitizersClick = onNavigateToSettingsSanitizers,
        onLicensesClick = onNavigateToSettingsLicenses,
        onRememberedSitesClick = onNavigateToSettingsRememberedSites,
        onBrowserSwitchCheckedChange = viewModel::onBrowserSwitchCheckedChange,
        onBrowserClick = viewModel::onBrowserClick,
        onBrowserSelect = viewModel::onBrowserSelect,
        onBrowserDialogDismiss = viewModel::onBrowserDialogDismiss,
        onCustomTabsSwitchCheckedChange = viewModel::onCustomTabsSwitchCheckedChange,
        onProtectScreenSwitchCheckedChange = viewModel::onProtectScreenSwitchCheckedChange,
        onHistorySwitchCheckedChange = viewModel::onHistorySwitchCheckedChange,
        onResolveShortLinksSwitchCheckedChange = viewModel::onResolveShortLinksSwitchCheckedChange,
        onShortenLinksSwitchCheckedChange = viewModel::onShortenLinksSwitchCheckedChange,
        onRememberPerSiteSwitchCheckedChange = viewModel::onRememberPerSiteSwitchCheckedChange,
        onAutoResetClick = viewModel::onAutoResetClick,
    )
}

@Composable
fun Content(
    isLoading: Boolean,
    browserEnabled: Boolean,
    customTabsEnabled: Boolean,
    protectScreenEnabled: Boolean,
    historyEnabled: Boolean,
    resolveShortLinksEnabled: Boolean,
    shortenLinksEnabled: Boolean,
    rememberPerSiteEnabled: Boolean,
    autoReset: AutoReset,
    browsers: ImmutableList<Browser>,
    selectedBrowser: Browser?,
    isDefaultBrowser: Boolean,
    isBrowserDialogShown: Boolean,
    onActionAfterCleanClick: () -> Unit,
    onSanitizersClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onRememberedSitesClick: () -> Unit,
    onBrowserSwitchCheckedChange: (Boolean) -> Unit,
    onBrowserClick: () -> Unit,
    onBrowserSelect: (Browser?) -> Unit,
    onBrowserDialogDismiss: () -> Unit,
    onCustomTabsSwitchCheckedChange: (Boolean) -> Unit,
    onProtectScreenSwitchCheckedChange: (Boolean) -> Unit,
    onHistorySwitchCheckedChange: (Boolean) -> Unit,
    onResolveShortLinksSwitchCheckedChange: (Boolean) -> Unit,
    onShortenLinksSwitchCheckedChange: (Boolean) -> Unit,
    onRememberPerSiteSwitchCheckedChange: (Boolean) -> Unit,
    onAutoResetClick: (AutoReset) -> Unit,
    modifier: Modifier = Modifier,
    supportsBrowserRegistration: Boolean = true,
    supportsBrowserSelection: Boolean = true,
    supportsCustomTabs: Boolean = true,
    supportsProtectScreen: Boolean = true,
) {
    val urlActions = rememberUrlActions()
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }
    var showAutoResetDialog by rememberSaveable { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }

    if (showAutoResetDialog) {
        AutoResetDialog(
            selected = autoReset,
            onSelect = {
                showAutoResetDialog = false
                onAutoResetClick(it)
            },
            onDismissRequest = { showAutoResetDialog = false },
        )
    }

    if (isBrowserDialogShown) {
        BrowserDialog(
            browsers = browsers,
            selectedBrowser = selectedBrowser,
            isDefaultBrowser = isDefaultBrowser,
            onSelect = onBrowserSelect,
            onDismissRequest = onBrowserDialogDismiss,
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                if (supportsBrowserSelection) {
                    ClickableRow(
                        text = stringResource(Res.string.open_links_with),
                        supportingText =
                            selectedBrowser?.label ?: stringResource(Res.string.default_browser),
                        onClick = onBrowserClick,
                    )
                }

                if (supportsCustomTabs) {
                    SwitchRow(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(Res.string.open_in_custom_tabs),
                        supportingText = stringResource(Res.string.open_in_custom_tabs_summary),
                        checked = customTabsEnabled,
                        onCheckedChange = onCustomTabsSwitchCheckedChange,
                        // Custom Tabs open in the browser links are opened with; while that would
                        // be Léon itself, the switch has nothing to apply to.
                        enabled = selectedBrowser != null || !urlActions.isDefaultBrowser,
                    )

                    if (selectedBrowser != null && !selectedBrowser.supportsCustomTabs) {
                        CustomTabsUnsupportedNote(
                            modifier = Modifier.padding(top = 8.dp),
                            browserLabel = selectedBrowser.label,
                        )
                    }
                }

                if (supportsBrowserRegistration) {
                    SwitchRow(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(Res.string.register_as_browser),
                        supportingText = stringResource(Res.string.register_as_browser_summary),
                        checked = browserEnabled,
                        onCheckedChange = onBrowserSwitchCheckedChange,
                    )
                }

                if (supportsProtectScreen) {
                    SwitchRow(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(Res.string.protect_screen),
                        supportingText = stringResource(Res.string.protect_screen_summary),
                        checked = protectScreenEnabled,
                        onCheckedChange = onProtectScreenSwitchCheckedChange,
                    )
                }

                SwitchRow(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.history_enabled),
                    supportingText = stringResource(Res.string.history_enabled_summary),
                    checked = historyEnabled,
                    onCheckedChange = onHistorySwitchCheckedChange,
                )

                SwitchRow(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.resolve_short_links),
                    supportingText = stringResource(Res.string.resolve_short_links_summary),
                    checked = resolveShortLinksEnabled,
                    onCheckedChange = onResolveShortLinksSwitchCheckedChange,
                )

                SwitchRow(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.shorten_links),
                    supportingText = stringResource(Res.string.shorten_links_summary),
                    checked = shortenLinksEnabled,
                    onCheckedChange = onShortenLinksSwitchCheckedChange,
                )

                SwitchRow(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.remember_per_site),
                    supportingText = stringResource(Res.string.remember_per_site_summary),
                    checked = rememberPerSiteEnabled,
                    onCheckedChange = onRememberPerSiteSwitchCheckedChange,
                )

                ClickableRow(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.auto_reset_after),
                    supportingText = autoReset.text(),
                    onClick = { showAutoResetDialog = true },
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = onActionAfterCleanClick,
                ) {
                    Text(stringResource(Res.string.action_after_clean))
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = onSanitizersClick,
                ) {
                    Text(stringResource(Res.string.sanitizers))
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = onRememberedSitesClick,
                ) {
                    Text(stringResource(Res.string.remembered_sites))
                }

                // Sets the settings apart from the information about the app below.
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = onLicensesClick,
                ) {
                    Text(stringResource(Res.string.licenses))
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    onClick = { showAboutDialog = true },
                ) {
                    Text(stringResource(Res.string.about))
                }
            }
        }
    }
}

/**
 * Explains that the chosen browser cannot open Custom Tabs, so that links open in the browser app
 * whatever the switch above says.
 */
@Composable
private fun CustomTabsUnsupportedNote(browserLabel: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
        )
        Text(
            text = stringResource(Res.string.custom_tabs_unsupported, browserLabel),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}
