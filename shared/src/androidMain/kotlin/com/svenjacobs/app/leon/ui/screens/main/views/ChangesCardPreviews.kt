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

import androidx.compose.runtime.Composable
import com.svenjacobs.app.leon.core.domain.change.Change
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.ui.screens.main.model.MainScreenViewModel.UiState.ChangeRow
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews
import kotlinx.collections.immutable.persistentListOf

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
