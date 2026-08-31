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

val GoogleAnalyticsSanitizer =
    Sanitizer(
        id = SanitizerId("google_analytics"),
        name = "Google Analytics",
        rules = persistentListOf(Rule.RemoveParameters("(?:ga_|utm_|gclid|gad_).*")),
    )

val GoogleStoreSanitizer =
    Sanitizer(
        id = SanitizerId("google_play_store"),
        name = "Google Play Store",
        rules = persistentListOf(Rule.RemoveParameters("hl|selections")),
        match = persistentListOf(Match(HostMatch.Domain("store.google.com"))),
    )

val GoogleAdsSanitizer =
    Sanitizer(
        id = SanitizerId("google_ad_services"),
        name = "Google Ads",
        rules =
            persistentListOf(
                Rule.Follow(Source.Parameter("adurl"), persistentListOf(Decode.PercentDecode))
            ),
        match = persistentListOf(Match(HostMatch.Domain("googleadservices.com"))),
    )

val GoogleSearchSanitizer =
    Sanitizer(
        id = SanitizerId("google_search"),
        name = "Google Search",
        rules =
            persistentListOf(
                Rule.Follow(Source.Parameter("url|q"), persistentListOf(Decode.PercentDecode))
            ),
        match = persistentListOf(Match(HostMatch.Pattern("google\\.[^/?#:]+"), pathPrefix = "/url")),
    )

val GoogleMapsSanitizer =
    Sanitizer(
        id = SanitizerId("google_maps"),
        name = "Google Maps",
        rules =
            persistentListOf(
                Rule.RewriteHost(".*", "www.google.com"),
                Rule.RewritePath(
                    ".*(@-?\\d+\\.\\d+,-?\\d+\\.\\d+,\\d+(?:\\.\\d+)?z).*",
                    "/maps/$1",
                ),
                Rule.RemoveParameters(".*"),
                Rule.RemoveFragment(),
            ),
        match =
            persistentListOf(
                Match(HostMatch.Pattern("google\\.[^/?#:]+"), pathPrefix = "/maps"),
                Match(HostMatch.Domain("maps.google.com")),
            ),
    )
