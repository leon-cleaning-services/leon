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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
 * Places the top app bar, snackbar host and background image around every scene that contains a
 * [Destination.TopLevel] entry — the chrome that used to live in `MainScreen`'s `Scaffold`. On
 * phones, a scene reached from [Destination.SettingsSanitizers] or [Destination.SettingsLicenses]
 * holds only that single entry, carries no such metadata and is returned untouched, keeping its own
 * `Scaffold` and back-arrow top bar. On wide windows the list-detail scene holds both the
 * [Destination.Settings] list entry and the detail entry side by side, so entries are scanned
 * rather than only the last one — otherwise the chrome would vanish whenever a detail pane is open.
 *
 * The navigation area itself (bottom bar / rail) is hoisted into `MainRouter`'s
 * `NavigationSuiteScaffold`, wrapping `NavDisplay` — composed once, outside every scene's animated
 * content, so switching tabs can no longer make it flicker.
 */
internal class TopLevelSceneDecoratorStrategy(private val snackbarHostState: SnackbarHostState) :
    SceneDecoratorStrategy<NavKey> {

    override fun SceneDecoratorStrategyScope<NavKey>.decorateScene(
        scene: Scene<NavKey>
    ): Scene<NavKey> {
        // In a two-pane list-detail scene the LAST entry is the detail pane, which carries no
        // top-level metadata — scan all entries to know whether this scene belongs to a top-level
        // destination at all.
        val isTopLevelScene = scene.entries.any { TOP_LEVEL_METADATA_KEY in it.metadata }
        if (!isTopLevelScene) return scene
        return TopLevelScene(scene, snackbarHostState)
    }
}

private class TopLevelScene(
    private val scene: Scene<NavKey>,
    snackbarHostState: SnackbarHostState,
) : Scene<NavKey> {

    // ListDetailSceneStrategy's own scene key is constant (Unit) across every list/detail
    // combination unless a caller passes a distinct `sceneKey`, which we don't — so basing this
    // key on `scene.key` alone would make every two-pane Settings scene compare equal to every
    // other one, and whatever machinery in NavDisplay decides to skip recomposition when a scene
    // key repeats would then never notice the detail pane changing. Keying off the actual set of
    // entries keeps identity stable when nothing changed while still reflecting real navigation.
    override val key: Any = TopLevelScene::class to scene.entries.map { it.contentKey }
    override val entries: List<NavEntry<NavKey>>
        get() = scene.entries

    override val previousEntries: List<NavEntry<NavKey>>
        get() = scene.previousEntries

    override val metadata: Map<String, Any>
        get() = scene.metadata

    @OptIn(ExperimentalMaterial3Api::class)
    override val content: @Composable () -> Unit = {
        // The current top-level destination of this scene, used only to key the app bar's scroll
        // state below — detail entries (SettingsSanitizers/SettingsLicenses) shown alongside
        // Settings in a two-pane scene are not TopLevel, so navigating between them does not reset
        // the bar; only an actual tab switch does.
        val current =
            scene.entries
                .map { it.contentKey }
                .filterIsInstance<Destination.TopLevel>()
                .lastOrNull()

        // ponytail: state is keyed on `current`, so switching tabs resets the bar to fully shown
        // rather
        // than restoring that tab's previous offset. Swap in a per-destination map of saved
        // TopAppBarStates if the reset ever reads as wrong.
        val topBarState = remember(current) { TopAppBarState(-Float.MAX_VALUE, 0f, 0f) }
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topBarState)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TopAppBar(scrollBehavior = scrollBehavior) },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                BackgroundImage()
                scene.content()
            }
        }
    }

    override fun equals(other: Any?): Boolean = other is TopLevelScene && key == other.key

    override fun hashCode(): Int = key.hashCode()
}
