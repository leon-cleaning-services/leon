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
package com.svenjacobs.app.leon.core.domain.sanitizer.latinatoday

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class LatinaTodaySanitizerTest :
    WordSpec({
        val sanitizer = LatinaTodaySanitizer()

        "invoke" should {
            "remove all parameters" {
                sanitizer(
                    "https://www.latinatoday.it/cronaca/articolo.html?utm_source=facebook&utm_medium=social"
                ) shouldBe "https://www.latinatoday.it/cronaca/articolo.html"
            }
        }

        "matchesDomain" should {
            "match latinatoday.it" {
                sanitizer.matchesDomain("https://latinatoday.it") shouldBe true
            }

            "match www.latinatoday.it" {
                sanitizer.matchesDomain("https://www.latinatoday.it") shouldBe true
            }

            "not match other.com" {
                sanitizer.matchesDomain("https://other.com") shouldBe false
            }
        }
    })
