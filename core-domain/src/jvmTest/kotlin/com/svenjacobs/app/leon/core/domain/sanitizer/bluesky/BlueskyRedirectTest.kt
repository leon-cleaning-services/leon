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
package com.svenjacobs.app.leon.core.domain.sanitizer.bluesky

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.BlueskyRedirect
import io.kotest.matchers.shouldBe

class BlueskyRedirectTest :
    SanitizerSpec(
        BlueskyRedirect,
        {
            "clean" should
                {
                    "extract URL from Bluesky redirect link" {
                        clean(
                            "https://go.bsky.app/redirect?u=https%3A%2F%2Fexample.com%2Fsome%2Fpath%3Ffoo%3Dbar"
                        ) shouldBe "https://example.com/some/path?foo=bar"
                    }
                }

            "matches" should
                {
                    "match go.bsky.app/redirect" {
                        matches("https://go.bsky.app/redirect?u=https%3A%2F%2Fexample.com") shouldBe
                            true
                    }

                    "not match other domains" {
                        matches("https://bsky.app/profile/user") shouldBe false
                    }
                }
        },
    )
