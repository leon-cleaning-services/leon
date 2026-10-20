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
package com.svenjacobs.app.leon

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.ui.shortlink.ShortLinkResolvingDialogs
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkCommand
import com.svenjacobs.app.leon.ui.shortlink.model.ShortLinkState
import com.svenjacobs.app.leon.ui.shortlink.model.resolveInteractively
import com.svenjacobs.app.leon.ui.shortlink.shortLinkHintMessage
import com.svenjacobs.app.leon.ui.theme.AppTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Cleans the text selected in another app, or passed in by one, and hands it back.
 *
 * The activity is translucent: it has nothing to show unless a short link is resolved, which shows
 * the same progress and error dialogs as the main screen on top of the calling app.
 */
@RequiresApi(Build.VERSION_CODES.M)
class ProcessTextActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent.action) {
            ACTION_CLEAN -> actionClean()
            Intent.ACTION_PROCESS_TEXT -> actionProcessText()
            else -> cancel()
        }
    }

    private fun actionClean() {
        val text = intent.getStringExtra(Intent.EXTRA_TEXT)

        when {
            text.isNullOrBlank() -> cancel()
            else -> result(text, Intent.EXTRA_TEXT)
        }
    }

    private fun actionProcessText() {
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)

        when {
            text.isNullOrBlank() -> cancel()

            // If readonly, delegate to MainActivity
            readonly -> {
                startActivity(
                    Intent(this, MainActivity::class.java).apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    },
                )
                finish()
            }

            else -> result(text, Intent.EXTRA_PROCESS_TEXT)
        }
    }

    /**
     * Cleans [text], returns it as [key] and finishes.
     *
     * Resolving a short link may take seconds, so this must not block the main thread. setResult()
     * still reaches the caller as long as it comes before finish().
     */
    private fun result(text: String, key: String) {
        lifecycleScope.launch {
            val cleanedText = clean(text)
            setResult(RESULT_OK, Intent().apply { putExtra(key, cleanedText) })
            finish()
        }
    }

    private suspend fun clean(text: String): String {
        val appDataStoreManager = appGraph.appDataStoreManager
        val decodeUrl = appDataStoreManager.urlDecodeEnabled.first()
        val shortLinks = appGraph.cleaner.shortLinks(text)

        val resolved =
            when {
                shortLinks.isEmpty() -> emptyMap()

                // There is no screen to show the hint on, so a toast has to do.
                !appDataStoreManager.resolveShortLinksEnabled.first() -> {
                    Toast.makeText(applicationContext, shortLinkHintMessage(), Toast.LENGTH_LONG)
                        .show()
                    emptyMap()
                }

                else -> resolve(shortLinks)
            }

        return appGraph.cleaner
            .clean(text = text, decodeUrl = decodeUrl, resolved = resolved)
            .cleanedText
    }

    /**
     * Resolves [urls] while showing the progress dialog, and the error dialog when that fails.
     * Cancelling, or continuing without resolving, keeps the short links as they are.
     */
    private suspend fun resolve(urls: List<Url>): Map<Url, Url> {
        val commands = Channel<ShortLinkCommand>(Channel.CONFLATED)
        var state by mutableStateOf<ShortLinkState>(ShortLinkState.Idle)

        setContent {
            AppTheme {
                ShortLinkResolvingDialogs(
                    state = state,
                    onCancel = { commands.trySend(ShortLinkCommand.Cancel) },
                    onRetry = { commands.trySend(ShortLinkCommand.Retry) },
                    onContinue = { commands.trySend(ShortLinkCommand.Continue) },
                )
            }
        }

        return appGraph.shortLinkResolver.resolveInteractively(urls = urls, commands = commands) {
            state = it.state
        }
    }

    private fun cancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private companion object {
        private const val ACTION_CLEAN = "com.svenjacobs.app.leon.CLEAN"
    }
}
