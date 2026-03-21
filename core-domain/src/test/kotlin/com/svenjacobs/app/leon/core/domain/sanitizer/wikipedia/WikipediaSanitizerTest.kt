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

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WikipediaSanitizerTest :
    WordSpec({
        val sanitizer = WikipediaSanitizer()

        "invoke" should
            {
                "clean en.wikipedia.org URLs" {
                    sanitizer("https://en.wikipedia.org/wiki/Kerosene?wprov=sfla1") shouldBe
                        "https://en.wikipedia.org/wiki/Kerosene"
                }
            }

        "matchesDomain" should
            {
                "match wikipedia.org" {
                    sanitizer.matchesDomain("https://wikipedia.org") shouldBe true
                }

                "match en.wikipedia.org" {
                    sanitizer.matchesDomain("https://en.wikipedia.org") shouldBe true
                }

                "match m.en.wikipedia.org" {
                    sanitizer.matchesDomain("https://de.m.wikipedia.org") shouldBe true
                }

                "don't match google.com" {
                    sanitizer.matchesDomain("https://google.com") shouldBe false
                }
            }
    })
