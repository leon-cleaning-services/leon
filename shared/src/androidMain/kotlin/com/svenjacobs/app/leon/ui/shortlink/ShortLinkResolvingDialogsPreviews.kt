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
package com.svenjacobs.app.leon.ui.shortlink

import androidx.compose.runtime.Composable
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution.Reason
import com.svenjacobs.app.leon.ui.theme.AppTheme
import com.svenjacobs.app.leon.ui.tooling.DayNightPreviews

@DayNightPreviews
@Composable
private fun ShortLinkProgressDialogPreview() {
    AppTheme { ShortLinkProgressDialog(hosts = listOf("www.tiktok.com"), onCancel = {}) }
}

@DayNightPreviews
@Composable
private fun ShortLinkProgressDialogSeveralHostsPreview() {
    AppTheme {
        ShortLinkProgressDialog(
            hosts = listOf("www.tiktok.com", "a.co", "maps.app.goo.gl"),
            onCancel = {},
        )
    }
}

@Composable
private fun ErrorDialog(vararg reasons: Reason) {
    AppTheme { ShortLinkErrorDialog(reasons = reasons.toList(), onRetry = {}, onContinue = {}) }
}

@DayNightPreviews
@Composable
private fun ShortLinkErrorDialogOfflinePreview() {
    ErrorDialog(Reason.Offline)
}

@DayNightPreviews
@Composable
private fun ShortLinkErrorDialogTimeoutPreview() {
    ErrorDialog(Reason.Timeout)
}

@DayNightPreviews
@Composable
private fun ShortLinkErrorDialogHttpErrorPreview() {
    ErrorDialog(Reason.HttpError(403))
}

@DayNightPreviews
@Composable
private fun ShortLinkErrorDialogNoRedirectPreview() {
    ErrorDialog(Reason.NoRedirect)
}

@DayNightPreviews
@Composable
private fun ShortLinkErrorDialogExpiredPreview() {
    ErrorDialog(Reason.Expired)
}

@DayNightPreviews
@Composable
private fun ShortLinkErrorDialogUnknownPreview() {
    ErrorDialog(Reason.Unknown)
}

@DayNightPreviews
@Composable
private fun ShortLinkErrorDialogSeveralFailuresPreview() {
    ErrorDialog(Reason.Offline, Reason.HttpError(404), Reason.Offline, Reason.Expired)
}
