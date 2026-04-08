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
package com.svenjacobs.app.leon.core.domain.sanitizer.xiaohongshu

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class XiaohongshuSanitizerTest :
    WordSpec({
        val sanitizer = XiaohongshuSanitizer()

        "invoke" should
            {
                "remove all parameters from Xiaohongshu URL" {
                    val result =
                        sanitizer(
                            "https://www.xiaohongshu.com/discovery/item?id=123&from=share&utm_source=google&extra=keep"
                        )
                    result shouldBe "https://www.xiaohongshu.com/discovery/item"
                }
            }

        "matchesDomain" should
            {
                "match xiaohongshu.com and xhslink.com domains" {
                    sanitizer.matchesDomain("xiaohongshu.com/discovery") shouldBe true
                    sanitizer.matchesDomain("www.xiaohongshu.com") shouldBe true
                    sanitizer.matchesDomain("xhslink.com/abc") shouldBe true
                }
            }
    })
