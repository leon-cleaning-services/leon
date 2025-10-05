/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.svenjacobs.app.leon.BuildConfig
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.ui.common.isDefaultBrowser
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsScreenViewModel
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews

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
		actionAfterClean = uiState.actionAfterClean,
		onSanitizersClick = onNavigateToSettingsSanitizers,
		onLicensesClick = onNavigateToSettingsLicenses,
		onBrowserSwitchCheckedChange = viewModel::onBrowserSwitchCheckedChange,
		onCustomTabsSwitchCheckedChange = viewModel::onCustomTabsSwitchCheckedChange,
		onActionAfterCleanClick = viewModel::onActionAfterCleanClick,
	)
}

@Composable
private fun Content(
	isLoading: Boolean,
	browserEnabled: Boolean,
	customTabsEnabled: Boolean,
	actionAfterClean: ActionAfterClean,
	onSanitizersClick: () -> Unit,
	onLicensesClick: () -> Unit,
	onBrowserSwitchCheckedChange: (Boolean) -> Unit,
	onCustomTabsSwitchCheckedChange: (Boolean) -> Unit,
	onActionAfterCleanClick: (ActionAfterClean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier.fillMaxSize(),
	) {
		if (isLoading) {
			CircularProgressIndicator(
				modifier = Modifier.align(Alignment.Center),
			)
		} else {
			Column(
				modifier = Modifier.padding(16.dp),
			) {
				OutlinedButton(
					modifier = Modifier.fillMaxWidth(),
					onClick = onSanitizersClick,
				) {
					Text(stringResource(R.string.sanitizers))
				}

				OutlinedButton(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 16.dp),
					onClick = onLicensesClick,
				) {
					Text(stringResource(R.string.licenses))
				}

				SwitchRow(
					modifier = Modifier.padding(top = 16.dp),
					text = stringResource(R.string.register_as_browser),
					checked = browserEnabled,
					onCheckedChange = onBrowserSwitchCheckedChange,
				)

				SwitchRow(
					modifier = Modifier.padding(top = 16.dp),
					text = stringResource(R.string.open_in_custom_tabs),
					checked = customTabsEnabled,
					onCheckedChange = onCustomTabsSwitchCheckedChange,
					enabled = !isDefaultBrowser(LocalContext.current),
				)

				Column(
					modifier = Modifier.padding(top = 8.dp),
				) {
					var expanded by rememberSaveable { mutableStateOf(false) }

					Text(stringResource(R.string.action_after_clean))

					ExposedDropdownMenuBox(
						modifier = Modifier.padding(top = 8.dp),
						expanded = expanded,
						onExpandedChange = { expanded = !expanded },
					) {
						TextField(
							modifier = Modifier
								.fillMaxWidth()
								.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
							value = actionAfterClean.text(),
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
		}

		Text(
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.padding(
					bottom = 8.dp,
					end = 8.dp,
				),
			text = "v${BuildConfig.VERSION_NAME}",
			style = MaterialTheme.typography.bodySmall,
		)
	}
}

@Composable
private fun SwitchRow(
	text: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			modifier = Modifier
				.padding(end = 8.dp)
				.weight(1f),
			text = text,
		)

		Switch(
			checked = checked,
			onCheckedChange = onCheckedChange,
			enabled = enabled,
		)
	}
}

@Composable
private fun ActionAfterClean.text(): String = when (this) {
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
			actionAfterClean = ActionAfterClean.OpenShareMenu,
			onSanitizersClick = {},
			onLicensesClick = {},
			onBrowserSwitchCheckedChange = {},
			onCustomTabsSwitchCheckedChange = {},
			onActionAfterCleanClick = {},
		)
	}
}
