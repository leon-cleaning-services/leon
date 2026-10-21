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
package com.svenjacobs.app.leon.db

import androidx.room3.Entity

/**
 * A choice the user made about one parameter on one site, see
 * [SitePreference][com.svenjacobs.app.leon.core.domain.preference.SitePreference].
 */
@Entity(tableName = "site_parameter", primaryKeys = ["site", "parameter"])
data class SiteParameter(
    /** See [Url.site][com.svenjacobs.app.leon.core.domain.preference.site]. */
    val site: String,
    /** The name of the parameter. */
    val parameter: String,
    val decision: Decision,
) {

    enum class Decision {
        /** Removed, although no sanitizer proposes it. */
        REMOVE,

        /** Kept, although a sanitizer proposes removing it. */
        KEEP,
    }
}
