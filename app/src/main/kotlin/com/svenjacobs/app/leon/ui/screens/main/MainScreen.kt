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

package com.svenjacobs.app.leon.ui.screens.main

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.view.Window
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.ui.common.isDefaultBrowser
import com.svenjacobs.app.leon.ui.common.views.TopAppBar
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import com.svenjacobs.app.leon.ui.screens.main.model.Screen
import com.svenjacobs.app.leon.ui.screens.main.views.BackgroundImage
import com.svenjacobs.app.leon.ui.screens.main.views.BottomBar
import com.svenjacobs.app.leon.ui.screens.settings.SettingsScreen
import com.svenjacobs.app.leon.ui.theme.AppTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
	sourceText: State<String?>,
	onNavigateToSettingsSanitizers: () -> Unit,
	onNavigateToSettingsLicenses: () -> Unit,
	onResetClick: () -> Unit,
	modifier: Modifier = Modifier,
	viewModel: MainScreenViewModel = viewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	val navController = rememberNavController()
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()
	val isDarkTheme = isSystemInDarkTheme()
	val context = LocalContext.current
	val clipboard = LocalClipboard.current
	val shareTitle = stringResource(R.string.share)
	val openTitle = stringResource(R.string.open)
	val copiedToClipboardMessage = stringResource(R.string.clipboard_message)
	val clipboardEmptyMessage = stringResource(R.string.clipboard_empty_message)
	var didPerformActionAfterClean by remember(uiState.result) { mutableStateOf(false) }
	val view = LocalView.current

	LaunchedEffect(sourceText.value) {
		viewModel.setText(sourceText.value)
	}

	LaunchedEffect(Unit) {
		val window = view.context.findWindow() ?: return@LaunchedEffect
		val insetsController = WindowCompat.getInsetsController(window, view)
		insetsController.isAppearanceLightStatusBars = !isDarkTheme
	}

	fun openShareMenu(result: Result.Success) {
		val intent = Intent(Intent.ACTION_SEND).apply {
			type = "text/plain"
			addCategory(Intent.CATEGORY_DEFAULT)
			putExtra(Intent.EXTRA_TEXT, result.cleanedText)
		}

		context.startActivity(
			Intent.createChooser(
				intent,
				shareTitle,
			),
		)
	}

	fun openInDefaultApp(result: Result.Success) {
		val uri = result.urls.firstOrNull()
			?.let { url -> runCatching { Uri.parse(url) }.getOrNull() } ?: return

		val intent = Intent(Intent.ACTION_VIEW, uri)

		context.startActivity(
			Intent.createChooser(
				intent,
				openTitle,
			),
		)
	}

	fun openInCustomTabs(result: Result.Success) {
		result.urls.firstOrNull()?.let { url ->
			val intent = CustomTabsIntent.Builder()
				.setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
				.build()

			intent.launchUrl(context, Uri.parse(url))
		}
	}

	fun openUrl(result: Result.Success) {
		// When Leon is the system default browser we neither can open the URL in custom tabs nor
		// in the default browser because this would just open Leon again.
		if (isDefaultBrowser(context)) return

		if (uiState.isCustomTabsEnabled) {
			openInCustomTabs(result)
		} else {
			openInDefaultApp(result)
		}
	}

	fun copyToClipboard(result: Result.Success) {
		coroutineScope.launch {
			clipboard.setClipEntry(
				ClipData.newPlainText(result.cleanedText, result.cleanedText).toClipEntry(),
			)
			snackbarHostState.showSnackbar(copiedToClipboardMessage)
		}
	}

	Scaffold(
		modifier = modifier,
		topBar = { TopAppBar() },
		bottomBar = { BottomBar(navController = navController) },
		snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
		content = { padding ->
			Box(
				modifier = Modifier.padding(padding),
			) {
				BackgroundImage()

				NavHost(
					navController = navController,
					startDestination = Screen.Main.route,
				) {
					composable(Screen.Main.route) {
						LaunchedEffect(uiState.result, uiState.actionAfterClean) {
							if (didPerformActionAfterClean) return@LaunchedEffect

							(uiState.result as? Result.Success)?.let { result ->
								when (uiState.actionAfterClean) {
									ActionAfterClean.OpenShareMenu -> openShareMenu(result)
									ActionAfterClean.OpenUrl -> openUrl(result)
									ActionAfterClean.CopyToClipboard -> copyToClipboard(result)
									ActionAfterClean.DoNothing -> {}
								}

								didPerformActionAfterClean = true
							}
						}

						Content(
							result = uiState.result,
							isUrlDecodeEnabled = uiState.isUrlDecodeEnabled,
							isExtractUrlEnabled = uiState.isExtractUrlEnabled,
							onImportFromClipboardClick = {
								coroutineScope.launch {
									val text =
										clipboard.getClipEntry()?.clipData?.getItemAt(0)?.text?.toString()

									if (text.isNullOrBlank()) {
										snackbarHostState.showSnackbar(clipboardEmptyMessage)
									} else {
										viewModel.setText(text)
									}
								}
							},
							onShareClick = ::openShareMenu,
							onCopyToClipboardClick = ::copyToClipboard,
							onOpenClick = ::openUrl,
							onResetClick = {
								viewModel.onResetClick()
								onResetClick()
							},
							onUrlDecodeCheckedChange = viewModel::onUrlDecodeCheckedChange,
							onExtractUrlCheckedChange = viewModel::onExtractUrlCheckedChange,
						)
					}

					composable(Screen.Settings.route) {
						SettingsScreen(
							onNavigateToSettingsSanitizers = onNavigateToSettingsSanitizers,
							onNavigateToSettingsLicenses = onNavigateToSettingsLicenses,
						)
					}
				}
			}
		},
	)
}

@Composable
private fun Content(
	result: Result,
	isUrlDecodeEnabled: Boolean,
	isExtractUrlEnabled: Boolean,
	onImportFromClipboardClick: () -> Unit,
	onShareClick: (Result.Success) -> Unit,
	onCopyToClipboardClick: (Result.Success) -> Unit,
	onOpenClick: (Result.Success) -> Unit,
	onResetClick: () -> Unit,
	onUrlDecodeCheckedChange: (Boolean) -> Unit,
	onExtractUrlCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier.verticalScroll(rememberScrollState()),
	) {
		Box(
			modifier = Modifier.padding(16.dp),
		) {
			Column(
				modifier = Modifier.fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				when (result) {
					is Result.Success -> SuccessBody(
						result = result,
						isUrlDecodeEnabled = isUrlDecodeEnabled,
						isExtractUrlEnabled = isExtractUrlEnabled,
						onShareClick = onShareClick,
						onCopyToClipboardClick = onCopyToClipboardClick,
						onOpenClick = onOpenClick,
						onResetClick = onResetClick,
						onUrlDecodeCheckedChange = onUrlDecodeCheckedChange,
						onExtractUrlCheckedChange = onExtractUrlCheckedChange,
					)

					else -> HowToBody(
						onImportFromClipboardClick = onImportFromClipboardClick,
					)
				}
			}
		}
	}
}

@Composable
private fun SuccessBody(
	result: Result.Success,
	isUrlDecodeEnabled: Boolean,
	isExtractUrlEnabled: Boolean,
	onShareClick: (Result.Success) -> Unit,
	onCopyToClipboardClick: (Result.Success) -> Unit,
	onOpenClick: (Result.Success) -> Unit,
	onResetClick: () -> Unit,
	onUrlDecodeCheckedChange: (Boolean) -> Unit,
	onExtractUrlCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
	) {
		Card {
			Column(
				modifier = Modifier.padding(16.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = stringResource(R.string.original_url),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.primary,
				)

				SelectionContainer {
					Text(
						modifier = Modifier.padding(16.dp),
						text = result.originalText,
						style = MaterialTheme.typography.bodyMedium,
					)
				}

				Text(
					text = stringResource(R.string.cleaned_url),
					style = MaterialTheme.typography.bodyLarge,
					color = MaterialTheme.colorScheme.primary,
				)

				SelectionContainer {
					Text(
						modifier = Modifier.padding(16.dp),
						text = result.cleanedText,
						style = MaterialTheme.typography.bodyMedium,
					)
				}

				val buttonModifier = Modifier.widthIn(min = 120.dp)

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp),
					horizontalArrangement = Arrangement.SpaceAround,
				) {
					OutlinedButton(
						modifier = buttonModifier,
						onClick = { onShareClick(result) },
					) {
						Text(
							text = stringResource(R.string.share),
							style = MaterialTheme.typography.bodyMedium,
						)
					}

					OutlinedButton(
						modifier = buttonModifier,
						onClick = { onCopyToClipboardClick(result) },
					) {
						Text(
							text = stringResource(R.string.copy),
							style = MaterialTheme.typography.bodyMedium,
						)
					}
				}

				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp),
					horizontalArrangement = Arrangement.SpaceAround,
				) {
					OutlinedButton(
						modifier = buttonModifier,
						onClick = { onOpenClick(result) },
						enabled = !isDefaultBrowser(LocalContext.current),
					) {
						Text(
							text = stringResource(R.string.open),
							style = MaterialTheme.typography.bodyMedium,
						)
					}

					OutlinedButton(
						modifier = buttonModifier,
						onClick = onResetClick,
					) {
						Text(
							text = stringResource(R.string.reset),
							style = MaterialTheme.typography.bodyMedium,
						)
					}
				}

				SwitchRow(
					modifier = Modifier.padding(top = 8.dp),
					text = stringResource(R.string.decode_url),
					checked = isUrlDecodeEnabled,
					onCheckedChange = onUrlDecodeCheckedChange,
				)

				SwitchRow(
					modifier = Modifier.padding(top = 8.dp),
					text = stringResource(R.string.extract_url),
					checked = isExtractUrlEnabled,
					onCheckedChange = onExtractUrlCheckedChange,
				)
			}
		}
	}
}

@Composable
private fun SwitchRow(
	text: String,
	checked: Boolean,
	onCheckedChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			modifier = Modifier.weight(1f),
			text = text,
			style = MaterialTheme.typography.bodyMedium,
		)

		Switch(
			modifier = Modifier.padding(start = 16.dp),
			checked = checked,
			onCheckedChange = onCheckedChange,
		)
	}
}

@Composable
private fun HowToBody(modifier: Modifier = Modifier, onImportFromClipboardClick: () -> Unit) {
	Card(
		modifier = modifier.fillMaxWidth(),
	) {
		Column(
			modifier = Modifier.padding(16.dp),
		) {
			OutlinedButton(
				modifier = Modifier.fillMaxWidth(),
				onClick = onImportFromClipboardClick,
			) {
				Text(
					text = stringResource(R.string.import_from_clipboard),
					style = MaterialTheme.typography.bodyMedium,
				)
			}

			Text(
				modifier = Modifier.padding(
					top = 16.dp,
					bottom = 8.dp,
				),
				text = stringResource(R.string.how_to_title),
				style = MaterialTheme.typography.headlineSmall,
			)

			Row {
				Image(
					modifier = Modifier
						.height(300.dp)
						.padding(end = 16.dp),
					painter = painterResource(R.drawable.howto_pixel_5),
					contentDescription = stringResource(R.string.a11y_howto),
				)

				Text(
					modifier = Modifier,
					textAlign = TextAlign.Justify,
					text = stringResource(R.string.how_to_text),
				)
			}
		}
	}
}

private tailrec fun Context.findWindow(): Window? = when (this) {
	is Activity -> window
	is ContextWrapper -> baseContext.findWindow()
	else -> null
}

@Preview(showBackground = true)
@Composable
private fun SuccessBodyPreview() {
	AppTheme {
		SuccessBody(
			result = Result.Success(
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
		)
	}
}

@Preview(showBackground = true)
@Composable
private fun HowToBodyPreview() {
	AppTheme {
		HowToBody(
			onImportFromClipboardClick = {},
		)
	}
}
