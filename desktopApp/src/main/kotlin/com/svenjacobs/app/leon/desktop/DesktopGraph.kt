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
package com.svenjacobs.app.leon.desktop

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.svenjacobs.app.leon.core.domain.Cleaner
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.db.AppDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import java.io.File
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath

@DependencyGraph(AppScope::class)
interface DesktopGraph : ViewModelGraph {

    val appDataStoreManager: AppDataStoreManager
    val cleaner: Cleaner

    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(dataDir: File): AppDatabase =
        Room.databaseBuilder<AppDatabase>(File(dataDir, "leon.db").absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

    @Provides
    @SingleIn(AppScope::class)
    @Named("settings")
    fun provideSettingsDataStore(dataDir: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath {
            File(dataDir, "settings.preferences_pb").absolutePath.toPath()
        }

    @Provides
    @SingleIn(AppScope::class)
    @Named("sanitizers")
    fun provideSanitizersDataStore(dataDir: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath {
            File(dataDir, "sanitizers.preferences_pb").absolutePath.toPath()
        }

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides dataDir: File): DesktopGraph
    }
}
