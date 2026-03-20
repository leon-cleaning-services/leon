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

package com.svenjacobs.app.leon.core.domain.sanitizer

import android.content.Context
import androidx.compose.runtime.Composable

/**
 * Performs sanitization of URLs.
 *
 * Might just remove a few parameters or completely rewrite an URL.
 */
interface Sanitizer {

    data class Metadata(val name: String, val hasSettingsScreen: Boolean = false)

    val id: SanitizerId

    fun getMetadata(context: Context): Metadata

    fun matchesDomain(input: String): Boolean = true

    operator fun invoke(input: String): String

    @Composable
    @Suppress("unused")
    fun SettingsScreen() {
    }
}
