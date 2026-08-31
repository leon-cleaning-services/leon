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

val YoutubeSanitizer =
    Sanitizer(
        id = SanitizerId("youtube"),
        name = "YouTube",
        rules =
            persistentListOf(
                Rule.RemoveParameters("(v|search_query|list|t|channel_id)", negate = true)
            ),
        match =
            persistentListOf(
                Match(HostMatch.Pattern("(?:m(?:usic)?\\.)?youtube\\.com")),
                Match(HostMatch.Domain("youtu.be")),
            ),
    )

val YoutubeRedirectSanitizer =
    Sanitizer(
        id = SanitizerId("youtube_redirect"),
        name = "YouTube Redirect",
        rules =
            persistentListOf(
                Rule.Follow(Source.Parameter("q"), persistentListOf(Decode.PercentDecode))
            ),
        match = persistentListOf(Match(HostMatch.Domain("youtube.com"), pathPrefix = "/redirect")),
    )

val YoutubeMusicSanitizer =
    Sanitizer(
        id = SanitizerId("youtube_music"),
        name = "YouTube Music",
        rules = persistentListOf(Rule.RewriteHost("music\\.youtube\\.com", "youtube.com")),
        match = persistentListOf(Match(HostMatch.Domain("music.youtube.com"))),
    )

val YoutubeShortUrlSanitizer =
    Sanitizer(
        id = SanitizerId("youtube_short_url"),
        name = "Youtu.be",
        // The whole target is built out of the video id in the path.
        rules =
            persistentListOf(
                Rule.Follow(
                    from = Source.Path,
                    steps =
                        persistentListOf(
                            Decode.Capture("/(.+)", "https://www.youtube.com/watch?v=$1")
                        ),
                )
            ),
        match = persistentListOf(Match(HostMatch.Domain("youtu.be"))),
    )
