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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.svenjacobs.app.leon.AppConstants
import com.svenjacobs.app.leon.BuildConfig
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews

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

    val developerTemplate = stringResource(R.string.about_developer)
    val contributorsText = stringResource(R.string.about_contributors)

    val licenseTemplate = stringResource(R.string.about_license)
    val bugsTemplate = stringResource(R.string.about_bugs)
    val sponsorTemplate = stringResource(R.string.about_sponsor)

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.about)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = developerTemplate,
                            linkStyles = linkStyles,
                            "Sven Jacobs" to AppConstants.AUTHOR_URL,
                            contributorsText to AppConstants.CONTRIBUTORS_URL,
                        ),
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = licenseTemplate,
                            linkStyles = linkStyles,
                            AppConstants.GITHUB_URL to AppConstants.GITHUB_URL,
                        ),
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = bugsTemplate,
                            linkStyles = linkStyles,
                            AppConstants.ISSUES_URL to AppConstants.ISSUES_URL,
                        ),
                )

                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text =
                        buildLinkedAnnotatedString(
                            template = sponsorTemplate,
                            linkStyles = linkStyles,
                            AppConstants.SPONSORS_URL to AppConstants.SPONSORS_URL,
                        ),
                )

                Text(text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.close)) }
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
        val placeholder = "%${index + 1}\$s"
        val pos = template.indexOf(placeholder, cursor)
        if (pos >= 0) {
            append(template.substring(cursor, pos))
            withLink(LinkAnnotation.Url(url, linkStyles)) { append(displayText) }
            cursor = pos + placeholder.length
        }
    }
    append(template.substring(cursor))
}

@Composable
@DayNightPreviews
private fun AboutDialogPreview() {
    AppTheme { AboutDialog(onDismissRequest = {}) }
}
