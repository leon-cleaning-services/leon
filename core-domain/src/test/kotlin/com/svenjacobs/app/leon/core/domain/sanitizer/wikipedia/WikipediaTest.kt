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
package com.svenjacobs.app.leon.core.domain.sanitizer.wikipedia

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Wikipedia
import io.kotest.matchers.shouldBe

class WikipediaTest :
    SanitizerSpec(
        Wikipedia,
        {
            "clean" should
                {
                    "clean en.wikipedia.org URLs" {
                        clean("https://en.wikipedia.org/wiki/Kerosene?wprov=sfla1") shouldBe
                            "https://en.wikipedia.org/wiki/Kerosene"
                    }
                }

            "matches" should
                {
                    "match wikipedia.org" { matches("https://wikipedia.org") shouldBe true }

                    "match en.wikipedia.org" { matches("https://en.wikipedia.org") shouldBe true }

                    "match m.en.wikipedia.org" {
                        matches("https://de.m.wikipedia.org") shouldBe true
                    }

                    "don't match google.com" { matches("https://google.com") shouldBe false }

                    "don't match wikipedia.org inside another URL" {
                        matches("https://evil.com/?u=en.wikipedia.org") shouldBe false
                    }

                    "don't match host which only starts with wikipedia.org" {
                        matches("https://wikipedia.org.evil.com") shouldBe false
                    }

                    "don't match host where the dot is another character" {
                        matches("https://wikipedia-org") shouldBe false
                    }
                }
        },
    )
