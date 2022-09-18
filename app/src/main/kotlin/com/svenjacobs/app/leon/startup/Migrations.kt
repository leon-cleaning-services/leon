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
import androidx.core.content.ContextCompat
import java.io.File
import javax.inject.Inject
import timber.log.Timber

@Deprecated(message = "Remove this in a future version")
class Migrations @Inject constructor() {

	fun migrate(context: Context) {
		try {
			// Delete obsolete database files from previous app version
			val dataDir = ContextCompat.getDataDir(context) ?: return
			val databases = File(dataDir, "databases")

			databases.listFiles()
				?.filter { file -> file.name.startsWith("leon") }
				?.forEach { file -> file.delete() }
		} catch (e: Exception) {
			Timber.e(e)
		}
	}
}
