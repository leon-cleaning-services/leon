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

package com.svenjacobs.app.leon.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.svenjacobs.app.leon.ui.screens.main.MainScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsLicensesScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsSanitizersScreen

@Composable
fun MainRouter(
    sourceText: State<String?>,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.MAIN,
    ) {
        composable(Routes.MAIN) {
            MainScreen(
                sourceText = sourceText,
                onNavigateToSettingsSanitizers = dropUnlessResumed {
                    navController.navigate(Routes.SETTINGS_SANITIZER)
                },
                onNavigateToSettingsLicenses = dropUnlessResumed {
                    navController.navigate(Routes.SETTINGS_LICENSES)
                },
                onResetClick = onResetClick,
            )
        }

        composable(Routes.SETTINGS_SANITIZER) {
            SettingsSanitizersScreen(
                onBackClick = dropUnlessResumed { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS_LICENSES) {
            SettingsLicensesScreen(
                onBackClick = dropUnlessResumed { navController.popBackStack() },
            )
        }
    }
}

private object Routes {
    const val MAIN = "main"
    const val SETTINGS_SANITIZER = "settings_sanitizers"
    const val SETTINGS_LICENSES = "settings_licenses"
}
