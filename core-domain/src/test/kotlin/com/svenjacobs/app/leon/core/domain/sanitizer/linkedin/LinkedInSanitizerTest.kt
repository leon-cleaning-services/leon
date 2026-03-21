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
package com.svenjacobs.app.leon.core.domain.sanitizer.linkedin

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class LinkedInSanitizerTest :
    WordSpec({
        val sanitizer = LinkedInSanitizer()

        "invoke" should
            {
                "remove rcm parameter" {
                    sanitizer(
                        "https://www.linkedin.com/feed/update/urn:li:activity:7358242171365335040" +
                            "?utm_source=share&utm_medium=member_desktop&rcm=ACoAAAUR3RBZwN2Ag"
                    ) shouldBe
                        "https://www.linkedin.com/feed/update/urn:li:activity:7358242171365335040" +
                            "?utm_source=share&utm_medium=member_desktop"
                }

                "remove rcm parameter when it is the only parameter" {
                    sanitizer(
                        "https://www.linkedin.com/posts/example-7394057060608462849-3jHb" +
                            "?rcm=ACoAAAUR3RBZwN2Ag"
                    ) shouldBe "https://www.linkedin.com/posts/example-7394057060608462849-3jHb"
                }
            }

        "matchesDomain" should
            {
                "match linkedin.com" {
                    sanitizer.matchesDomain("https://www.linkedin.com") shouldBe true
                }

                "not match other.com" {
                    sanitizer.matchesDomain("https://other.com") shouldBe false
                }
            }
    })
