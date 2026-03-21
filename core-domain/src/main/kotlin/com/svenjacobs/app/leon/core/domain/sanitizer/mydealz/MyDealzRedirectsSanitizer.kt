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
package com.svenjacobs.app.leon.core.domain.sanitizer.mydealz

import android.content.Context
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class MyDealzRedirectsSanitizer : Sanitizer {

    override val id = SanitizerId("mydealz_redirects")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = context.getString(R.string.sanitizer_mydealz_redirects_name))

    override fun matchesDomain(input: String) = MyDealzDomains.DOMAINS_REGEX.containsMatchIn(input)

    override fun invoke(input: String): String {
        val groupValues =
            DEAL_REGEX.matchEntire(input)?.groupValues
                ?: return input // URL is not in redirect format, pass through
        val host =
            groupValues.getOrNull(1)
                ?: throw IllegalArgumentException("Could not extract host from MyDealz URL")
        val deal =
            groupValues.getOrNull(2)
                ?: throw IllegalArgumentException("Could not extract deal from MyDealz URL")
        return "https://$host/deals/a-$deal"
    }

    private companion object {
        private val DEAL_REGEX = Regex("https://([^/]+)/share-deal-from-app/(.+)\$")
    }
}
