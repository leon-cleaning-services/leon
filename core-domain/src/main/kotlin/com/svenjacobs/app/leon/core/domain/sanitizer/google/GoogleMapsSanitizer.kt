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

package com.svenjacobs.app.leon.core.domain.sanitizer.google

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomainRegex
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class GoogleMapsSanitizer : Sanitizer {

    override val id = SanitizerId("google_maps")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_google_maps_name),
    )

    override fun matchesDomain(input: String) = input.matchesDomainRegex("google\\.com/maps") ||
        input.matchesDomainRegex("maps\\.google\\.com")

    override fun invoke(input: String): String {
        val match = COORD_REGEX.find(input) ?: return input
        val scheme = if (input.startsWith("https://")) "https" else "http"
        return "$scheme://www.google.com/maps/${match.value}"
    }

    private companion object {
        private val COORD_REGEX = Regex("@-?\\d+\\.\\d+,-?\\d+\\.\\d+,\\d+(?:\\.\\d+)?z")
    }
}
