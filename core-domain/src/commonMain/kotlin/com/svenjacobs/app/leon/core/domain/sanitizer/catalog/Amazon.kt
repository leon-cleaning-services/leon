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
package com.svenjacobs.app.leon.core.domain.sanitizer.catalog

import com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch
import com.svenjacobs.app.leon.core.domain.sanitizer.Match
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.sanitizer.Source
import kotlinx.collections.immutable.persistentListOf

val Amazon =
    Sanitizer(
        id = SanitizerId("amazon2"),
        name = "Amazon",
        rules = persistentListOf(Rule.RemoveParameters("ref_?")),
        match = persistentListOf(Match(HostMatch.Pattern("amazon\\.[^/?#:]+"), pathPrefix = "/")),
    )

val AmazonProduct =
    Sanitizer(
        id = SanitizerId("amazon"),
        name = "Amazon Products",
        rules =
            persistentListOf(
                Rule.RewritePath(".*/(?:dp?|gp/product)/([^/?&]*).*", "/dp/$1/"),
                Rule.RemoveParameters(".*"),
            ),
        match = persistentListOf(Match(HostMatch.Pattern("amazon\\.[^/?#:]+"))),
    )

val AmazonSponsoredProduct =
    Sanitizer(
        id = SanitizerId("amazonSponsoredProduct"),
        name = "Amazon Sponsored Products",
        // The product page is not the URL itself but hidden, percent-encoded, in its "url"
        // parameter: /sspa/click?...&url=%2F...%2Fdp%2F<asin>%2F...
        rules =
            persistentListOf(
                Rule.RewritePath(
                    pattern = "(?i).*%2F(?:dp?|gp%2Fproduct)%2F([^%]*).*",
                    replacement = "/dp/$1/",
                    from = Source.Parameter("url"),
                ),
                Rule.RemoveParameters(".*"),
            ),
        match =
            persistentListOf(
                Match(HostMatch.Pattern("amazon\\.[^/?#:]+"), pathPrefix = "/sspa/click")
            ),
    )
