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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.action_after_clean_browser
import com.svenjacobs.app.leon.shared.resources.action_after_clean_description
import com.svenjacobs.app.leon.shared.resources.action_after_clean_in_app
import com.svenjacobs.app.leon.shared.resources.action_after_clean_other_app
import com.svenjacobs.app.leon.shared.resources.action_after_clean_per_source
import com.svenjacobs.app.leon.shared.resources.action_after_clean_per_source_summary
import com.svenjacobs.app.leon.shared.resources.copy_to_clipboard
import com.svenjacobs.app.leon.shared.resources.do_nothing
import com.svenjacobs.app.leon.shared.resources.open_share_menu
import com.svenjacobs.app.leon.shared.resources.open_url
import com.svenjacobs.app.leon.ui.common.rememberUrlActions
import com.svenjacobs.app.leon.ui.common.views.SwitchRow
import com.svenjacobs.app.leon.ui.common.views.TopAppBar
import com.svenjacobs.app.leon.ui.model.ActionsAfterClean
import com.svenjacobs.app.leon.ui.model.InputSource
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsActionAfterCleanScreenViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActionAfterCleanScreen(
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: SettingsActionAfterCleanScreenViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        // Only attached while a top app bar is composed, see SettingsSanitizersScreen.
        modifier =
            if (onBackClick != null) {
                modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else {
                modifier
            },
        topBar = {
            if (onBackClick != null) {
                TopAppBar(onBackClick = onBackClick, scrollBehavior = scrollBehavior)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { contentPadding ->
        ActionAfterCleanContent(
            modifier = Modifier.padding(contentPadding),
            isLoading = uiState.isLoading,
            actionsAfterClean = uiState.actionsAfterClean,
            browserEnabled = uiState.browserEnabled,
            supportsBrowserRegistration = uiState.supportsBrowserRegistration,
            onActionAfterCleanClick = viewModel::onActionAfterCleanClick,
            onSourceActionAfterCleanClick = viewModel::onActionAfterCleanClick,
            onPerSourceCheckedChange = viewModel::onPerSourceCheckedChange,
        )
    }
}

@Composable
fun ActionAfterCleanContent(
    isLoading: Boolean,
    actionsAfterClean: ActionsAfterClean,
    browserEnabled: Boolean,
    supportsBrowserRegistration: Boolean,
    onActionAfterCleanClick: (ActionAfterClean) -> Unit,
    onSourceActionAfterCleanClick: (InputSource, ActionAfterClean) -> Unit,
    onPerSourceCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(Res.string.action_after_clean_description),
            style = MaterialTheme.typography.bodyLarge,
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
            )
            return@Column
        }

        if (!actionsAfterClean.perSource) {
            ActionAfterCleanDropdown(
                value = actionsAfterClean.otherApp,
                onClick = onActionAfterCleanClick,
            )
        }

        SwitchRow(
            modifier = Modifier.padding(top = if (actionsAfterClean.perSource) 0.dp else 16.dp),
            text = stringResource(Res.string.action_after_clean_per_source),
            supportingText = stringResource(Res.string.action_after_clean_per_source_summary),
            checked = actionsAfterClean.perSource,
            onCheckedChange = onPerSourceCheckedChange,
        )

        if (actionsAfterClean.perSource) {
            ActionAfterCleanDropdown(
                modifier = Modifier.padding(top = 16.dp),
                label = stringResource(Res.string.action_after_clean_other_app),
                value = actionsAfterClean.otherApp,
                onClick = { onSourceActionAfterCleanClick(InputSource.OtherApp, it) },
            )

            if (supportsBrowserRegistration && browserEnabled) {
                ActionAfterCleanDropdown(
                    modifier = Modifier.padding(top = 16.dp),
                    label = stringResource(Res.string.action_after_clean_browser),
                    value = actionsAfterClean.browser,
                    onClick = { onSourceActionAfterCleanClick(InputSource.Browser, it) },
                )
            }

            ActionAfterCleanDropdown(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                label = stringResource(Res.string.action_after_clean_in_app),
                value = actionsAfterClean.inApp,
                onClick = { onSourceActionAfterCleanClick(InputSource.InApp, it) },
            )
        }
    }
}

/** A dropdown for choosing an [ActionAfterClean], with [label] above it when given. */
@Composable
private fun ActionAfterCleanDropdown(
    value: ActionAfterClean,
    onClick: (ActionAfterClean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
) {
    val urlActions = rememberUrlActions()
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label != null) Text(label)

        ExposedDropdownMenuBox(
            modifier = if (label != null) Modifier.padding(top = 8.dp) else Modifier,
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                modifier =
                    Modifier.fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                value = value.text(),
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
                    text = { Text(stringResource(Res.string.do_nothing)) },
                    onClick = {
                        expanded = false
                        onClick(ActionAfterClean.DoNothing)
                    },
                )

                if (urlActions.canShare) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.open_share_menu)) },
                        onClick = {
                            expanded = false
                            onClick(ActionAfterClean.OpenShareMenu)
                        },
                    )
                }

                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.open_url)) },
                    onClick = {
                        expanded = false
                        onClick(ActionAfterClean.OpenUrl)
                    },
                )

                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.copy_to_clipboard)) },
                    onClick = {
                        expanded = false
                        onClick(ActionAfterClean.CopyToClipboard)
                    },
                )
            }
        }
    }
}

@Composable
private fun ActionAfterClean.text(): String =
    when (this) {
        ActionAfterClean.DoNothing -> stringResource(Res.string.do_nothing)
        ActionAfterClean.OpenShareMenu -> stringResource(Res.string.open_share_menu)
        ActionAfterClean.OpenUrl -> stringResource(Res.string.open_url)
        ActionAfterClean.CopyToClipboard -> stringResource(Res.string.copy_to_clipboard)
    }
