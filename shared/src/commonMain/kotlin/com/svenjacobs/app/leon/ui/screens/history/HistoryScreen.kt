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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svenjacobs.app.leon.db.HistoryEntry
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.cancel
import com.svenjacobs.app.leon.shared.resources.clipboard_message
import com.svenjacobs.app.leon.shared.resources.copy
import com.svenjacobs.app.leon.shared.resources.history_clear_all
import com.svenjacobs.app.leon.shared.resources.history_clear_all_confirm
import com.svenjacobs.app.leon.shared.resources.history_delete
import com.svenjacobs.app.leon.shared.resources.history_deleted
import com.svenjacobs.app.leon.shared.resources.history_disabled_text
import com.svenjacobs.app.leon.shared.resources.history_empty_text
import com.svenjacobs.app.leon.shared.resources.history_empty_title
import com.svenjacobs.app.leon.shared.resources.open
import com.svenjacobs.app.leon.shared.resources.share
import com.svenjacobs.app.leon.shared.resources.undo
import com.svenjacobs.app.leon.ui.common.copyToClipboard
import com.svenjacobs.app.leon.ui.common.formatDateTime
import com.svenjacobs.app.leon.ui.common.rememberUrlActions
import com.svenjacobs.app.leon.ui.screens.history.model.HistoryScreenViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun HistoryScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: HistoryScreenViewModel = metroViewModel(),
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
fun Content(
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
    val deletedMessage = stringResource(Res.string.history_deleted)
    val undoLabel = stringResource(Res.string.undo)
    var showClearAllDialog by rememberSaveable { mutableStateOf(false) }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            text = { Text(stringResource(Res.string.history_clear_all_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearAllDialog = false
                        onClearAllClick()
                    }
                ) {
                    Text(stringResource(Res.string.history_clear_all))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }

    Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when {
            !isEnabled ->
                EmptyState(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(Res.string.history_disabled_text),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }

            entries.isEmpty() ->
                EmptyState(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = stringResource(Res.string.history_empty_title),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(Res.string.history_empty_text),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }

            else ->
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        columns = GridCells.Adaptive(320.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
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
                                modifier = Modifier.animateItem(),
                            )
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showClearAllDialog = true },
                    ) {
                        Text(stringResource(Res.string.history_clear_all))
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
    val urlActions = rememberUrlActions()
    val clipboard = LocalClipboard.current
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val shareTitle = stringResource(Res.string.share)
    val openTitle = stringResource(Res.string.open)
    val clipboardMessage = stringResource(Res.string.clipboard_message)
    var menuFor by remember { mutableStateOf<String?>(null) }

    // Deliberately `remember`, not `rememberSwipeToDismissBoxState`: that one is backed by
    // `rememberSaveable`, and a LazyColumn stores an item's saveable state under the item's key.
    // A row swiped away and then brought back by Undo would therefore return with its swipe
    // restored — dismissed, drawn fully off to the side, an invisible gap in the list. A
    // half-finished swipe is not worth preserving across anything, so this state is not saved at
    // all and a re-entering row always starts settled.
    val positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    val dismissState =
        remember(positionalThreshold) {
            SwipeToDismissBoxState(
                initialValue = SwipeToDismissBoxValue.Settled,
                positionalThreshold = positionalThreshold,
            )
        }

    Box(modifier = modifier) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = { SwipeBackground(dismissState.dismissDirection) },
            enableDismissFromStartToEnd = urlActions.canShare,
            onDismiss = { value ->
                when (value) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        urlActions.share(text = entry.url, chooserTitle = shareTitle)
                    }

                    SwipeToDismissBoxValue.EndToStart -> {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDelete(entry)
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }

                // Neither gesture removes the row by itself: share keeps the entry, and a delete
                // is removed by the data — the DAO delete drops it from the Flow — so the card is
                // always sent back to its resting position rather than left sitting dismissed.
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
                            text = formatDateTime(entry.at),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            },
        )

        DropdownMenu(expanded = menuFor == entry.id, onDismissRequest = { menuFor = null }) {
            if (urlActions.canShare) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.share)) },
                    onClick = {
                        menuFor = null
                        urlActions.share(text = entry.url, chooserTitle = shareTitle)
                    },
                )
            }

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.copy)) },
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
                text = { Text(stringResource(Res.string.open)) },
                enabled = !urlActions.isDefaultBrowser,
                onClick = {
                    menuFor = null
                    urlActions.open(
                        url = entry.url,
                        customTabs = isCustomTabsEnabled,
                        chooserTitle = openTitle,
                    )
                },
            )

            DropdownMenuItem(
                text = { Text(stringResource(Res.string.history_delete)) },
                onClick = {
                    menuFor = null
                    onDelete(entry)
                },
            )
        }
    }
}

@Composable
internal fun SwipeBackground(direction: SwipeToDismissBoxValue) {
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
            description = stringResource(Res.string.share)
        }

        SwipeToDismissBoxValue.EndToStart -> {
            color = MaterialTheme.colorScheme.errorContainer
            icon = Icons.Default.Delete
            alignment = Alignment.CenterEnd
            tint = MaterialTheme.colorScheme.onErrorContainer
            description = stringResource(Res.string.history_delete)
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
