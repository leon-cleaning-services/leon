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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import com.svenjacobs.app.leon.shared.resources.auto_reset_minutes
import com.svenjacobs.app.leon.shared.resources.auto_reset_off
import com.svenjacobs.app.leon.shared.resources.history_enabled
import com.svenjacobs.app.leon.shared.resources.history_enabled_summary
import com.svenjacobs.app.leon.shared.resources.licenses
import com.svenjacobs.app.leon.shared.resources.open_in_custom_tabs
import com.svenjacobs.app.leon.shared.resources.open_in_custom_tabs_summary
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
import com.svenjacobs.app.leon.ui.common.rememberUrlActions
import com.svenjacobs.app.leon.ui.common.views.SwitchRow
import com.svenjacobs.app.leon.ui.model.AutoReset
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsScreenViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    onNavigateToSettingsActionAfterClean: () -> Unit,
    onNavigateToSettingsSanitizers: () -> Unit,
    onNavigateToSettingsLicenses: () -> Unit,
    onNavigateToSettingsRememberedSites: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        modifier = modifier,
        isLoading = uiState.isLoading,
        browserEnabled = uiState.browserEnabled,
        customTabsEnabled = uiState.customTabsEnabled,
        protectScreenEnabled = uiState.protectScreenEnabled,
        historyEnabled = uiState.historyEnabled,
        resolveShortLinksEnabled = uiState.resolveShortLinksEnabled,
        rememberPerSiteEnabled = uiState.rememberPerSiteEnabled,
        autoReset = uiState.autoReset,
        supportsBrowserRegistration = uiState.supportsBrowserRegistration,
        supportsCustomTabs = uiState.supportsCustomTabs,
        supportsProtectScreen = uiState.supportsProtectScreen,
        onActionAfterCleanClick = onNavigateToSettingsActionAfterClean,
        onSanitizersClick = onNavigateToSettingsSanitizers,
        onLicensesClick = onNavigateToSettingsLicenses,
        onRememberedSitesClick = onNavigateToSettingsRememberedSites,
        onBrowserSwitchCheckedChange = viewModel::onBrowserSwitchCheckedChange,
        onCustomTabsSwitchCheckedChange = viewModel::onCustomTabsSwitchCheckedChange,
        onProtectScreenSwitchCheckedChange = viewModel::onProtectScreenSwitchCheckedChange,
        onHistorySwitchCheckedChange = viewModel::onHistorySwitchCheckedChange,
        onResolveShortLinksSwitchCheckedChange = viewModel::onResolveShortLinksSwitchCheckedChange,
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
    rememberPerSiteEnabled: Boolean,
    autoReset: AutoReset,
    onActionAfterCleanClick: () -> Unit,
    onSanitizersClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onRememberedSitesClick: () -> Unit,
    onBrowserSwitchCheckedChange: (Boolean) -> Unit,
    onCustomTabsSwitchCheckedChange: (Boolean) -> Unit,
    onProtectScreenSwitchCheckedChange: (Boolean) -> Unit,
    onHistorySwitchCheckedChange: (Boolean) -> Unit,
    onResolveShortLinksSwitchCheckedChange: (Boolean) -> Unit,
    onRememberPerSiteSwitchCheckedChange: (Boolean) -> Unit,
    onAutoResetClick: (AutoReset) -> Unit,
    modifier: Modifier = Modifier,
    supportsBrowserRegistration: Boolean = true,
    supportsCustomTabs: Boolean = true,
    supportsProtectScreen: Boolean = true,
) {
    val urlActions = rememberUrlActions()
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                if (supportsBrowserRegistration) {
                    SwitchRow(
                        text = stringResource(Res.string.register_as_browser),
                        supportingText = stringResource(Res.string.register_as_browser_summary),
                        checked = browserEnabled,
                        onCheckedChange = onBrowserSwitchCheckedChange,
                    )
                }

                if (supportsCustomTabs) {
                    SwitchRow(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(Res.string.open_in_custom_tabs),
                        supportingText = stringResource(Res.string.open_in_custom_tabs_summary),
                        checked = customTabsEnabled,
                        onCheckedChange = onCustomTabsSwitchCheckedChange,
                        enabled = !urlActions.isDefaultBrowser,
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
                    text = stringResource(Res.string.remember_per_site),
                    supportingText = stringResource(Res.string.remember_per_site_summary),
                    checked = rememberPerSiteEnabled,
                    onCheckedChange = onRememberPerSiteSwitchCheckedChange,
                )

                Column(modifier = Modifier.padding(top = 8.dp)) {
                    var expanded by rememberSaveable { mutableStateOf(false) }

                    Text(stringResource(Res.string.auto_reset_after))

                    ExposedDropdownMenuBox(
                        modifier = Modifier.padding(top = 8.dp),
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        TextField(
                            modifier =
                                Modifier.fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            value = autoReset.text(),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )

                        ExposedDropdownMenu(
                            modifier = Modifier.exposedDropdownSize(),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(AutoReset.Off.text()) },
                                onClick = {
                                    expanded = false
                                    onAutoResetClick(AutoReset.Off)
                                },
                            )

                            DropdownMenuItem(
                                text = { Text(AutoReset.OneMinute.text()) },
                                onClick = {
                                    expanded = false
                                    onAutoResetClick(AutoReset.OneMinute)
                                },
                            )

                            DropdownMenuItem(
                                text = { Text(AutoReset.FiveMinutes.text()) },
                                onClick = {
                                    expanded = false
                                    onAutoResetClick(AutoReset.FiveMinutes)
                                },
                            )

                            DropdownMenuItem(
                                text = { Text(AutoReset.TenMinutes.text()) },
                                onClick = {
                                    expanded = false
                                    onAutoResetClick(AutoReset.TenMinutes)
                                },
                            )

                            DropdownMenuItem(
                                text = { Text(AutoReset.ThirtyMinutes.text()) },
                                onClick = {
                                    expanded = false
                                    onAutoResetClick(AutoReset.ThirtyMinutes)
                                },
                            )

                            DropdownMenuItem(
                                text = { Text(AutoReset.SixtyMinutes.text()) },
                                onClick = {
                                    expanded = false
                                    onAutoResetClick(AutoReset.SixtyMinutes)
                                },
                            )
                        }
                    }
                }

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

@Composable
private fun AutoReset.text(): String =
    when (val minutes = minutes) {
        null -> stringResource(Res.string.auto_reset_off)
        else -> stringResource(Res.string.auto_reset_minutes, minutes)
    }
