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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.auto_reset_after
import com.svenjacobs.app.leon.shared.resources.auto_reset_minutes
import com.svenjacobs.app.leon.shared.resources.auto_reset_off
import com.svenjacobs.app.leon.ui.model.AutoReset
import org.jetbrains.compose.resources.stringResource

/**
 * Lets the user choose after how long the main screen resets itself, or turn that off. Choosing a
 * value closes the dialog.
 */
@Composable
fun AutoResetDialog(
    selected: AutoReset,
    onSelect: (AutoReset) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChoiceDialog(
        modifier = modifier,
        title = stringResource(Res.string.auto_reset_after),
        onDismissRequest = onDismissRequest,
    ) {
        AutoReset.entries.forEach { autoReset ->
            ChoiceRow(
                text = autoReset.text(),
                selected = autoReset == selected,
                onClick = { onSelect(autoReset) },
            )
        }
    }
}

/** The value as shown to the user: "Off", or the number of minutes. */
@Composable
internal fun AutoReset.text(): String =
    when (val minutes = minutes) {
        null -> stringResource(Res.string.auto_reset_off)
        else -> stringResource(Res.string.auto_reset_minutes, minutes)
    }
