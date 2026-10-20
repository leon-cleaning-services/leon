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
package com.svenjacobs.app.leon.core.domain.sanitizer.lazada

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.LazadaShortLink
import io.kotest.matchers.shouldBe

class LazadaShortLinkTest :
    SanitizerSpec(
        LazadaShortLink,
        {
            "clean" should
                {
                    "replace a resolved short link with its target" {
                        clean(
                            "https://s.lazada.com.my/s.fp03W",
                            resolved =
                                mapOf(
                                    "https://s.lazada.com.my/s.fp03W" to
                                        "https://www.lazada.com.my/products/i123-s456.html",
                                ),
                        ) shouldBe "https://www.lazada.com.my/products/i123-s456.html"
                    }

                    "keep a short link which was not resolved" {
                        clean("https://s.lazada.com.my/s.fp03W") shouldBe
                            "https://s.lazada.com.my/s.fp03W"
                    }
                }

            "isShortLink" should
                {
                    "recognize a short link" {
                        isShortLink("https://s.lazada.com.my/s.fp03W") shouldBe true
                    }

                    "not consider the bare host a short link" {
                        isShortLink("https://s.lazada.com.my/") shouldBe false
                    }
                }

            "matches" should
                {
                    "match the short link host of every country" {
                        matches("https://s.lazada.com.my/s.fp03W") shouldBe true
                        matches("https://s.lazada.sg/s.fp03W") shouldBe true
                        matches("https://s.lazada.co.th/s.fp03W") shouldBe true
                        matches("https://s.lazada.com.ph/s.fp03W") shouldBe true
                        matches("https://s.lazada.co.id/s.fp03W") shouldBe true
                        matches("https://s.lazada.vn/s.fp03W") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("https://s.lazada.com.my.evil.com/s.fp03W") shouldBe false
                        matches("https://s.lazada.com.evil.com/s.fp03W") shouldBe false
                    }

                    "not match the shop itself" {
                        matches("https://www.lazada.com.my/products/i123-s456.html") shouldBe false
                    }
                }
        },
    )
