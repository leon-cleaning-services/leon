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

package com.svenjacobs.app.leon.core.domain.sanitizer.cx

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class CxAnalyticsSanitizerTest :
    WordSpec(
        {
            "invoke" should {

                "remove \"cx_*\", \"cxrecs_s\", and \"mibextid\" parameters" {
                    val sanitizer = CxAnalyticsSanitizer()

                    val result = sanitizer(
                        "https://www.redacted.com/redacted/2023/06/01/page-of-article.html?" +
                            "cx_testId=40&cx_testVariant=cx_8&cx_artPos=4&mibextid=Zxz2cZ&cxrecs_s=5",
                    )

                    result shouldBe
                        "https://www.redacted.com/redacted/2023/06/01/page-of-article.html"
                }
            }
        },
    )
