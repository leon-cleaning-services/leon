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
package com.svenjacobs.app.leon.core.domain.sanitizer.google

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleShortLink
import io.kotest.matchers.shouldBe

class GoogleShortLinkTest :
    SanitizerSpec(
        GoogleShortLink,
        {
            "clean" should
                {
                    "replace a resolved short link with its target" {
                        clean(
                            "https://share.google/LjiVlhSkGoc8UoeIQ",
                            resolved =
                                mapOf(
                                    "https://share.google/LjiVlhSkGoc8UoeIQ" to
                                        "https://www.sciencedaily.com/releases/2026/11/" +
                                            "261102075806.htm",
                                ),
                        ) shouldBe "https://www.sciencedaily.com/releases/2026/11/261102075806.htm"
                    }

                    "keep a short link which was not resolved" {
                        clean("https://share.google/LjiVlhSkGoc8UoeIQ") shouldBe
                            "https://share.google/LjiVlhSkGoc8UoeIQ"
                    }
                }

            "isShortLink" should
                {
                    "recognize a short link" {
                        isShortLink("https://share.google/LjiVlhSkGoc8UoeIQ") shouldBe true
                    }

                    "recognize the short link it redirects to" {
                        isShortLink(
                            "https://www.google.com/share.google?q=LjiVlhSkGoc8UoeIQ",
                        ) shouldBe true
                    }

                    "not consider the bare host a short link" {
                        isShortLink("https://share.google/") shouldBe false
                    }

                    "not consider google.com/share.google without a code a short link" {
                        isShortLink("https://www.google.com/share.google") shouldBe false
                        isShortLink("https://www.google.com/share.google?q=") shouldBe false
                        isShortLink("https://www.google.com/share.google?x=abc") shouldBe false
                    }

                    "not consider other Google pages short links" {
                        isShortLink("https://www.google.com/search?q=leon") shouldBe false
                    }
                }

            "shortLinkRequest" should
                {
                    "drop the query of share.google" {
                        shortLinkRequest(
                            "https://share.google/LjiVlhSkGoc8UoeIQ?utm_source=x",
                        ) shouldBe "https://share.google/LjiVlhSkGoc8UoeIQ"
                    }

                    "keep only the code of google.com/share.google" {
                        shortLinkRequest(
                            "https://www.google.com/share.google?utm_source=x&q=LjiVlhSkGoc8UoeIQ#f",
                        ) shouldBe "https://www.google.com/share.google?q=LjiVlhSkGoc8UoeIQ"
                    }
                }

            "matches" should
                {
                    "match share.google" {
                        matches("https://share.google/LjiVlhSkGoc8UoeIQ") shouldBe true
                    }

                    "match google.com/share.google" {
                        matches("https://www.google.com/share.google?q=LjiVlhSkGoc8UoeIQ") shouldBe
                            true
                    }

                    "not match a lookalike host" {
                        matches("https://share.google.evil.com/LjiVlhSkGoc8UoeIQ") shouldBe false
                    }

                    "not match other paths of google.com" {
                        matches("https://www.google.com/LjiVlhSkGoc8UoeIQ") shouldBe false
                    }
                }
        },
    )
