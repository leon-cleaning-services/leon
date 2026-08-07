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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import com.svenjacobs.app.leon.BuildConfig
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.ui.common.isDefaultBrowser
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsScreenViewModel
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews

private val ContentPadding = PaddingValues(16.dp)

@Composable
fun SettingsScreen(
    onNavigateToSettingsSanitizers: () -> Unit,
    onNavigateToSettingsLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        modifier = modifier,
        isLoading = uiState.isLoading,
        browserEnabled = uiState.browserEnabled,
        customTabsEnabled = uiState.customTabsEnabled,
        protectScreenEnabled = uiState.protectScreenEnabled,
        actionAfterClean = uiState.actionAfterClean,
        onSanitizersClick = onNavigateToSettingsSanitizers,
        onLicensesClick = onNavigateToSettingsLicenses,
        onBrowserSwitchCheckedChange = viewModel::onBrowserSwitchCheckedChange,
        onCustomTabsSwitchCheckedChange = viewModel::onCustomTabsSwitchCheckedChange,
        onProtectScreenSwitchCheckedChange = viewModel::onProtectScreenSwitchCheckedChange,
        onActionAfterCleanClick = viewModel::onActionAfterCleanClick,
    )
}

@Composable
private fun Content(
    isLoading: Boolean,
    browserEnabled: Boolean,
    customTabsEnabled: Boolean,
    protectScreenEnabled: Boolean,
    actionAfterClean: ActionAfterClean,
    onSanitizersClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onBrowserSwitchCheckedChange: (Boolean) -> Unit,
    onCustomTabsSwitchCheckedChange: (Boolean) -> Unit,
    onProtectScreenSwitchCheckedChange: (Boolean) -> Unit,
    onActionAfterCleanClick: (ActionAfterClean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutDialog(onDismissRequest = { showAboutDialog = false })
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    if (isWide) {
        WideLayout(
            modifier = modifier,
            browserEnabled = browserEnabled,
            customTabsEnabled = customTabsEnabled,
            protectScreenEnabled = protectScreenEnabled,
            actionAfterClean = actionAfterClean,
            onSanitizersClick = onSanitizersClick,
            onLicensesClick = onLicensesClick,
            onAboutClick = { showAboutDialog = true },
            onBrowserSwitchCheckedChange = onBrowserSwitchCheckedChange,
            onCustomTabsSwitchCheckedChange = onCustomTabsSwitchCheckedChange,
            onProtectScreenSwitchCheckedChange = onProtectScreenSwitchCheckedChange,
            onActionAfterCleanClick = onActionAfterCleanClick,
        )
    } else {
        CompactLayout(
            modifier = modifier,
            browserEnabled = browserEnabled,
            customTabsEnabled = customTabsEnabled,
            protectScreenEnabled = protectScreenEnabled,
            actionAfterClean = actionAfterClean,
            onSanitizersClick = onSanitizersClick,
            onLicensesClick = onLicensesClick,
            onAboutClick = { showAboutDialog = true },
            onBrowserSwitchCheckedChange = onBrowserSwitchCheckedChange,
            onCustomTabsSwitchCheckedChange = onCustomTabsSwitchCheckedChange,
            onProtectScreenSwitchCheckedChange = onProtectScreenSwitchCheckedChange,
            onActionAfterCleanClick = onActionAfterCleanClick,
        )
    }
}

@Composable
private fun CompactLayout(
    browserEnabled: Boolean,
    customTabsEnabled: Boolean,
    protectScreenEnabled: Boolean,
    actionAfterClean: ActionAfterClean,
    onSanitizersClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onAboutClick: () -> Unit,
    onBrowserSwitchCheckedChange: (Boolean) -> Unit,
    onCustomTabsSwitchCheckedChange: (Boolean) -> Unit,
    onProtectScreenSwitchCheckedChange: (Boolean) -> Unit,
    onActionAfterCleanClick: (ActionAfterClean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = ContentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            NavigationSection(
                onSanitizersClick = onSanitizersClick,
                onLicensesClick = onLicensesClick,
                onAboutClick = onAboutClick,
            )
        }

        item {
            SettingsSection(
                browserEnabled = browserEnabled,
                customTabsEnabled = customTabsEnabled,
                protectScreenEnabled = protectScreenEnabled,
                actionAfterClean = actionAfterClean,
                onBrowserSwitchCheckedChange = onBrowserSwitchCheckedChange,
                onCustomTabsSwitchCheckedChange = onCustomTabsSwitchCheckedChange,
                onProtectScreenSwitchCheckedChange = onProtectScreenSwitchCheckedChange,
                onActionAfterCleanClick = onActionAfterCleanClick,
            )
        }

        item { VersionText() }
    }
}

@Composable
private fun WideLayout(
    browserEnabled: Boolean,
    customTabsEnabled: Boolean,
    protectScreenEnabled: Boolean,
    actionAfterClean: ActionAfterClean,
    onSanitizersClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onAboutClick: () -> Unit,
    onBrowserSwitchCheckedChange: (Boolean) -> Unit,
    onCustomTabsSwitchCheckedChange: (Boolean) -> Unit,
    onProtectScreenSwitchCheckedChange: (Boolean) -> Unit,
    onActionAfterCleanClick: (ActionAfterClean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                NavigationSection(
                    onSanitizersClick = onSanitizersClick,
                    onLicensesClick = onLicensesClick,
                    onAboutClick = onAboutClick,
                )
            }

            item { VersionText() }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SettingsSection(
                    browserEnabled = browserEnabled,
                    customTabsEnabled = customTabsEnabled,
                    protectScreenEnabled = protectScreenEnabled,
                    actionAfterClean = actionAfterClean,
                    onBrowserSwitchCheckedChange = onBrowserSwitchCheckedChange,
                    onCustomTabsSwitchCheckedChange = onCustomTabsSwitchCheckedChange,
                    onProtectScreenSwitchCheckedChange = onProtectScreenSwitchCheckedChange,
                    onActionAfterCleanClick = onActionAfterCleanClick,
                )
            }
        }
    }
}

@Composable
private fun NavigationSection(
    onSanitizersClick: () -> Unit,
    onLicensesClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.sanitizers)) },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable(onClick = onSanitizersClick),
        )

        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.licenses)) },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable(onClick = onLicensesClick),
        )

        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.about)) },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable(onClick = onAboutClick),
        )
    }
}

@Composable
private fun SettingsSection(
    browserEnabled: Boolean,
    customTabsEnabled: Boolean,
    protectScreenEnabled: Boolean,
    actionAfterClean: ActionAfterClean,
    onBrowserSwitchCheckedChange: (Boolean) -> Unit,
    onCustomTabsSwitchCheckedChange: (Boolean) -> Unit,
    onProtectScreenSwitchCheckedChange: (Boolean) -> Unit,
    onActionAfterCleanClick: (ActionAfterClean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDefaultBrowser = isDefaultBrowser(LocalContext.current)

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(stringResource(R.string.register_as_browser)) },
            trailingContent = {
                Switch(checked = browserEnabled, onCheckedChange = onBrowserSwitchCheckedChange)
            },
        )

        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.open_in_custom_tabs)) },
            trailingContent = {
                Switch(
                    checked = customTabsEnabled,
                    onCheckedChange = onCustomTabsSwitchCheckedChange,
                    enabled = !isDefaultBrowser,
                )
            },
        )

        HorizontalDivider()

        ListItem(
            headlineContent = { Text(stringResource(R.string.protect_screen)) },
            trailingContent = {
                Switch(
                    checked = protectScreenEnabled,
                    onCheckedChange = onProtectScreenSwitchCheckedChange,
                )
            },
        )

        HorizontalDivider()

        ActionAfterCleanItem(
            actionAfterClean = actionAfterClean,
            onActionAfterCleanClick = onActionAfterCleanClick,
        )
    }
}

@Composable
private fun ActionAfterCleanItem(
    actionAfterClean: ActionAfterClean,
    onActionAfterCleanClick: (ActionAfterClean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = stringResource(R.string.action_after_clean),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        var expanded by rememberSaveable { mutableStateOf(false) }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                modifier =
                    Modifier.fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                value = actionAfterClean.text(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )

            ExposedDropdownMenu(
                modifier = Modifier.exposedDropdownSize(),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.do_nothing)) },
                    onClick = {
                        expanded = false
                        onActionAfterCleanClick(ActionAfterClean.DoNothing)
                    },
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.open_share_menu)) },
                    onClick = {
                        expanded = false
                        onActionAfterCleanClick(ActionAfterClean.OpenShareMenu)
                    },
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.open_url)) },
                    onClick = {
                        expanded = false
                        onActionAfterCleanClick(ActionAfterClean.OpenUrl)
                    },
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.copy_to_clipboard)) },
                    onClick = {
                        expanded = false
                        onActionAfterCleanClick(ActionAfterClean.CopyToClipboard)
                    },
                )
            }
        }
    }
}

@Composable
private fun VersionText(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        text = "v${BuildConfig.VERSION_NAME}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ActionAfterClean.text(): String =
    when (this) {
        ActionAfterClean.DoNothing -> stringResource(R.string.do_nothing)
        ActionAfterClean.OpenShareMenu -> stringResource(R.string.open_share_menu)
        ActionAfterClean.OpenUrl -> stringResource(R.string.open_url)
        ActionAfterClean.CopyToClipboard -> stringResource(R.string.copy_to_clipboard)
    }

@Composable
@DayNightPreviews
private fun ContentPreview() {
    AppTheme {
        Content(
            isLoading = false,
            browserEnabled = false,
            customTabsEnabled = false,
            protectScreenEnabled = false,
            actionAfterClean = ActionAfterClean.OpenShareMenu,
            onSanitizersClick = {},
            onLicensesClick = {},
            onBrowserSwitchCheckedChange = {},
            onCustomTabsSwitchCheckedChange = {},
            onProtectScreenSwitchCheckedChange = {},
            onActionAfterCleanClick = {},
        )
    }
}
