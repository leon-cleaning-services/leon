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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneDecoratorStrategy
import androidx.navigation3.scene.SceneDecoratorStrategyScope
import com.svenjacobs.app.leon.ui.common.views.TopAppBar
import com.svenjacobs.app.leon.ui.screens.main.views.BackgroundImage

internal const val TOP_LEVEL_METADATA_KEY = "com.svenjacobs.app.leon.topLevel"

internal fun topLevelMetadata(destination: Destination.TopLevel): Map<String, Any> =
    mapOf(TOP_LEVEL_METADATA_KEY to destination)

/**
 * Places the top app bar, navigation area (bottom bar on phones, rail on wide windows), snackbar
 * host and background image around every scene whose top entry is a [Destination.TopLevel] — the
 * chrome that used to live in `MainScreen`'s `Scaffold`. Scenes reached from
 * [Destination.SettingsSanitizers] or [Destination.SettingsLicenses] carry no such metadata and are
 * returned untouched, keeping their own `Scaffold` and back-arrow top bar.
 */
internal class TopLevelSceneDecoratorStrategy(
    private val snackbarHostState: SnackbarHostState,
    private val onTopLevelClick: (Destination.TopLevel) -> Unit,
) : SceneDecoratorStrategy<NavKey> {

    override fun SceneDecoratorStrategyScope<NavKey>.decorateScene(
        scene: Scene<NavKey>
    ): Scene<NavKey> {
        val current =
            scene.entries.lastOrNull()?.metadata?.get(TOP_LEVEL_METADATA_KEY)
                as? Destination.TopLevel ?: return scene
        return TopLevelScene(scene, current, snackbarHostState, onTopLevelClick)
    }
}

private class TopLevelScene(
    private val scene: Scene<NavKey>,
    current: Destination.TopLevel,
    snackbarHostState: SnackbarHostState,
    onTopLevelClick: (Destination.TopLevel) -> Unit,
) : Scene<NavKey> {

    override val key: Any = TopLevelScene::class to scene.key
    override val entries: List<NavEntry<NavKey>>
        get() = scene.entries

    override val previousEntries: List<NavEntry<NavKey>>
        get() = scene.previousEntries

    override val metadata: Map<String, Any>
        get() = scene.metadata

    override val content: @Composable () -> Unit = {
        NavigationSuiteScaffold(
            navigationItems = {
                TopLevelDestinations.forEach { destination ->
                    NavigationSuiteItem(
                        selected = destination == current,
                        onClick = dropUnlessResumed { onTopLevelClick(destination) },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription =
                                    stringResource(destination.iconContentDescription),
                            )
                        },
                        label = { Text(stringResource(destination.label)) },
                    )
                }
            }
        ) {
            Scaffold(
                topBar = { TopAppBar() },
                snackbarHost = { SnackbarHost(snackbarHostState) },
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    BackgroundImage()
                    scene.content()
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean = other is TopLevelScene && key == other.key

    override fun hashCode(): Int = key.hashCode()
}
