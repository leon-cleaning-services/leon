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

val RedditSanitizer =
    Sanitizer(
        id = SanitizerId("reddit"),
        name = "Reddit",
        rules = persistentListOf(Rule.RemoveParameters(".*")),
        match = persistentListOf(Match(HostMatch.Domain("reddit.com"))),
    )

val RedditOutSanitizer =
    Sanitizer(
        id = SanitizerId("reddit_out"),
        name = "Reddit (out.reddit.com)",
        rules =
            persistentListOf(
                Rule.Follow(Source.Parameter("url"), persistentListOf(Decode.PercentDecode))
            ),
        match = persistentListOf(Match(HostMatch.Domain("out.reddit.com"))),
    )

val RedditMailSanitizer =
    Sanitizer(
        id = SanitizerId("reddit_mail"),
        name = "Reddit (click.redditmail.com)",
        // The target sits in the path, and the query appended to it is Reddit's
        // newsletter tracking rather than the article's own.
        rules =
            persistentListOf(
                Rule.Follow(
                    from = Source.Path,
                    steps = persistentListOf(Decode.Capture("/[^/]+/(.+)"), Decode.PercentDecode),
                    dropParameters = true,
                )
            ),
        match = persistentListOf(Match(HostMatch.Domain("click.redditmail.com"))),
    )
