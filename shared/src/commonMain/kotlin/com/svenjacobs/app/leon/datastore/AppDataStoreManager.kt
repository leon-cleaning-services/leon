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
package com.svenjacobs.app.leon.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import com.svenjacobs.app.leon.ui.model.AutoReset
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Manages app specific preferences stored via [DataStore]. */
@Inject
@SingleIn(AppScope::class)
class AppDataStoreManager(@Named("settings") private val dataStore: DataStore<Preferences>) {

    suspend fun setVersionCode(versionCode: Int) {
        dataStore.edit { it[KEY_VERSION_CODE] = versionCode }
    }

    suspend fun setActionAfterClean(actionAfterClean: ActionAfterClean) {
        dataStore.edit { it[KEY_ACTION_AFTER_CLEAN] = actionAfterClean.name }
    }

    val actionAfterClean: Flow<ActionAfterClean?> =
        dataStore.data.map { preferences ->
            runCatching { preferences[KEY_ACTION_AFTER_CLEAN]?.let(ActionAfterClean::valueOf) }
                .getOrNull()
        }

    suspend fun setUrlDecodeEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_URL_DECODE] = enabled }
    }

    val urlDecodeEnabled: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_URL_DECODE] ?: false }

    suspend fun setExtractUrlEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_EXTRACT_URL] = enabled }
    }

    val extractUrlEnabled: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_EXTRACT_URL] ?: false }

    suspend fun setCustomTabsEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_CUSTOM_TABS] = enabled }
    }

    val customTabsEnabled: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_CUSTOM_TABS] ?: false }

    suspend fun setProtectScreenEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_PROTECT_SCREEN] = enabled }
    }

    val protectScreenEnabled: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_PROTECT_SCREEN] ?: false }

    suspend fun setAutoReset(autoReset: AutoReset) {
        dataStore.edit { it[KEY_AUTO_RESET] = autoReset.name }
    }

    val autoReset: Flow<AutoReset?> =
        dataStore.data.map { preferences ->
            runCatching { preferences[KEY_AUTO_RESET]?.let(AutoReset::valueOf) }.getOrNull()
        }

    /**
     * The id of the last text the main screen was given and the wall-clock time in epoch
     * milliseconds at which it arrived. Auto-reset measures its timeout from this.
     */
    data class LastInput(val id: String, val at: Long)

    suspend fun setLastInput(id: String, at: Long) {
        dataStore.edit {
            it[KEY_LAST_INPUT_ID] = id
            it[KEY_LAST_INPUT_AT] = at
        }
    }

    val lastInput: Flow<LastInput?> =
        dataStore.data.map { preferences ->
            val id = preferences[KEY_LAST_INPUT_ID]
            val at = preferences[KEY_LAST_INPUT_AT]
            if (id != null && at != null) LastInput(id, at) else null
        }

    suspend fun setHistoryEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_HISTORY_ENABLED] = enabled }
    }

    val historyEnabled: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_HISTORY_ENABLED] ?: true }

    private companion object {
        private val KEY_VERSION_CODE = intPreferencesKey("version_code")
        private val KEY_ACTION_AFTER_CLEAN = stringPreferencesKey("action_after_clean")
        private val KEY_URL_DECODE = booleanPreferencesKey("url_decode")
        private val KEY_EXTRACT_URL = booleanPreferencesKey("extract_url")
        private val KEY_CUSTOM_TABS = booleanPreferencesKey("custom_tabs")
        private val KEY_PROTECT_SCREEN = booleanPreferencesKey("protect_screen")
        private val KEY_AUTO_RESET = stringPreferencesKey("auto_reset")
        private val KEY_LAST_INPUT_ID = stringPreferencesKey("last_input_id")
        private val KEY_LAST_INPUT_AT = longPreferencesKey("last_input_at")
        private val KEY_HISTORY_ENABLED = booleanPreferencesKey("history_enabled")
    }
}
