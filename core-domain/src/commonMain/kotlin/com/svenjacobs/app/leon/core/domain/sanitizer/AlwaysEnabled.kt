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
package com.svenjacobs.app.leon.core.domain.sanitizer

import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A repository in which every sanitizer is enabled and nothing can be turned off.
 *
 * The default for a caller which has no place to store the user's preferences, such as a command
 * line front end. The app replaces it with a repository backed by its settings.
 */
object AlwaysEnabled : SanitizerRepository {

    override val state:
        Flow<kotlinx.collections.immutable.ImmutableList<SanitizerRepository.SanitizerState>> =
        flowOf(persistentListOf())

    override suspend fun isEnabled(id: SanitizerId) = true

    override suspend fun setEnabled(id: SanitizerId, enabled: Boolean) = Unit
}
