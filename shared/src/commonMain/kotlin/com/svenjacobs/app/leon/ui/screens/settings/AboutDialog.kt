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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.about
import com.svenjacobs.app.leon.shared.resources.about_bugs
import com.svenjacobs.app.leon.shared.resources.about_contributors
import com.svenjacobs.app.leon.shared.resources.about_developer
import com.svenjacobs.app.leon.shared.resources.about_license
import com.svenjacobs.app.leon.shared.resources.about_sponsor
import com.svenjacobs.app.leon.shared.resources.about_version
import com.svenjacobs.app.leon.shared.resources.close
import com.svenjacobs.app.leon.ui.LocalBuildInfo
import com.svenjacobs.app.leon.ui.screens.settings.model.SettingsScreenViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AboutDialog(onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    val linkStyles =
        TextLinkStyles(
            style =
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                )
        )

    val developerTemplate = stringResource(Res.string.about_developer)
    val contributorsText = stringResource(Res.string.about_contributors)

    val licenseTemplate = stringResource(Res.string.about_license)
    val bugsTemplate = stringResource(Res.string.about_bugs)
    val sponsorTemplate = stringResource(Res.string.about_sponsor)

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.about)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = developerTemplate,
                            linkStyles = linkStyles,
                            "Sven Jacobs" to SettingsScreenViewModel.AUTHOR_URL,
                            contributorsText to SettingsScreenViewModel.CONTRIBUTORS_URL,
                        ),
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = licenseTemplate,
                            linkStyles = linkStyles,
                            SettingsScreenViewModel.GITHUB_URL to
                                SettingsScreenViewModel.GITHUB_URL,
                        ),
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = bugsTemplate,
                            linkStyles = linkStyles,
                            SettingsScreenViewModel.ISSUES_URL to
                                SettingsScreenViewModel.ISSUES_URL,
                        ),
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = sponsorTemplate,
                            linkStyles = linkStyles,
                            SettingsScreenViewModel.SPONSORS_URL to
                                SettingsScreenViewModel.SPONSORS_URL,
                        ),
                )

                Text(
                    text =
                        stringResource(Res.string.about_version, LocalBuildInfo.current.versionName)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(Res.string.close)) }
        },
    )
}

private fun buildLinkedAnnotatedString(
    template: String,
    linkStyles: TextLinkStyles,
    vararg linkPairs: Pair<String, String>,
): AnnotatedString = buildAnnotatedString {
    var cursor = 0
    linkPairs.forEachIndexed { index, (displayText, url) ->
        val placeholder = $$"%$${index + 1}$s"
        val pos = template.indexOf(placeholder, cursor)
        if (pos >= 0) {
            append(template.substring(cursor, pos))
            withLink(LinkAnnotation.Url(url, linkStyles)) { append(displayText) }
            cursor = pos + placeholder.length
        }
    }
    append(template.substring(cursor))
}
