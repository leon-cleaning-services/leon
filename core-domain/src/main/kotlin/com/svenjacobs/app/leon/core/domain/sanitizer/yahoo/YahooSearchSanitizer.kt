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

package com.svenjacobs.app.leon.core.domain.sanitizer.yahoo

import android.content.Context
import com.svenjacobs.app.leon.core.common.domain.matchesDomain
import com.svenjacobs.app.leon.core.common.regex.RegexFactory
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.sanitizer.extractSearchResultValue

class YahooSearchSanitizer : Sanitizer {

    override val id = SanitizerId("yahoo_search")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_yahoo_search_name),
    )

    override fun matchesDomain(input: String) = input.matchesDomain("search.yahoo.com")

    override fun invoke(input: String): String = if (input.contains("/search")) {
        RegexFactory.exceptParameter("p").replace(input, "")
    } else {
        extractSearchResultValue(REGEX_SEARCH_RESULT, input)
    }

    companion object {
        private val REGEX_SEARCH_RESULT = Regex("RU=([^/]+)")
    }
}
