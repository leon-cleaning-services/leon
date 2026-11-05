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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.cancel
import org.jetbrains.compose.resources.stringResource

/**
 * A dialog for choosing one of several values of a setting, each a [ChoiceRow] in [content].
 * Choosing a value is expected to close the dialog; only "Cancel" closes it without a choice.
 */
@Composable
internal fun ChoiceDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column(
                modifier =
                    Modifier.heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                        .selectableGroup(),
                content = content,
            )
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(Res.string.cancel)) }
        },
    )
}

/**
 * One value in a [ChoiceDialog]: a radio button, an optional [icon] and the value's [text].
 *
 * @param icon Drawn in a fixed-size slot, so that the texts of all rows line up even while an icon
 *   is still loading. Receives whether the row is enabled, to dim itself accordingly.
 */
@Composable
internal fun ChoiceRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    enabled: Boolean = true,
    icon: (@Composable (enabled: Boolean) -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .selectable(
                    selected = selected,
                    enabled = enabled,
                    role = Role.RadioButton,
                    onClick = onClick,
                )
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // `onClick = null`: the whole row is selectable, so the button must not take the click.
        RadioButton(selected = selected, onClick = null, enabled = enabled)

        if (icon != null) {
            Box(modifier = Modifier.padding(start = 16.dp).size(ChoiceIconSize)) { icon(enabled) }
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = text, color = choiceContentColor(enabled))

            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * A setting whose value is chosen in a [ChoiceDialog]: its [text], the current value as
 * [supportingText] below, and the whole row clickable to open the dialog.
 */
@Composable
internal fun ClickableRow(
    text: String,
    supportingText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Text(text = text)
        Text(
            text = supportingText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** The color of a [ChoiceRow]'s text, and of an icon tinted to match it. */
@Composable
internal fun choiceContentColor(enabled: Boolean): Color =
    if (enabled) LocalContentColor.current
    else MaterialTheme.colorScheme.onSurface.copy(alpha = DISABLED_ALPHA)

/** The size of the icon slot of a [ChoiceRow]. */
internal val ChoiceIconSize = 32.dp

/** The alpha Material 3 dims the content of a disabled component with. */
private const val DISABLED_ALPHA = 0.38f
