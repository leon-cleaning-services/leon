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
package com.svenjacobs.app.leon.ui.screens.settings.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svenjacobs.app.leon.db.SiteParameter
import com.svenjacobs.app.leon.db.SiteParameterDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Lists the parameter choices remembered per site and lets the user forget them. */
@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class SettingsRememberedSitesScreenViewModel(private val siteParameterDao: SiteParameterDao) :
    ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val sites: ImmutableList<Site> = persistentListOf(),
    ) {
        /** One site with everything remembered for it, ordered by parameter name. */
        data class Site(val site: String, val parameters: ImmutableList<SiteParameter>)
    }

    val uiState: StateFlow<UiState> =
        siteParameterDao
            .entries()
            .map { entries ->
                UiState(
                    isLoading = false,
                    sites =
                        entries
                            .groupBy { it.site }
                            .map { (site, parameters) ->
                                UiState.Site(site, parameters.toImmutableList())
                            }
                            .toImmutableList(),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UiState(),
            )

    fun onForgetClick(parameter: SiteParameter) {
        viewModelScope.launch { siteParameterDao.delete(parameter.site, parameter.parameter) }
    }

    fun onForgetSiteClick(site: String) {
        viewModelScope.launch { siteParameterDao.deleteSite(site) }
    }

    fun onForgetAllClick() {
        viewModelScope.launch { siteParameterDao.clear() }
    }
}
