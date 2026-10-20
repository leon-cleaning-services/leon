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
import kotlinx.collections.immutable.persistentListOf

val Tiktok =
    Sanitizer(
        id = SanitizerId("tiktok"),
        name = "TikTok",
        rules = persistentListOf(Rule.RemoveParameters(".*")),
        match = persistentListOf(Match(HostMatch.Domain("tiktok.com"))),
    )

val TiktokShortLink =
    Sanitizer(
        id = SanitizerId("tiktok_short_link"),
        name = "TikTok Short Link",
        // tiktok.com and tt.site keep their short links under /t/, next to the site's own pages;
        // vm. and vt.tiktok.com serve nothing but short links. A code is never a single character,
        // which keeps a bare /t/ from passing as the code "t".
        rules = persistentListOf(Rule.Resolve("(?:/t)?/[A-Za-z0-9_-]{2,}/?")),
        match =
            persistentListOf(
                Match(HostMatch.Domain("tiktok.com"), pathPrefix = "/t/"),
                Match(HostMatch.Domain("tt.site"), pathPrefix = "/t/"),
                Match(HostMatch.Domain("vm.tiktok.com")),
                Match(HostMatch.Domain("vt.tiktok.com")),
            ),
    )
