/*
 * Léon - The URL Cleaner
 * Copyright (C) 2022 Sven Jacobs
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

package com.svenjacobs.app.leon.startup

import android.content.Context
import androidx.startup.Initializer
import com.svenjacobs.app.leon.BuildConfig
import com.svenjacobs.app.leon.datastore.AppDataStoreManager
import kotlinx.coroutines.runBlocking

/**
 * Performs (initial) initialization of app.
 */
@Suppress("unused")
class AppInitializer : Initializer<Unit> {

	override fun create(context: Context) {
		val appDataStoreManager = AppDataStoreManager()
		val stethoHelper = StethoHelper()

		stethoHelper.initialize(context)

		runBlocking {
			appDataStoreManager.setVersionCode(BuildConfig.VERSION_CODE)
		}
	}

	override fun dependencies() = listOf(
		ComponentInitializer::class.java,
		TimberInitializer::class.java,
	)
}
