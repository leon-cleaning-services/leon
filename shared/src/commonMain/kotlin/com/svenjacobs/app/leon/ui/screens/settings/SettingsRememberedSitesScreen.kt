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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.svenjacobs.app.leon.db.SiteParameter
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.cancel
import com.svenjacobs.app.leon.shared.resources.remembered_forget
import com.svenjacobs.app.leon.shared.resources.remembered_forget_all
import com.svenjacobs.app.leon.shared.resources.remembered_forget_all_confirm
import com.svenjacobs.app.leon.shared.resources.remembered_kept
import com.svenjacobs.app.leon.shared.resources.remembered_removed
import com.svenjacobs.app.leon.shared.resources.remembered_sites_description
import com.svenjacobs.app.leon.shared.resources.remembered_sites_empty
import com.svenjacobs.app.leon.ui.common.views.TopAppBar
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsRememberedSitesScreenViewModel
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsRememberedSitesScreenViewModel.UiState.Site
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRememberedSitesScreen(
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    viewModel: SettingsRememberedSitesScreenViewModel = metroViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        // Only attached while a top app bar is composed, see SettingsSanitizersScreen.
        modifier =
            if (onBackClick != null) {
                modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            } else {
                modifier
            },
        topBar = {
            if (onBackClick != null) {
                TopAppBar(onBackClick = onBackClick, scrollBehavior = scrollBehavior)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { contentPadding ->
        RememberedSitesContent(
            modifier = Modifier.padding(contentPadding),
            isLoading = uiState.isLoading,
            sites = uiState.sites,
            onForgetClick = viewModel::onForgetClick,
            onForgetSiteClick = viewModel::onForgetSiteClick,
            onForgetAllClick = viewModel::onForgetAllClick,
        )
    }
}

@Composable
fun RememberedSitesContent(
    isLoading: Boolean,
    sites: ImmutableList<Site>,
    onForgetClick: (SiteParameter) -> Unit,
    onForgetSiteClick: (String) -> Unit,
    onForgetAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showForgetAllDialog by rememberSaveable { mutableStateOf(false) }

    if (showForgetAllDialog) {
        AlertDialog(
            onDismissRequest = { showForgetAllDialog = false },
            text = { Text(stringResource(Res.string.remembered_forget_all_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showForgetAllDialog = false
                        onForgetAllClick()
                    },
                ) {
                    Text(stringResource(Res.string.remembered_forget_all))
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgetAllDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
        )
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = stringResource(Res.string.remembered_sites_description),
            style = MaterialTheme.typography.bodyLarge,
        )

        when {
            isLoading ->
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
                )

            sites.isEmpty() ->
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(Res.string.remembered_sites_empty),
                    style = MaterialTheme.typography.bodyMedium,
                )

            else -> {
                Card(modifier = Modifier.weight(1f, fill = false)) {
                    // A host never contains a slash, so these keys cannot collide.
                    LazyColumn {
                        sites.forEachIndexed { index, site ->
                            if (index > 0)
                                item(key = "divider/${site.site}") { HorizontalDivider() }

                            item(key = "site/${site.site}") {
                                SiteHeader(
                                    site = site.site,
                                    onForgetClick = { onForgetSiteClick(site.site) },
                                )
                            }

                            items(
                                site.parameters,
                                key = { "parameter/${it.site}/${it.parameter}" },
                            ) {
                                ParameterItem(parameter = it, onForgetClick = { onForgetClick(it) })
                            }
                        }
                    }
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    onClick = { showForgetAllDialog = true },
                ) {
                    Text(stringResource(Res.string.remembered_forget_all))
                }
            }
        }
    }
}

@Composable
private fun SiteHeader(site: String, onForgetClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = site,
            style = MaterialTheme.typography.titleMedium,
        )

        IconButton(onClick = onForgetClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.remembered_forget),
            )
        }
    }
}

@Composable
private fun ParameterItem(
    parameter: SiteParameter,
    onForgetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = parameter.parameter, style = MaterialTheme.typography.bodyMedium)
            Text(
                text =
                    when (parameter.decision) {
                        SiteParameter.Decision.REMOVE ->
                            stringResource(Res.string.remembered_removed)
                        SiteParameter.Decision.KEEP -> stringResource(Res.string.remembered_kept)
                    },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onForgetClick) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(Res.string.remembered_forget),
            )
        }
    }
}
