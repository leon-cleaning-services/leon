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
package com.svenjacobs.app.leon.ui.shortlink

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.ok
import com.svenjacobs.app.leon.shared.resources.resolve_short_links
import com.svenjacobs.app.leon.shared.resources.short_link_dialog_text
import com.svenjacobs.app.leon.shared.resources.short_link_dialog_title
import com.svenjacobs.app.leon.ui.common.views.SwitchRow
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkDialogViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.stringResource

/**
 * Shows [ShortLinkDialog] once, until the user has confirmed or dismissed it, and nothing at all
 * after that.
 */
@Composable
fun ShortLinkDialogHost(
    modifier: Modifier = Modifier,
    viewModel: ShortLinkDialogViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isVisible) {
        ShortLinkDialog(
            modifier = modifier,
            checked = uiState.resolveShortLinksEnabled,
            onCheckedChange = viewModel::onCheckedChange,
            onConfirm = viewModel::onConfirm,
        )
    }
}

/**
 * Introduces short link resolving, which is off by default, and lets the user turn it on right
 * here. Dismissing the dialog counts as confirming it.
 */
@Composable
fun ShortLinkDialog(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onConfirm,
        title = { Text(stringResource(Res.string.short_link_dialog_title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(Res.string.short_link_dialog_text))

                SwitchRow(
                    modifier = Modifier.padding(top = 16.dp),
                    text = stringResource(Res.string.resolve_short_links),
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(Res.string.ok)) }
        },
    )
}
