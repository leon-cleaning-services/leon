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

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.settings_detail_placeholder
import com.svenjacobs.app.leon.ui.model.SourceText
import com.svenjacobs.app.leon.ui.screens.history.HistoryScreen
import com.svenjacobs.app.leon.ui.screens.main.MainScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsLicensesScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsSanitizersScreen
import com.svenjacobs.app.leon.ui.screens.settings.SettingsScreen
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainRouter(
    sourceText: State<SourceText?>,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(SavedStateConfiguration.DEFAULT, Destination.Main)
    val snackbarHostState = remember { SnackbarHostState() }

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

    // The Main entry is never removed (see goToTopLevel above), so once the user has navigated to
    // History or Settings the back stack holds BOTH Main (at index 0) and the real current
    // destination — the selected item is therefore the LAST TopLevel entry, not the first.
    val selectedTopLevel =
        backStack.filterIsInstance<Destination.TopLevel>().lastOrNull() ?: Destination.Main

    // Settings' detail pane (SettingsSanitizers/SettingsLicenses) ends the back stack on a
    // non-TopLevel key even though, in the two-pane layout, it is shown alongside the Settings list
    // — so the rail must stay visible there. On a single pane the same detail screen fills the
    // whole window and the nav area must disappear, exactly as the phone does today.
    val shouldShowNavBar = isTwoPane || backStack.lastOrNull() is Destination.TopLevel
    val navigationSuiteScaffoldState = rememberNavigationSuiteScaffoldState()

    LaunchedEffect(shouldShowNavBar) {
        if (shouldShowNavBar) {
            navigationSuiteScaffoldState.show()
        } else {
            navigationSuiteScaffoldState.hide()
        }
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

    NavigationSuiteScaffold(
        navigationItems = {
            TopLevelDestinations.forEach { destination ->
                NavigationSuiteItem(
                    selected = destination == selectedTopLevel,
                    onClick = dropUnlessResumed { goToTopLevel(destination) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = stringResource(destination.iconContentDescription),
                        )
                    },
                    label = { Text(stringResource(destination.label)) },
                )
            }
        },
        state = navigationSuiteScaffoldState,
        modifier = modifier,
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            // The default predictive back transition scales the outgoing scene down; a fade
            // matches the other transitions instead.
            predictivePopTransitionSpec = { ContentTransform(fadeIn(), fadeOut()) },
            entryDecorators =
                listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
            sceneStrategies = listOf(listDetailStrategy),
            sceneDecoratorStrategies =
                listOf(remember { TopLevelSceneDecoratorStrategy(snackbarHostState) }),
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
}

@Composable
private fun SettingsDetailPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.settings_detail_placeholder),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
