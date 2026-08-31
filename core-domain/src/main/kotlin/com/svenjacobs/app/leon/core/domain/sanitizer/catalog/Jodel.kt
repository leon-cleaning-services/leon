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

val JodelSanitizer =
    Sanitizer(
        id = SanitizerId("jodel"),
        name = "Jodel",
        // The share link hides the real URL in a base64 encoded JSON payload. The capture
        // is needed because a Jodel link carries a second "?" inside its own query.
        rules =
            persistentListOf(
                Rule.Follow(
                    from = Source.Parameter("data"),
                    steps =
                        persistentListOf(
                            Decode.Capture("([^?]*)"),
                            Decode.PercentDecode,
                            Decode.Base64Decode,
                            Decode.JsonField("\$android_url"),
                        ),
                )
            ),
        match = persistentListOf(Match(HostMatch.Domain("shared.jodel.com"))),
    )
