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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.ui.common.views.TopAppBar
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsSanitizersScreenViewModel

@Composable
fun SettingsSanitizersScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsSanitizersScreenViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier, topBar = { TopAppBar(onBackClick = onBackClick) }) {
        contentPadding ->
        Column(modifier = Modifier.padding(contentPadding).padding(horizontal = 16.dp)) {
            Text(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                text = stringResource(R.string.sanitizers_description),
                style = MaterialTheme.typography.bodyLarge,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text(stringResource(R.string.sanitizers_search_placeholder)) },
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription =
                                    stringResource(R.string.sanitizers_search_clear),
                            )
                        }
                    }
                },
            )

            if (uiState.sanitizers.isEmpty() && uiState.searchQuery.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.sanitizers_no_results),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                ElevatedCard {
                    LazyColumn {
                        //noinspection NewApi
                        uiState.sanitizers.forEachIndexed { index, sanitizer ->
                            item(key = sanitizer.id.value) {
                                if (index > 0) {
                                    HorizontalDivider()
                                }
                                SanitizerItem(
                                    name = sanitizer.name,
                                    isEnabled = sanitizer.enabled,
                                    onToggle = {
                                        viewModel.onSanitizerCheckedChange(
                                            sanitizer.id,
                                            !sanitizer.enabled,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SanitizerItem(
    name: String,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable(onClick = onToggle),
        headlineContent = { Text(name) },
        trailingContent = { Switch(checked = isEnabled, onCheckedChange = null) },
    )
}
