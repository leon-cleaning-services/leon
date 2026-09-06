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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.ui.model.SourceText
import com.svenjacobs.app.leon.ui.screens.history.HistoryScreen
import com.svenjacobs.app.leon.ui.screens.main.MainScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsLicensesScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsSanitizersScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainRouter(
    sourceText: State<SourceText?>,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Destination.Main)
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkTheme = isSystemInDarkTheme()
    val view = LocalView.current

    // Override the default so there is no horizontal gap between the panes on wide windows.
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val directive =
        remember(windowAdaptiveInfo) {
            calculatePaneScaffoldDirective(windowAdaptiveInfo)
                .copy(horizontalPartitionSpacerSize = 0.dp)
        }
    // The strategy's own default back behavior (PopUntilScaffoldValueChange) keeps popping
    // backstack entries until the two/single-pane shape changes — but with a permanent
    // detailPlaceholder the shape never changes while the window stays wide, so one back press
    // would cascade past every visited sub-screen straight back to Main. PopLatest pops exactly
    // one entry, matching the phone's own back-arrow behavior.
    val listDetailStrategy =
        rememberListDetailSceneStrategy<NavKey>(
            directive = directive,
            backNavigationBehavior = BackNavigationBehavior.PopLatest,
        )
    // Settings and its Sanitizers/Licenses sub-screens are shown side by side on wide windows, so
    // the detail screen must not draw its own back arrow — the list pane's chrome already covers
    // navigating back.
    val isTwoPane = directive.maxHorizontalPartitions > 1

    fun goToTopLevel(destination: Destination.TopLevel) {
        // popUpTo(start) { saveState } + launchSingleTop, without ever removing the Main entry
        while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        if (destination != Destination.Main) backStack.add(destination)
    }

    // The activity is a singleTask, so a share arrives at whichever tab was open when the app was
    // last left — without this, the cleaned URL waits unseen behind the settings. Switching to the
    // main tab is also what composes `MainScreen`, which is where the text is handed to the view
    // model.
    LaunchedEffect(sourceText.value) {
        if (sourceText.value?.text != null) {
            goToTopLevel(Destination.Main)
        }
    }

    LaunchedEffect(Unit) {
        val window = view.context.findWindow() ?: return@LaunchedEffect
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = !isDarkTheme
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        // The default predictive back transition scales the outgoing scene down; a fade matches
        // the other transitions instead.
        predictivePopTransitionSpec = { ContentTransform(fadeIn(), fadeOut()) },
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        sceneStrategies = listOf(listDetailStrategy),
        sceneDecoratorStrategies =
            listOf(remember { TopLevelSceneDecoratorStrategy(snackbarHostState, ::goToTopLevel) }),
        entryProvider =
            entryProvider {
                entry<Destination.Main>(metadata = topLevelMetadata(Destination.Main)) {
                    MainScreen(
                        sourceText = sourceText,
                        snackbarHostState = snackbarHostState,
                        onResetClick = onResetClick,
                    )
                }

                entry<Destination.History>(metadata = topLevelMetadata(Destination.History)) {
                    HistoryScreen(snackbarHostState = snackbarHostState)
                }

                entry<Destination.Settings>(
                    metadata =
                        topLevelMetadata(Destination.Settings) +
                            ListDetailSceneStrategy.listPane(
                                detailPlaceholder = { SettingsDetailPlaceholder() }
                            )
                ) {
                    SettingsScreen(
                        onNavigateToSettingsSanitizers =
                            dropUnlessResumed { backStack.add(Destination.SettingsSanitizers) },
                        onNavigateToSettingsLicenses =
                            dropUnlessResumed { backStack.add(Destination.SettingsLicenses) },
                    )
                }

                entry<Destination.SettingsSanitizers>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    SettingsSanitizersScreen(
                        onBackClick =
                            if (isTwoPane) {
                                null
                            } else {
                                dropUnlessResumed { backStack.removeLastOrNull() }
                            }
                    )
                }

                entry<Destination.SettingsLicenses>(
                    metadata = ListDetailSceneStrategy.detailPane()
                ) {
                    SettingsLicensesScreen(
                        onBackClick =
                            if (isTwoPane) {
                                null
                            } else {
                                dropUnlessResumed { backStack.removeLastOrNull() }
                            }
                    )
                }
            },
    )
}

@Composable
private fun SettingsDetailPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.settings_detail_placeholder),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }
