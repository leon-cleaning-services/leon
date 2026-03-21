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
package com.svenjacobs.app.leon.core.domain.sanitizer.reddit

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class RedditOutSanitizerTest :
    WordSpec({
        val sanitizer = RedditOutSanitizer()

        "invoke" should
            {
                "extract URL" {
                    val result =
                        sanitizer(
                            "https://out.reddit.com/t3_11zcpau?url=https%3A%2F%2Fcompress-or-die.co" +
                                "m%2FThe-nasty-red-JPG-compression-artifacts&token=AQAA-odsZCyQ04Ae10crjv" +
                                "g8DGlsTPckMpu3vvIjNwmWPgLdQMbC&app_name=web2x&web_redirect=true/"
                        )

                    result shouldBe
                        "https://compress-or-die.com/The-nasty-red-JPG-compression-artifacts"
                }
            }

        "matchesDomain" should
            {
                "match for out.reddit.com" {
                    sanitizer.matchesDomain("https://out.reddit.com") shouldBe true
                }

                "not match for reddit.com" {
                    sanitizer.matchesDomain("https://reddit.com") shouldBe false
                }
            }
    })
