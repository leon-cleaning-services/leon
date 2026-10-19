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
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleMapsShortLink
import io.kotest.matchers.shouldBe

class GoogleMapsShortLinkTest :
    SanitizerSpec(
        GoogleMapsShortLink,
        {
            "clean" should
                {
                    "replace a resolved short link with its target" {
                        clean(
                            "https://maps.app.goo.gl/AbCdEf123",
                            resolved =
                                mapOf(
                                    "https://maps.app.goo.gl/AbCdEf123" to
                                        "https://www.google.com/maps/place/Somewhere",
                                ),
                        ) shouldBe "https://www.google.com/maps/place/Somewhere"
                    }

                    "keep a short link which was not resolved" {
                        clean("https://maps.app.goo.gl/AbCdEf123?g_st=ic") shouldBe
                            "https://maps.app.goo.gl/AbCdEf123?g_st=ic"
                    }
                }

            "isShortLink" should
                {
                    "recognize a short link" {
                        isShortLink("https://maps.app.goo.gl/AbCdEf123") shouldBe true
                        isShortLink("https://maps.app.goo.gl/AbC-dEf_123") shouldBe true
                    }

                    "not consider the bare host a short link" {
                        isShortLink("https://maps.app.goo.gl/") shouldBe false
                    }
                }

            "matches" should
                {
                    "match maps.app.goo.gl" {
                        matches("https://maps.app.goo.gl/AbCdEf123") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("https://maps.app.goo.gl.evil.com/AbCdEf123") shouldBe false
                    }

                    "not match other goo.gl hosts" {
                        matches("https://goo.gl/AbCdEf123") shouldBe false
                    }
                }
        },
    )
