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
package com.svenjacobs.app.leon.startup

import android.content.Context
import com.svenjacobs.app.leon.core.domain.inject.DomainContainer
import com.svenjacobs.app.leon.inject.AppContainer
import com.svenjacobs.app.leon.sanitizer.SanitizerRepositoryImpl

class ContainerInitializer : DistinctInitializer<Unit> {

    /**
     * The sanitizers themselves come from the catalog in `core-domain`; the app only supplies the
     * repository which remembers which of them the user turned off.
     */
    override fun create(context: Context) {
        AppContainer.init(appContext = context)
        DomainContainer.init(sanitizerRepositoryProvider = { SanitizerRepositoryImpl() })
    }
}
