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
import com.svenjacobs.app.leon.db.SiteParameter
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsRememberedSitesScreenViewModel.UiState.Site
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews
import kotlinx.collections.immutable.persistentListOf

@DayNightPreviews
@Composable
private fun RememberedSitesContentPreview() {
    AppTheme {
        RememberedSitesContent(
            isLoading = false,
            sites =
                persistentListOf(
                    Site(
                        "amazon.com",
                        persistentListOf(
                            SiteParameter("amazon.com", "th", SiteParameter.Decision.KEEP),
                        ),
                    ),
                    Site(
                        "example.com",
                        persistentListOf(
                            SiteParameter("example.com", "ref", SiteParameter.Decision.REMOVE),
                            SiteParameter("example.com", "src", SiteParameter.Decision.REMOVE),
                        ),
                    ),
                ),
            onForgetClick = {},
            onForgetSiteClick = {},
            onForgetAllClick = {},
        )
    }
}

@DayNightPreviews
@Composable
private fun RememberedSitesContentEmptyPreview() {
    AppTheme {
        RememberedSitesContent(
            isLoading = false,
            sites = persistentListOf(),
            onForgetClick = {},
            onForgetSiteClick = {},
            onForgetAllClick = {},
        )
    }
}
