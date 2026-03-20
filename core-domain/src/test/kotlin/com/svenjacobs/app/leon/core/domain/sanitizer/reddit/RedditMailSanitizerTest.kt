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

class RedditMailSanitizerTest :
    WordSpec(
        {
            val sanitizer = RedditMailSanitizer()

            "invoke" should {

                "extract and decode URL" {
                    val result = sanitizer(
                        "https://click.redditmail.com/CL0/https:%2F%2Fwww.reddit.com%2Fr%2FComp" +
                            "ressOrDie%2Fcomments%2F11u2vso%2Frcompressordie_lounge%2Fjl9fp68%2F%3F\$" +
                            "deep_link=true%26correlation_id=5329d6c9-34a4-4a44-9cea-76317f68123f%26r" +
                            "ef=email_comment_reply%26ref_campaign=email_comment_reply%26ref_source=e" +
                            "mail/3/010001884768a910-4b97a265-36d8-461f-9d79-fc2b535e5217-000000/sa_u" +
                            "FF6uMCdJu1cTLCaOI8Ng6wQBjfPtc5hMCnOrx4Q=301",
                    )

                    result shouldBe
                        "https://www.reddit.com/r/CompressOrDie/comments/11u2vso/rcompres" +
                        "sordie_lounge/jl9fp68/"
                }
            }

            "matchesDomain" should {

                "match for click.redditmail.com" {
                    sanitizer.matchesDomain("https://click.redditmail.com") shouldBe true
                }
            }
        },
    )
