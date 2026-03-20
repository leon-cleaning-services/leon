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

package com.svenjacobs.app.leon.core.domain.sanitizer.theguardian

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TheGuardianSanitizerTest :
    WordSpec(
        {
            val sanitizer = TheGuardianSanitizer()

            "invoke" should {

                "remove all parameters" {

                    val result = sanitizer(
                        "https://www.theguardian.com/world/2023/jan/15/nepal-plane-crash-with-7" +
                            "2-onboard-leaves-at-least-16-dead?CMP=Share_AndroidApp_Other",
                    )

                    result shouldBe
                        "https://www.theguardian.com/world/2023/jan/15/nepal-plane-crash-" +
                        "with-72-onboard-leaves-at-least-16-dead"
                }
            }

            "matchesDomain" should {

                "match for theguardian.com" {
                    sanitizer.matchesDomain("https://www.theguardian.com") shouldBe true
                }
            }
        },
    )
