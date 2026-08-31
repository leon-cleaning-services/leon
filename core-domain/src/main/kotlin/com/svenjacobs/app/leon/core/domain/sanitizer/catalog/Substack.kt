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

val SubstackSanitizer =
    Sanitizer(
        id = SanitizerId("substack"),
        name = "Substack",
        // "open.substack.com/pub/<publication>/…" is an indirection for opening an article in
        // the app; the publication it belongs to is named in the path, and the canonical URL is
        // that publication's own subdomain. Both rewrites find nothing on any other path.
        //
        // Everything else is tracking, most notably "r" (the reader who received the mail) and
        // "token" (a JWT identifying them). The keep-list holds the parameters which *are* the
        // article reference on "app-link" URLs, plus the "j" blob of "/redirect/<uuid>" links,
        // which is undocumented and would leave a dead link if it were dropped.
        rules =
            persistentListOf(
                Rule.RewriteHost("/pub/([^/]+)(?:/.*)?", "$1.substack.com", from = Source.Path),
                Rule.RewritePath("/pub/[^/]+(/.*)?", "$1"),
                Rule.RemoveParameters("publication_id|post_id|note_id|j", negate = true),
            ),
        match = persistentListOf(Match(HostMatch.Subdomains("substack.com"))),
    )
