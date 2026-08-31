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
package com.svenjacobs.app.leon.core.domain.sanitizer.amazon

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Amazon
import io.kotest.matchers.shouldBe

class AmazonTest :
    SanitizerSpec(
        Amazon,
        {
            "clean" should
                {
                    "remove ref_ parameter" {
                        val result =
                            clean(
                                "https://www.amazon.de/gp/css/homepage.html?ref_=nav_AccountFlyout_ya"
                            )

                        result shouldBe "https://www.amazon.de/gp/css/homepage.html"
                    }
                }

            "matches" should
                {
                    "match Amazon domains" {
                        matches("https://www.amazon.de/dp/B091G3FLL7/") shouldBe true
                    }

                    "not match host which continues after the domain" {
                        matches("https://amazon.evil.com?u=/dp/B091G3FLL7/") shouldBe false
                    }
                }
        },
    )
