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
package com.svenjacobs.app.leon.inject

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import com.svenjacobs.app.leon.datastore.SanitizerDataStoreManager
import com.svenjacobs.app.leon.db.AppDatabase
import com.svenjacobs.app.leon.db.HistoryDao

object AppContainer {

    fun init(appContext: Context) {
        this.AppContext = appContext
    }

    lateinit var AppContext: Context
        private set

    val AppDataStoreManager: AppDataStoreManager by lazy { AppDataStoreManager() }
    val SanitizerDataStoreManager: SanitizerDataStoreManager by lazy { SanitizerDataStoreManager() }

    val AppDatabase: AppDatabase by lazy {
        Room.databaseBuilder<AppDatabase>(AppContext, "leon")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    val HistoryDao: HistoryDao by lazy { AppDatabase.historyDao() }
}
