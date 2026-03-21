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
package com.svenjacobs.app.leon.core.domain.sanitizer.pearl

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class PearlSanitizerTest :
    WordSpec({
        val sanitizer = PearlSanitizer()

        "invoke" should
            {
                "remove all parameters" {
                    val result =
                        sanitizer(
                            "https://www.pearl.de/m/10884/?vid=985&curr=DEM&wa_id=995&wa_num=10884&" +
                                "mt=bWsqHZ2EOdIMxzpUi7oRsjVMLsC2%2Fyc65JwkwDStD1WiEu3REqi2%2Fw%3D%3D&utm_" +
                                "source=10884&utm_medium=onlineversion_D"
                        )

                    result shouldBe "https://www.pearl.de/m/10884/"
                }
            }

        "matchesDomain" should
            {
                "match for pearl.de" {
                    sanitizer.matchesDomain("https://www.pearl.de") shouldBe true
                }
            }
    })
