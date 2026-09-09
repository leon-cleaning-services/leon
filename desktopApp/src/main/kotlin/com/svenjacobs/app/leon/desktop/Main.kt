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
package com.svenjacobs.app.leon.desktop

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.svenjacobs.app.leon.ui.BuildInfo
import com.svenjacobs.app.leon.ui.LocalBuildInfo
import com.svenjacobs.app.leon.ui.MainRouter
import com.svenjacobs.app.leon.ui.model.SourceText
import com.svenjacobs.app.leon.ui.theme.AppTheme
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import kotlin.uuid.Uuid

fun main(args: Array<String>) {
    val dirs = DesktopDirs(data = desktopDataDir(), config = desktopConfigDir())
    dirs.data.mkdirs()
    dirs.config.mkdirs()
    migrateConfigFiles(dirs.data, dirs.config)
    val graph = createGraphFactory<DesktopGraph.Factory>().create(dirs)

    val sourceText =
        mutableStateOf(
            args
                .joinToString(" ")
                .takeIf { it.isNotBlank() }
                ?.let {
                    SourceText(id = Uuid.random().toString(), text = it)
                }
        )

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Léon",
            state = rememberWindowState(width = 1000.dp, height = 800.dp),
        ) {
            CompositionLocalProvider(
                LocalMetroViewModelFactory provides graph.metroViewModelFactory,
                LocalBuildInfo provides
                    BuildInfo(
                        isDebug = false,
                        versionName = System.getProperty("jpackage.app-version") ?: "dev",
                    ),
            ) {
                AppTheme {
                    MainRouter(sourceText = sourceText, onResetClick = { sourceText.value = null })
                }
            }
        }
    }
}
