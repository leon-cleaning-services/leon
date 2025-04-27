/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.svenjacobs.app.leon.ui.common.views.TopAppBar

@Composable
fun SettingsLicensesScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			TopAppBar(
				onBackClick = onBackClick,
			)
		},
	) { contentPadding ->
		LibrariesContainer(
			modifier = Modifier
				.padding(contentPadding)
				.fillMaxSize(),
			colors = LibraryDefaults.libraryColors(
				backgroundColor = MaterialTheme.colorScheme.background,
				contentColor = MaterialTheme.colorScheme.onBackground,
				badgeBackgroundColor = MaterialTheme.colorScheme.primary,
				badgeContentColor = MaterialTheme.colorScheme.onPrimary,
				dialogConfirmButtonColor = MaterialTheme.colorScheme.primary,
			),
		)
	}
}
