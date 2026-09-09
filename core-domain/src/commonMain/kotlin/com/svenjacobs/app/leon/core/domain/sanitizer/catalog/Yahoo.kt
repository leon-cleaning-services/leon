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

import com.svenjacobs.app.leon.core.domain.sanitizer.Decode
import com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch
import com.svenjacobs.app.leon.core.domain.sanitizer.Match
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.sanitizer.Source
import kotlinx.collections.immutable.persistentListOf

val YahooReferrer =
    Sanitizer(
        id = SanitizerId("yahooReferrer"),
        name = "Yahoo Referrer",
        rules =
            persistentListOf(Rule.RemoveParameters("guccounter|guce_referrer|guce_referrer_sig")),
    )

val YahooSearch =
    Sanitizer(
        id = SanitizerId("yahoo_search"),
        name = "Yahoo Search",
        // A search results page (/search?p=...) only keeps its query; every other URL under this
        // host is a redirect whose target is embedded in the path as .../RU=<url>/... — the two
        // rules are mutually exclusive in practice, each finding nothing to do on the other's URLs.
        rules =
            persistentListOf(
                Rule.RemoveParameters("p", negate = true),
                Rule.Follow(
                    Source.Path,
                    persistentListOf(Decode.Capture("RU=([^/]+)"), Decode.PercentDecode),
                ),
            ),
        match = persistentListOf(Match(HostMatch.Subdomains("search.yahoo.com"))),
    )
