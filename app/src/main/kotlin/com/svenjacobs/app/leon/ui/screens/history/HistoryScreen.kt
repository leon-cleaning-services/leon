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
package com.svenjacobs.app.leon.ui.screens.history

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.db.HistoryEntry
import com.svenjacobs.app.leon.ui.common.copyToClipboard
import com.svenjacobs.app.leon.ui.common.isDefaultBrowser
import com.svenjacobs.app.leon.ui.common.openUrl
import com.svenjacobs.app.leon.ui.common.shareText
import com.svenjacobs.app.leon.ui.screens.history.model.HistoryScreenViewModel
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: HistoryScreenViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        modifier = modifier,
        isEnabled = uiState.isEnabled,
        isCustomTabsEnabled = uiState.isCustomTabsEnabled,
        entries = uiState.entries,
        snackbarHostState = snackbarHostState,
        onDeleteClick = viewModel::onDeleteClick,
        onUndoDeleteClick = viewModel::onUndoDeleteClick,
        onClearAllClick = viewModel::onClearAllClick,
    )
}

@Composable
private fun Content(
    isEnabled: Boolean,
    isCustomTabsEnabled: Boolean,
    entries: ImmutableList<HistoryEntry>,
    snackbarHostState: SnackbarHostState,
    onDeleteClick: (String) -> Unit,
    onUndoDeleteClick: (HistoryEntry) -> Unit,
    onClearAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val deletedMessage = stringResource(R.string.history_deleted)
    val undoLabel = stringResource(R.string.undo)
    var showClearAllDialog by rememberSaveable { mutableStateOf(false) }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            text = { Text(stringResource(R.string.history_clear_all_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearAllDialog = false
                        onClearAllClick()
                    }
                ) {
                    Text(stringResource(R.string.history_clear_all))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when {
            !isEnabled ->
                EmptyState(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(R.string.history_disabled_text),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }

            entries.isEmpty() ->
                EmptyState(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(R.string.history_empty_title),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(R.string.history_empty_text),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }

            else ->
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        items(entries, key = { it.id }) { entry ->
                            HistoryRow(
                                entry = entry,
                                isCustomTabsEnabled = isCustomTabsEnabled,
                                snackbarHostState = snackbarHostState,
                                onDelete = { deleted ->
                                    coroutineScope.launch {
                                        onDeleteClick(deleted.id)
                                        val result =
                                            snackbarHostState.showSnackbar(
                                                message = deletedMessage,
                                                actionLabel = undoLabel,
                                                duration = SnackbarDuration.Short,
                                            )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            onUndoDeleteClick(deleted)
                                        }
                                    }
                                },
                                modifier = Modifier.padding(bottom = 8.dp).animateItem(),
                            )
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showClearAllDialog = true },
                    ) {
                        Text(stringResource(R.string.history_clear_all))
                    }
                }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
private fun HistoryRow(
    entry: HistoryEntry,
    isCustomTabsEnabled: Boolean,
    snackbarHostState: SnackbarHostState,
    onDelete: (HistoryEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val shareTitle = stringResource(R.string.share)
    val openTitle = stringResource(R.string.open)
    val clipboardMessage = stringResource(R.string.clipboard_message)
    var menuFor by remember { mutableStateOf<String?>(null) }

    val dismissState = rememberSwipeToDismissBoxState()

    Box(modifier = modifier) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = { SwipeBackground(dismissState.dismissDirection) },
            onDismiss = { value ->
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        shareText(context = context, text = entry.url, chooserTitle = shareTitle)
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDelete(entry)
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }

                // Always spring the card back rather than leaving the box in its dismissed state:
                // a LazyColumn item's state is restored under its key, so an entry brought back by
                // Undo would otherwise reappear as an invisible, already-swiped row. What removes
                // the row is the data — the DAO delete drops it from the Flow — not the box's own
                // dismissal. Share has to spring back regardless, since sharing keeps the entry.
                coroutineScope.launch { dismissState.reset() }
            },
            content = {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { menuFor = entry.id },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = entry.url,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text =
                                DateUtils.formatDateTime(
                                    context,
                                    entry.at,
                                    DateUtils.FORMAT_SHOW_DATE or
                                        DateUtils.FORMAT_SHOW_TIME or
                                        DateUtils.FORMAT_ABBREV_ALL,
                                ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
        )

        DropdownMenu(expanded = menuFor == entry.id, onDismissRequest = { menuFor = null }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.share)) },
                onClick = {
                    menuFor = null
                    shareText(context = context, text = entry.url, chooserTitle = shareTitle)
                },
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.copy)) },
                onClick = {
                    menuFor = null
                    coroutineScope.launch {
                        copyToClipboard(
                            clipboard = clipboard,
                            snackbarHostState = snackbarHostState,
                            text = entry.url,
                            message = clipboardMessage,
                        )
                    }
                },
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.open)) },
                enabled = !isDefaultBrowser(context),
                onClick = {
                    menuFor = null
                    openUrl(
                        context = context,
                        url = entry.url,
                        customTabs = isCustomTabsEnabled,
                        chooserTitle = openTitle,
                    )
                },
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.history_delete)) },
                onClick = {
                    menuFor = null
                    onDelete(entry)
                },
            )
        }
    }
}

@Composable
private fun SwipeBackground(direction: SwipeToDismissBoxValue) {
    val color: Color
    val icon: ImageVector?
    val alignment: Alignment
    val tint: Color
    val description: String?

    when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> {
            color = MaterialTheme.colorScheme.primaryContainer
            icon = Icons.Default.Share
            alignment = Alignment.CenterStart
            tint = MaterialTheme.colorScheme.onPrimaryContainer
            description = stringResource(R.string.share)
        }

        SwipeToDismissBoxValue.EndToStart -> {
            color = MaterialTheme.colorScheme.errorContainer
            icon = Icons.Default.Delete
            alignment = Alignment.CenterEnd
            tint = MaterialTheme.colorScheme.onErrorContainer
            description = stringResource(R.string.history_delete)
        }

        SwipeToDismissBoxValue.Settled -> {
            color = Color.Transparent
            icon = null
            alignment = Alignment.Center
            tint = Color.Unspecified
            description = null
        }
    }

    Box(
        modifier =
            Modifier.fillMaxSize()
                .background(color = color, shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 20.dp),
        contentAlignment = alignment,
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = description, tint = tint)
        }
    }
}

@Composable
@DayNightPreviews
private fun ContentPreview() {
    AppTheme {
        Content(
            isEnabled = true,
            isCustomTabsEnabled = false,
            entries =
                persistentListOf(
                    HistoryEntry(
                        id = "1",
                        url = "https://www.example.com/path?keep=123",
                        at = System.currentTimeMillis(),
                    ),
                    HistoryEntry(
                        id = "2",
                        url = "https://www.example.org/another/path?keep=456",
                        at = System.currentTimeMillis() - 3_600_000,
                    ),
                ),
            snackbarHostState = remember { SnackbarHostState() },
            onDeleteClick = {},
            onUndoDeleteClick = {},
            onClearAllClick = {},
        )
    }
}

@Composable
@DayNightPreviews
private fun ContentEmptyPreview() {
    AppTheme {
        Content(
            isEnabled = true,
            isCustomTabsEnabled = false,
            entries = persistentListOf(),
            snackbarHostState = remember { SnackbarHostState() },
            onDeleteClick = {},
            onUndoDeleteClick = {},
            onClearAllClick = {},
        )
    }
}

@Composable
@DayNightPreviews
private fun SwipeBackgroundPreview() {
    AppTheme {
        Column {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).height(80.dp)) {
                SwipeBackground(SwipeToDismissBoxValue.StartToEnd)
            }
            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                SwipeBackground(SwipeToDismissBoxValue.EndToStart)
            }
        }
    }
}
