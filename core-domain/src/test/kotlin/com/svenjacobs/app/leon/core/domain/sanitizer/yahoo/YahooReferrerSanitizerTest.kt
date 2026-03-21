/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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
package com.svenjacobs.app.leon.core.domain.sanitizer.yahoo

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class YahooReferrerSanitizerTest :
    WordSpec({
        val sanitizer = YahooReferrerSanitizer()

        "invoke" should
            {
                "remove guccounter parameter" {
                    sanitizer("https://www.example.com/article?id=123&guccounter=1") shouldBe
                        "https://www.example.com/article?id=123"
                }

                "remove guce_referrer parameter" {
                    sanitizer(
                        "https://www.example.com/article?id=123&guce_referrer=aHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8"
                    ) shouldBe "https://www.example.com/article?id=123"
                }

                "remove guce_referrer_sig parameter" {
                    sanitizer(
                        "https://www.example.com/article?id=123&guce_referrer_sig=AQAAABs"
                    ) shouldBe "https://www.example.com/article?id=123"
                }

                "remove all Yahoo referrer parameters" {
                    sanitizer(
                        "https://www.example.com/article?id=123&guccounter=1&guce_referrer=aHR0cHM6Ly93d3cuZ29vZ2xlLmNvbS8&guce_referrer_sig=AQAAABs"
                    ) shouldBe "https://www.example.com/article?id=123"
                }
            }
    })
