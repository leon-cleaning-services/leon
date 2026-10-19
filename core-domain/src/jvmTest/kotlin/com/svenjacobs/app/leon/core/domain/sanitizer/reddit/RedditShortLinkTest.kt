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
package com.svenjacobs.app.leon.core.domain.sanitizer.reddit

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.RedditShortLink
import io.kotest.matchers.shouldBe

class RedditShortLinkTest :
    SanitizerSpec(
        RedditShortLink,
        {
            "clean" should
                {
                    "replace a resolved short link with its target" {
                        clean(
                            "https://reddit.com/r/fossdroid/s/Wptky3Ma2I",
                            resolved =
                                mapOf(
                                    "https://reddit.com/r/fossdroid/s/Wptky3Ma2I" to
                                        "https://www.reddit.com/r/fossdroid/comments/1abcdef/title/",
                                ),
                        ) shouldBe "https://www.reddit.com/r/fossdroid/comments/1abcdef/title/"
                    }

                    "keep a short link which was not resolved" {
                        clean("https://www.reddit.com/r/fossdroid/s/Wptky3Ma2I") shouldBe
                            "https://www.reddit.com/r/fossdroid/s/Wptky3Ma2I"
                    }
                }

            "isShortLink" should
                {
                    "recognize a share link" {
                        isShortLink("https://reddit.com/r/fossdroid/s/Wptky3Ma2I") shouldBe true
                        isShortLink("https://www.reddit.com/r/fossdroid/s/Wptky3Ma2I/") shouldBe
                            true
                        isShortLink("https://www.reddit.com/r/fossdroid/s/Wpt_ky3-Ma2I") shouldBe
                            true
                    }

                    "recognize a share link on a subdomain" {
                        isShortLink("https://old.reddit.com/r/fossdroid/s/Wptky3Ma2I") shouldBe true
                    }

                    "not consider a post a short link" {
                        isShortLink(
                            "https://www.reddit.com/r/fossdroid/comments/1abcdef/title/",
                        ) shouldBe false
                        isShortLink("https://www.reddit.com/r/fossdroid") shouldBe false
                    }
                }

            "matches" should
                {
                    "match reddit.com and its subdomains" {
                        matches("https://reddit.com/r/fossdroid/s/Wptky3Ma2I") shouldBe true
                        matches("https://old.reddit.com/r/fossdroid/s/Wptky3Ma2I") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("https://reddit.com.evil.com/r/fossdroid/s/Wptky3Ma2I") shouldBe
                            false
                        matches("https://notreddit.com/r/fossdroid/s/Wptky3Ma2I") shouldBe false
                    }
                }
        },
    )
