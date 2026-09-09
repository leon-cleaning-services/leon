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
package com.svenjacobs.app.leon.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.a11y_howto
import com.svenjacobs.app.leon.shared.resources.cleaned_url
import com.svenjacobs.app.leon.shared.resources.clipboard_empty_message
import com.svenjacobs.app.leon.shared.resources.clipboard_message
import com.svenjacobs.app.leon.shared.resources.copy
import com.svenjacobs.app.leon.shared.resources.decode_url
import com.svenjacobs.app.leon.shared.resources.extract_url
import com.svenjacobs.app.leon.shared.resources.how_to_text
import com.svenjacobs.app.leon.shared.resources.how_to_title
import com.svenjacobs.app.leon.shared.resources.howto_pixel_5
import com.svenjacobs.app.leon.shared.resources.import_from_clipboard
import com.svenjacobs.app.leon.shared.resources.open
import com.svenjacobs.app.leon.shared.resources.options
import com.svenjacobs.app.leon.shared.resources.original_url
import com.svenjacobs.app.leon.shared.resources.reset
import com.svenjacobs.app.leon.shared.resources.share
import com.svenjacobs.app.leon.shared.resources.submit
import com.svenjacobs.app.leon.shared.resources.text_field_placeholder
import com.svenjacobs.app.leon.ui.common.copyToClipboard
import com.svenjacobs.app.leon.ui.common.readText
import com.svenjacobs.app.leon.ui.common.rememberUrlActions
import com.svenjacobs.app.leon.ui.model.SourceText
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.ChangeRow
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.Result
import com.svenjacobs.app.leon.ui.screens.main.views.ChangesCard
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    sourceText: State<SourceText?>,
    snackbarHostState: SnackbarHostState,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val urlActions = rememberUrlActions()
    val clipboard = LocalClipboard.current
    val shareTitle = stringResource(Res.string.share)
    val openTitle = stringResource(Res.string.open)
    val copiedToClipboardMessage = stringResource(Res.string.clipboard_message)
    val clipboardEmptyMessage = stringResource(Res.string.clipboard_empty_message)

    fun openShareMenu(result: Result.Success) {
        urlActions.share(text = result.cleanedText, chooserTitle = shareTitle)
    }

    fun openUrl(result: Result.Success) {
        val url = result.urls.firstOrNull() ?: return
        urlActions.open(
            url = url,
            customTabs = uiState.isCustomTabsEnabled,
            chooserTitle = openTitle,
        )
    }

    fun copyToClipboard(text: String) {
        coroutineScope.launch {
            copyToClipboard(
                clipboard = clipboard,
                snackbarHostState = snackbarHostState,
                text = text,
                message = copiedToClipboardMessage,
            )
        }
    }

    LaunchedEffect(sourceText.value) {
        val sourceText = sourceText.value ?: return@LaunchedEffect

        viewModel.setText(sourceText.text, sourceText.id)
    }

    // Waits out the auto-reset deadline. The loop, rather than a single delay for the whole
    // duration, is what makes this work across a backgrounded app or a sleeping device: `delay`
    // runs on uptime and does not tick while the process is frozen, so it wakes up short, re-reads
    // the wall clock and fires straight away.
    LaunchedEffect(uiState.autoResetAt) {
        val at = uiState.autoResetAt ?: return@LaunchedEffect

        while (true) {
            val remaining = at - Clock.System.now().toEpochMilliseconds()
            if (remaining <= 0) break
            delay(remaining)
        }

        viewModel.onResetClick()
        onResetClick()
    }

    LaunchedEffect(uiState.inputId, uiState.actionAfterClean) {
        val inputId = uiState.inputId ?: return@LaunchedEffect
        val result = uiState.result as? Result.Success ?: return@LaunchedEffect
        if (!viewModel.consumeActionAfterClean(inputId)) return@LaunchedEffect

        when (uiState.actionAfterClean) {
            ActionAfterClean.OpenShareMenu -> openShareMenu(result)
            ActionAfterClean.OpenUrl -> openUrl(result)
            ActionAfterClean.CopyToClipboard -> copyToClipboard(result.cleanedText)
            ActionAfterClean.DoNothing -> {}
        }
    }

    Content(
        modifier = modifier,
        result = uiState.result,
        isUrlDecodeEnabled = uiState.isUrlDecodeEnabled,
        isExtractUrlEnabled = uiState.isExtractUrlEnabled,
        onImportFromClipboardClick = {
            coroutineScope.launch {
                val text = clipboard.readText()

                if (text.isNullOrBlank()) {
                    snackbarHostState.showSnackbar(clipboardEmptyMessage)
                } else {
                    viewModel.setText(text)
                }
            }
        },
        onSubmit = viewModel::setText,
        onShareClick = ::openShareMenu,
        onCopyToClipboardClick = ::copyToClipboard,
        onOpenClick = ::openUrl,
        onResetClick = {
            viewModel.onResetClick()
            onResetClick()
        },
        onUrlDecodeCheckedChange = viewModel::onUrlDecodeCheckedChange,
        onExtractUrlCheckedChange = viewModel::onExtractUrlCheckedChange,
        onChangeToggled = viewModel::onChangeToggled,
    )
}

@Composable
private fun Content(
    result: Result,
    isUrlDecodeEnabled: Boolean,
    isExtractUrlEnabled: Boolean,
    onImportFromClipboardClick: () -> Unit,
    onSubmit: (String) -> Unit,
    onShareClick: (Result.Success) -> Unit,
    onCopyToClipboardClick: (String) -> Unit,
    onOpenClick: (Result.Success) -> Unit,
    onResetClick: () -> Unit,
    onUrlDecodeCheckedChange: (Boolean) -> Unit,
    onExtractUrlCheckedChange: (Boolean) -> Unit,
    onChangeToggled: (ChangeRow, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (result) {
                    is Result.Success ->
                        SuccessBody(
                            result = result,
                            isUrlDecodeEnabled = isUrlDecodeEnabled,
                            isExtractUrlEnabled = isExtractUrlEnabled,
                            onShareClick = onShareClick,
                            onCopyToClipboardClick = onCopyToClipboardClick,
                            onOpenClick = onOpenClick,
                            onResetClick = onResetClick,
                            onUrlDecodeCheckedChange = onUrlDecodeCheckedChange,
                            onExtractUrlCheckedChange = onExtractUrlCheckedChange,
                            onChangeToggled = onChangeToggled,
                        )

                    else ->
                        HowToBody(
                            onImportFromClipboardClick = onImportFromClipboardClick,
                            onSubmit = onSubmit,
                        )
                }
            }
        }
    }
}

@Composable
fun SuccessBody(
    result: Result.Success,
    isUrlDecodeEnabled: Boolean,
    isExtractUrlEnabled: Boolean,
    onShareClick: (Result.Success) -> Unit,
    onCopyToClipboardClick: (String) -> Unit,
    onOpenClick: (Result.Success) -> Unit,
    onResetClick: () -> Unit,
    onUrlDecodeCheckedChange: (Boolean) -> Unit,
    onExtractUrlCheckedChange: (Boolean) -> Unit,
    onChangeToggled: (ChangeRow, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    var optionsExpanded by remember { mutableStateOf(false) }

    if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UrlDisplaySection(
                result = result,
                onUrlTap = onCopyToClipboardClick,
                onChangeToggled = onChangeToggled,
                modifier = Modifier.weight(1f),
            )
            ActionsSection(
                result = result,
                isUrlDecodeEnabled = isUrlDecodeEnabled,
                isExtractUrlEnabled = isExtractUrlEnabled,
                optionsExpanded = optionsExpanded,
                onOptionsExpandedChange = { optionsExpanded = it },
                onShareClick = onShareClick,
                onCopyToClipboardClick = onCopyToClipboardClick,
                onOpenClick = onOpenClick,
                onResetClick = onResetClick,
                onUrlDecodeCheckedChange = onUrlDecodeCheckedChange,
                onExtractUrlCheckedChange = onExtractUrlCheckedChange,
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            UrlDisplaySection(
                result = result,
                onUrlTap = onCopyToClipboardClick,
                onChangeToggled = onChangeToggled,
            )
            ActionsSection(
                result = result,
                isUrlDecodeEnabled = isUrlDecodeEnabled,
                isExtractUrlEnabled = isExtractUrlEnabled,
                optionsExpanded = optionsExpanded,
                onOptionsExpandedChange = { optionsExpanded = it },
                onShareClick = onShareClick,
                onCopyToClipboardClick = onCopyToClipboardClick,
                onOpenClick = onOpenClick,
                onResetClick = onResetClick,
                onUrlDecodeCheckedChange = onUrlDecodeCheckedChange,
                onExtractUrlCheckedChange = onExtractUrlCheckedChange,
            )
        }
    }
}

@Composable
private fun UrlDisplaySection(
    result: Result.Success,
    onUrlTap: (String) -> Unit,
    onChangeToggled: (ChangeRow, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().clickable { onUrlTap(result.originalText) },
            colors =
                CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(Res.string.original_url),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                SelectionContainer {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = result.originalText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth().clickable { onUrlTap(result.cleanedText) },
            colors =
                CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(Res.string.cleaned_url),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                SelectionContainer {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = result.cleanedText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        ChangesCard(changes = result.changes, onChangeToggled = onChangeToggled)
    }
}

@OptIn(ExperimentalGridApi::class)
@Composable
private fun ActionsSection(
    result: Result.Success,
    isUrlDecodeEnabled: Boolean,
    isExtractUrlEnabled: Boolean,
    optionsExpanded: Boolean,
    onOptionsExpandedChange: (Boolean) -> Unit,
    onShareClick: (Result.Success) -> Unit,
    onCopyToClipboardClick: (String) -> Unit,
    onOpenClick: (Result.Success) -> Unit,
    onResetClick: () -> Unit,
    onUrlDecodeCheckedChange: (Boolean) -> Unit,
    onExtractUrlCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val urlActions = rememberUrlActions()

    Column(modifier = modifier) {
        Grid(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            config = {
                // ActionsSection sits inside SuccessBody's own even split on wide windows (see
                // above), so its local width is roughly half the window's — not the window's own
                // breakpoint. 500.dp keeps a comfortable margin above what a phone or the folded
                // two-pane layout ever measures locally (~355-395dp) and below what a genuinely
                // wide window's half-share measures (~568dp+ on a 1280dp-wide tablet).
                // `hasBoundedWidth` guards the `toDp()`: a transient unbounded measure would
                // otherwise compare against Constraints.Infinity, pick four columns and then size
                // each one to a quarter of infinity.
                val columns =
                    if (constraints.hasBoundedWidth && constraints.maxWidth.toDp() >= 500.dp) 4
                    else 2
                repeat(columns) { column(1f / columns) }
                gap(8.dp)
            },
        ) {
            if (urlActions.canShare) {
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onShareClick(result) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(Res.string.share),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onCopyToClipboardClick(result.cleanedText) },
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(Res.string.copy),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpenClick(result) },
                enabled = !urlActions.isDefaultBrowser,
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(Res.string.open),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onResetClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(Res.string.reset),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onOptionsExpandedChange(!optionsExpanded) },
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.options),
                style = MaterialTheme.typography.bodyMedium,
            )
            Icon(
                imageVector =
                    if (optionsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
            )
        }

        AnimatedVisibility(visible = optionsExpanded) {
            Column {
                SwitchRow(
                    modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(Res.string.decode_url),
                    checked = isUrlDecodeEnabled,
                    onCheckedChange = onUrlDecodeCheckedChange,
                )
                SwitchRow(
                    modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(Res.string.extract_url),
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
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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

@OptIn(ExperimentalGridApi::class)
@Composable
fun HowToBody(
    onImportFromClipboardClick: () -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }

    // The how-to paragraph is the one piece of running prose in the app, so it is the one place
    // where a full-width tablet window turns into an uncomfortable line length. It is capped here
    // rather than around the whole screen on purpose: `SuccessBody` is cards in a two column split
    // and reads fine at full width, and capping it would starve `ActionsSection`'s Grid of the
    // width it needs to lay the action buttons out four across.
    // `widthIn` must come before `fillMaxWidth`: the other way round, `fillMaxWidth` fixes the
    // width to the parent's before `widthIn` is reached, and a max below that fixed width cannot
    // be applied any more — the cap silently does nothing.
    Card(modifier = modifier.widthIn(max = 840.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(text = stringResource(Res.string.text_field_placeholder)) },
                singleLine = true,
            )

            Grid(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                config = {
                    // Side by side only while both buttons fit on one line. "Import from
                    // clipboard" plus its icon needs about 190dp, so below ~400dp of grid width —
                    // which is every phone, the card's own 16dp padding taken off — the two stack
                    // full width instead of wrapping their labels over two lines.
                    // `hasBoundedWidth` guards the `toDp()`, as in `ActionsSection` below.
                    val columns =
                        if (constraints.hasBoundedWidth && constraints.maxWidth.toDp() >= 400.dp) 2
                        else 1
                    repeat(columns) { column(1f / columns) }
                    gap(8.dp)
                },
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = text.isNotBlank(),
                    onClick = { onSubmit(text) },
                ) {
                    Text(
                        text = stringResource(Res.string.submit),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onImportFromClipboardClick,
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(Res.string.import_from_clipboard),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                text = stringResource(Res.string.how_to_title),
                style = MaterialTheme.typography.headlineSmall,
            )

            Row {
                Image(
                    modifier = Modifier.height(300.dp).padding(end = 16.dp),
                    painter = painterResource(Res.drawable.howto_pixel_5),
                    contentDescription = stringResource(Res.string.a11y_howto),
                )

                Text(
                    modifier = Modifier,
                    textAlign = TextAlign.Justify,
                    text = stringResource(Res.string.how_to_text),
                )
            }
        }
    }
}
