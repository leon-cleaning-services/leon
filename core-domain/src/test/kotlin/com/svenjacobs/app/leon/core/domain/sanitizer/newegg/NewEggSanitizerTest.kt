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

package com.svenjacobs.app.leon.core.domain.sanitizer.newegg

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class NewEggSanitizerTest :
    WordSpec(
        {

            "invoke" should {

                "remove obsolete parameters" {
                    val sanitizer = NewEggSanitizer()
                    val result = sanitizer(
                        "https://www.newegg.com/black-acer-nitro-5-an515-57-59f7-gaming/p/N82E1" +
                            "6834360174?Item=N82E16834360174",
                    )

                    result shouldBe "https://www.newegg.com/p/N82E16834360174"
                }
            }
        },
    )
