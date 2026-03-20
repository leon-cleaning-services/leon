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

package com.svenjacobs.app.leon.core.domain.sanitizer.netflix

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class NetflixSanitizerTest :
    WordSpec(
        {
            val sanitizer = NetflixSanitizer()

            "invoke" should {

                "remove various Netflix parameters" {
                    val result = sanitizer(
                        "https://www.netflix.com/de/title/81040344?s=a&trkid=13747225&t=more&vl" +
                            "ang=de&clip=81499054&trg=trg",
                    )

                    result shouldBe "https://www.netflix.com/de/title/81040344"
                }

                "remove parameters from help.netflix.com URL" {
                    val result = sanitizer(
                        "https://help.netflix.com/en/titlerequest?netflixsource=android&fromApp=true",
                    )

                    result shouldBe "https://help.netflix.com/en/titlerequest"
                }
            }

            "matchesDomain" should {

                "match netflix.com" {
                    sanitizer.matchesDomain("https://netflix.com") shouldBe true
                }

                "match help.netflix.com" {
                    sanitizer.matchesDomain("https://help.netflix.com") shouldBe true
                }
            }
        },
    )
