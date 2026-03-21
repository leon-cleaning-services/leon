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

import com.svenjacobs.app.leon.core.common.url.decodeUrl

/**
 * Base class for sanitizers that extract URLs from search engine result links.
 *
 * @param regex Regular expression which must return the URL in the first group
 */
abstract class SearchResultSanitizer(private val regex: Regex) : Sanitizer {

    override fun invoke(input: String): String = extractSearchResultValue(regex, input)
}

internal fun extractSearchResultValue(regex: Regex, input: String): String {
    val result = regex.find(input) ?: return input
    val group = result.groups[1] ?: return input
    return decodeUrl(group.value)
}
