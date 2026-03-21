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
package com.svenjacobs.app.leon.core.domain.sanitizer.shopee

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ShopeeSanitizerTest :
    WordSpec({
        val sanitizer = ShopeeSanitizer()

        "invoke" should
            {
                "remove all parameters" {
                    val result =
                        sanitizer(
                            "https://shopee.com.my/product/300862466/12251369135?smtt=O.123661111-1" +
                                "672730601.9"
                        )

                    result shouldBe "https://shopee.com.my/product/300862466/12251369135"
                }
            }

        "matchesDomain" should
            {
                "match for shopee.com.my" {
                    sanitizer.matchesDomain("https://shopee.com.my") shouldBe true
                }
            }
    })
