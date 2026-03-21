/*
 * Léon - The URL Cleaner
 * Copyright (C) 2023 Sven Jacobs
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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.svenjacobs.app.leon.core.domain.inject.DomainContainer.AppContext
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Manages [Sanitizer] specific preferences stored via [DataStore]. */
class SanitizerDataStoreManager(private val context: Context = AppContext) {
    private val Context.dataStore by preferencesDataStore(name = "sanitizers")

    internal fun preferencesKey(id: String) = booleanPreferencesKey(name = "sanitizer_$id")

    val data: Flow<Preferences>
        get() = context.dataStore.data

    suspend fun setSanitizerEnabled(id: String, enabled: Boolean) {
        val key = preferencesKey(id)
        context.dataStore.edit { it[key] = enabled }
    }

    fun isSanitizerEnabled(id: String): Flow<Boolean?> {
        val key = preferencesKey(id)
        return context.dataStore.data.map { it[key] }
    }
}
