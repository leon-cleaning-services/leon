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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.svenjacobs.app.leon.ui.common.views.TopAppBar

@Composable
fun SettingsLicensesScreen(modifier: Modifier = Modifier, onBackClick: (() -> Unit)?) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        // No top bar (and no back arrow) when shown as the detail pane of a two-pane layout; the
        // list pane's own top-level chrome already covers it.
        topBar = { if (onBackClick != null) TopAppBar(onBackClick = onBackClick) },
        // Let the list draw and scroll behind the navigation bar instead of stopping short of it;
        // the navigation bar inset is added back below as the list's own contentPadding.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { contentPadding ->
        val libs by produceLibraries()

        LibrariesContainer(
            modifier = Modifier.padding(contentPadding).fillMaxSize(),
            libraries = libs,
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
            colors =
                LibraryDefaults.libraryColors(
                    libraryBackgroundColor = MaterialTheme.colorScheme.background,
                    libraryContentColor = MaterialTheme.colorScheme.onBackground,
                    versionChipColors = LibraryDefaults.chipColors(),
                    licenseChipColors = LibraryDefaults.chipColors(),
                ),
        )
    }
}
