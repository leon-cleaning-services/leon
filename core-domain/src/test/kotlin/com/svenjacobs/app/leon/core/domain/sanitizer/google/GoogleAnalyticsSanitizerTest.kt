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
package com.svenjacobs.app.leon.core.domain.sanitizer.google

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleAnalytics
import io.kotest.matchers.shouldBe

class GoogleAnalyticsSanitizerTest :
    SanitizerSpec(
        GoogleAnalytics,
        {
            "clean" should
                {
                    "remove \"ga_*\", \"utm_*\", and \"gclid\" parameters" {
                        val result =
                            clean("https://www.example.com?ga_abc=123&utm_def=456&gclid=789")

                        result shouldBe "https://www.example.com"
                    }

                    "remove \"gad_*\" parameters" {
                        val result = clean("https://www.example.com?gad_source=1&keep=123")

                        result shouldBe "https://www.example.com?keep=123"
                    }
                }
        },
    )
