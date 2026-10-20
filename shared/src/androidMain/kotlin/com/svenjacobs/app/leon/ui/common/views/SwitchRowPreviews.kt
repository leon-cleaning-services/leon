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
package com.svenjacobs.app.leon.ui.common.views

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews

@Composable
private fun SwitchRowPreview(
    checked: Boolean,
    supportingText: String? = null,
    enabled: Boolean = true,
) {
    AppTheme {
        Surface {
            SwitchRow(
                text = "Resolve short links",
                checked = checked,
                onCheckedChange = {},
                supportingText = supportingText,
                enabled = enabled,
            )
        }
    }
}

@DayNightPreviews
@Composable
private fun SwitchRowCheckedPreview() {
    SwitchRowPreview(checked = true)
}

@DayNightPreviews
@Composable
private fun SwitchRowUncheckedPreview() {
    SwitchRowPreview(checked = false)
}

@DayNightPreviews
@Composable
private fun SwitchRowSupportingTextPreview() {
    SwitchRowPreview(
        checked = true,
        supportingText = "Asks only the service that created a short link where it leads",
    )
}

@DayNightPreviews
@Composable
private fun SwitchRowDisabledPreview() {
    SwitchRowPreview(
        checked = true,
        supportingText = "Turn on “Resolve short links” in Settings first.",
        enabled = false,
    )
}
