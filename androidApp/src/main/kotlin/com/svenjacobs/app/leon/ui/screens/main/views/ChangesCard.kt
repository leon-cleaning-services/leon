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
package com.svenjacobs.app.leon.ui.screens.main.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.core.domain.change.Change
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AllSanitizers
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.sanitizer.displayName
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.ChangeRow
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Lists everything the sanitizers proposed for the shared URL, plus its remaining parameters, and
 * lets the user pick which of them are applied.
 */
@Composable
fun ChangesCard(
    changes: ImmutableList<ChangeRow>,
    onChangeToggled: (ChangeRow, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (changes.isEmpty()) return

    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.changes_title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val context = LocalContext.current
            val names =
                remember(context) { AllSanitizers.associate { it.id to it.displayName(context) } }

            changes.forEach { row ->
                ChangeItem(
                    row = row,
                    // One row can be several sanitizers removing the same value.
                    sanitizerNames = row.sanitizerIds.mapNotNull(names::get).joinToString(),
                    onCheckedChange = { onChangeToggled(row, it) },
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun ChangeItem(
    row: ChangeRow,
    sanitizerNames: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!row.applied) }
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = row.applied, onCheckedChange = onCheckedChange)

        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = row.action.describe(),
                style = MaterialTheme.typography.bodyMedium,
                // Struck through once it is gone from the cleaned URL.
                textDecoration = if (row.applied) TextDecoration.LineThrough else null,
            )

            if (sanitizerNames.isNotEmpty()) {
                Text(
                    text = sanitizerNames,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * The technical description of a change: parameters, hosts and paths are not translated, since they
 * are shown exactly as they appear in the URL.
 */
private fun Change.Action.describe(): String =
    when (this) {
        is Change.Action.RemoveParameter -> parameter.toString()
        is Change.Action.Replace -> to.toString()
        is Change.Action.SetComponent ->
            when (component) {
                Change.Component.FRAGMENT -> "#${from.orEmpty()}"
                else -> "${from.orEmpty()} → ${to.orEmpty()}"
            }
    }

@DayNightPreviews
@Composable
private fun ChangesCardPreview() {
    val google = SanitizerId("google_analytics")
    val salesforce = SanitizerId("salesforce")

    AppTheme {
        ChangesCard(
            changes =
                persistentListOf(
                    // Proposed by two sanitizers at once, which is one row.
                    ChangeRow(
                        action =
                            Change.Action.RemoveParameter(Url.Parameter("utm_source", "twitter")),
                        sanitizerIds = persistentListOf(google, salesforce),
                        applied = true,
                    ),
                    ChangeRow(
                        action =
                            Change.Action.RemoveParameter(Url.Parameter("utm_medium", "social")),
                        sanitizerIds = persistentListOf(google),
                        applied = false,
                    ),
                    // Nothing proposed this one; the user may still remove it.
                    ChangeRow(
                        action = Change.Action.RemoveParameter(Url.Parameter("page", "2")),
                        sanitizerIds = persistentListOf(),
                        applied = false,
                    ),
                ),
            onChangeToggled = { _, _ -> },
        )
    }
}
