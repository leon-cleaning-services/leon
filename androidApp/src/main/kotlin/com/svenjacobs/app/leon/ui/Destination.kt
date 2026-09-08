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
package com.svenjacobs.app.leon.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.svenjacobs.app.leon.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

sealed interface Destination : NavKey {

    /**
     * A destination reachable from the bottom navigation bar.
     *
     * @property icon Icon of the bottom navigation bar item.
     * @property label Text below the icon, naming the destination.
     * @property iconContentDescription Description of [icon] for screen readers.
     */
    sealed interface TopLevel : Destination {
        val icon: ImageVector
        @get:StringRes val label: Int
        @get:StringRes val iconContentDescription: Int
    }

    @Serializable
    data object Main : TopLevel {
        override val icon = Icons.Filled.Home
        override val label = R.string.screen_main
        override val iconContentDescription = R.string.screen_main
    }

    @Serializable
    data object History : TopLevel {
        override val icon = Icons.Filled.History
        override val label = R.string.screen_history
        override val iconContentDescription = R.string.screen_history
    }

    @Serializable
    data object Settings : TopLevel {
        override val icon = Icons.Filled.Settings
        override val label = R.string.screen_settings
        override val iconContentDescription = R.string.screen_settings
    }

    @Serializable data object SettingsSanitizers : Destination

    @Serializable data object SettingsLicenses : Destination
}

internal val TopLevelDestinations =
    persistentListOf(Destination.Main, Destination.History, Destination.Settings)
