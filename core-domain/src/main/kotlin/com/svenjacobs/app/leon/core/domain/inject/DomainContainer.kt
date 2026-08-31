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
package com.svenjacobs.app.leon.core.domain.inject

import com.svenjacobs.app.leon.core.domain.sanitizer.AlwaysEnabled
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizersCollection
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AllSanitizers

/**
 * Holds what `Cleaner` needs.
 *
 * Both are usable without [init]: a caller which has nowhere to store the user's preferences — a
 * command line front end, a test — gets the complete catalog with every sanitizer enabled. The app
 * calls [init] to plug in its own repository.
 */
object DomainContainer {

    fun init(
        sanitizerRepositoryProvider: () -> SanitizerRepository,
        sanitizers: SanitizersCollection = AllSanitizers,
    ) {
        this.SanitizerRepositoryProvider = sanitizerRepositoryProvider
        this.Sanitizers = sanitizers
    }

    private var SanitizerRepositoryProvider: () -> SanitizerRepository = { AlwaysEnabled }

    var Sanitizers: SanitizersCollection = AllSanitizers
        private set

    val SanitizerRepository: SanitizerRepository by lazy { SanitizerRepositoryProvider() }
}
