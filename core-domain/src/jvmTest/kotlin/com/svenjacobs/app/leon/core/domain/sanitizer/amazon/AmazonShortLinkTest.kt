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
package com.svenjacobs.app.leon.core.domain.sanitizer.amazon

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AmazonShortLink
import io.kotest.matchers.shouldBe

class AmazonShortLinkTest :
    SanitizerSpec(
        AmazonShortLink,
        {
            "clean" should
                {
                    "replace a resolved a.co short link with its target" {
                        clean(
                            "https://a.co/d/4X8EKfk",
                            resolved =
                                mapOf(
                                    "https://a.co/d/4X8EKfk" to
                                        "https://www.amazon.com/dp/B09FDSW9CS?ref_=cm_sw_r_apan",
                                ),
                        ) shouldBe "https://www.amazon.com/dp/B09FDSW9CS?ref_=cm_sw_r_apan"
                    }

                    "replace a resolved amzn.to short link with its target" {
                        clean(
                            "https://amzn.to/3xYzAbC",
                            resolved =
                                mapOf(
                                    "https://amzn.to/3xYzAbC" to
                                        "https://www.amazon.com/dp/B09FDSW9CS",
                                ),
                        ) shouldBe "https://www.amazon.com/dp/B09FDSW9CS"
                    }

                    "keep a short link which was not resolved" {
                        clean("https://a.co/d/4X8EKfk") shouldBe "https://a.co/d/4X8EKfk"
                    }
                }

            "isShortLink" should
                {
                    "recognize the short link hosts" {
                        isShortLink("https://a.co/d/4X8EKfk") shouldBe true
                        isShortLink("https://amzn.to/3xYzAbC") shouldBe true
                        isShortLink("https://amzn.to/3xY-zA_bC") shouldBe true
                        isShortLink("https://amzn.eu/d/5aBcDeF") shouldBe true
                        isShortLink("https://amzn.eu/5aBcDeF") shouldBe true
                    }

                    "not consider other paths short links" {
                        isShortLink("https://a.co/4X8EKfk") shouldBe false
                        isShortLink("https://a.co/d/") shouldBe false
                        isShortLink("https://amzn.to/") shouldBe false
                        isShortLink("https://amzn.to/a/b/c") shouldBe false
                    }
                }

            "matches" should
                {
                    "match the short link hosts" {
                        matches("https://a.co/d/4X8EKfk") shouldBe true
                        matches("https://amzn.to/3xYzAbC") shouldBe true
                        matches("https://amzn.eu/d/5aBcDeF") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("https://a.co.evil.com/d/4X8EKfk") shouldBe false
                        matches("https://amzn.to.evil.com/3xYzAbC") shouldBe false
                    }

                    "not match amazon.com" {
                        matches("https://www.amazon.com/dp/B09FDSW9CS") shouldBe false
                    }
                }
        },
    )
