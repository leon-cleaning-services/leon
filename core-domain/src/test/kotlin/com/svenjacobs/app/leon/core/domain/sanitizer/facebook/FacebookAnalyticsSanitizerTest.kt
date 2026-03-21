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
package com.svenjacobs.app.leon.core.domain.sanitizer.facebook

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class FacebookAnalyticsSanitizerTest :
    WordSpec({
        "invoke" should
            {
                "remove \"fb_*\", \"fbclid\", \"sfnsn\" and \"cHash\" parameters" {
                    val sanitizer = FacebookAnalyticsSanitizer()

                    val result =
                        sanitizer("https://www.example.com?fb_abc=123&fbclid=12345&sfnsn=scwspmo")

                    result shouldBe "https://www.example.com"
                }

                "remove \"cHash\" parameter" {
                    val sanitizer = FacebookAnalyticsSanitizer()

                    val result =
                        sanitizer(
                            "https://www.spiegel.de/de?fbclid=IwY2xjawOxH_hleHRuA2FlbQIxMQ&cHash=137531e2404b087d877282a"
                        )

                    result shouldBe "https://www.spiegel.de/de"
                }
            }
    })
