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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution.Reason
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.cancel
import com.svenjacobs.app.leon.shared.resources.retry
import com.svenjacobs.app.leon.shared.resources.short_link_continue_without_resolving
import com.svenjacobs.app.leon.shared.resources.short_link_error_expired
import com.svenjacobs.app.leon.shared.resources.short_link_error_http
import com.svenjacobs.app.leon.shared.resources.short_link_error_offline
import com.svenjacobs.app.leon.shared.resources.short_link_error_timeout
import com.svenjacobs.app.leon.shared.resources.short_link_error_title
import com.svenjacobs.app.leon.shared.resources.short_link_error_unknown
import com.svenjacobs.app.leon.shared.resources.short_link_hint_toast
import com.svenjacobs.app.leon.shared.resources.short_link_resolving_text
import com.svenjacobs.app.leon.shared.resources.short_link_resolving_title
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkState
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * The dialog belonging to [state], if any: progress while resolving, the error after a failure. The
 * main screen and the process text activity on Android share it.
 */
@Composable
fun ShortLinkResolvingDialogs(
    state: ShortLinkState,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    onContinue: () -> Unit,
) {
    when (state) {
        is ShortLinkState.Resolving ->
            ShortLinkProgressDialog(hosts = state.hosts, onCancel = onCancel)
        is ShortLinkState.Failed ->
            ShortLinkErrorDialog(
                reasons = state.failures.values.toList(),
                onRetry = onRetry,
                onContinue = onContinue,
            )
        ShortLinkState.Idle,
        is ShortLinkState.Disabled -> {}
    }
}

/**
 * Shown while short links are being resolved. Contacting a server is what matters for privacy, so
 * the dialog names every host being contacted.
 *
 * A tap outside does not dismiss it, so that resolving is not cancelled by accident; going back
 * does, as an explicit [onCancel].
 */
@Composable
fun ShortLinkProgressDialog(
    hosts: List<String>,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        properties = DialogProperties(dismissOnClickOutside = false),
        title = { Text(stringResource(Res.string.short_link_resolving_title)) },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                Text(
                    stringResource(Res.string.short_link_resolving_text, hosts.joinToString(", ")),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCancel) { Text(stringResource(Res.string.cancel)) }
        },
    )
}

/**
 * Explains why short links could not be resolved: one message for every distinct [reasons], so that
 * several short links failing the same way — all of them offline, say — read as one.
 *
 * Dismissing it counts as [onContinue], which leaves the short links as they are, cleaned offline.
 */
@Composable
fun ShortLinkErrorDialog(
    reasons: List<Reason>,
    onRetry: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onContinue,
        title = { Text(stringResource(Res.string.short_link_error_title)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                reasons.map { message(it) }.distinct().forEach { Text(it) }
            }
        },
        confirmButton = {
            TextButton(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
        },
        dismissButton = {
            TextButton(onClick = onContinue) {
                Text(stringResource(Res.string.short_link_continue_without_resolving))
            }
        },
    )
}

@Composable
private fun message(reason: Reason): String =
    when (reason) {
        Reason.Offline -> stringResource(Res.string.short_link_error_offline)
        Reason.Timeout -> stringResource(Res.string.short_link_error_timeout)
        is Reason.HttpError -> stringResource(Res.string.short_link_error_http, reason.status)
        Reason.Expired -> stringResource(Res.string.short_link_error_expired)
        Reason.NoRedirect,
        Reason.Unknown -> stringResource(Res.string.short_link_error_unknown)
    }

/**
 * Tells the user that a short link was left as it is because resolving short links is turned off,
 * where there is no screen to show the hint on — the process text action on Android.
 */
suspend fun shortLinkHintMessage(): String = getString(Res.string.short_link_hint_toast)
