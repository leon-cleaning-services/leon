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
package com.svenjacobs.app.leon

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.svenjacobs.app.leon.inject.AppContainer.AppDataStoreManager
import com.svenjacobs.app.leon.ui.MainRouter
import com.svenjacobs.app.leon.ui.theme.AppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val sourceText = mutableStateOf<String?>(null)
    private var customTabsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        onIntent(intent)

        setContent {
            AppTheme {
                MainRouter(sourceText = sourceText, onResetClick = { sourceText.value = null })
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppDataStoreManager.customTabsEnabled.collect { customTabsEnabled ->
                    if (customTabsEnabled) {
                        setupCustomTabsService()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        onIntent(intent)
    }

    // TODO: Pass all Intent extras
    private fun onIntent(intent: Intent) {
        sourceText.value =
            when (intent.action) {
                Intent.ACTION_SEND ->
                    if (intent.type == MIME_TYPE_TEXT_PLAIN) {
                        intent.getStringExtra(Intent.EXTRA_TEXT)
                    } else {
                        null
                    }

                Intent.ACTION_VIEW ->
                    if (intent.scheme.orEmpty().startsWith("http")) {
                        intent.dataString
                    } else {
                        null
                    }

                else -> null
            }
    }

    private fun setupCustomTabsService() {
        if (customTabsInitialized) return

        val connection =
            object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(
                    name: ComponentName,
                    client: CustomTabsClient,
                ) {
                    client.warmup(0)
                }

                override fun onServiceDisconnected(name: ComponentName?) {}
            }

        val packageName = CustomTabsClient.getPackageName(this, null)
        if (packageName != null) {
            CustomTabsClient.bindCustomTabsService(this, packageName, connection)
        }

        customTabsInitialized = true
    }

    private companion object {
        private const val MIME_TYPE_TEXT_PLAIN = "text/plain"
    }
}
