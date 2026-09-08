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
package com.svenjacobs.app.leon.core.domain.sanitizer.latinatoday

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.LatinaToday
import io.kotest.matchers.shouldBe

class LatinaTodayTest :
    SanitizerSpec(
        LatinaToday,
        {
            "clean" should
                {
                    "remove all parameters" {
                        clean(
                            "https://www.latinatoday.it/cronaca/articolo.html?utm_source=facebook&utm_medium=social"
                        ) shouldBe "https://www.latinatoday.it/cronaca/articolo.html"
                    }
                }

            "matches" should
                {
                    "match latinatoday.it" { matches("https://latinatoday.it") shouldBe true }

                    "match www.latinatoday.it" {
                        matches("https://www.latinatoday.it") shouldBe true
                    }

                    "not match other.com" { matches("https://other.com") shouldBe false }
                }
        },
    )
