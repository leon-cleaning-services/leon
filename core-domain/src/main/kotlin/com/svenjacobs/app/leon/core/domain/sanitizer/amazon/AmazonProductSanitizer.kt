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

package com.svenjacobs.app.leon.core.domain.sanitizer.amazon

import android.content.Context
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

class AmazonProductSanitizer : Sanitizer {

    override val id = SanitizerId("amazon")

    override fun getMetadata(context: Context) = Sanitizer.Metadata(
        name = context.getString(R.string.sanitizer_amazon_product_name),
    )

    override fun matchesDomain(input: String) = REGEX.containsMatchIn(input)

    override fun invoke(input: String): String {
        val result = REGEX.find(input)
        // First group contains domain and protocol like https://www.amazon.com
        val domainGroup = result?.groups?.get(1) ?: return input
        // Second group contains product id
        val productIdGroup = result.groups[2] ?: return input

        return "${domainGroup.value}/dp/${productIdGroup.value}/"
    }

    private companion object {
        private val REGEX =
            Regex("((?:https?://)?(?:www\\.)?amazon\\.[^/]*).*/(?:dp?|gp/product)?/([^/?&]*)")
    }
}
