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
package com.svenjacobs.app.leon.core.domain.sanitizer.zhihu

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ZhihuSanitizerTest :
    WordSpec({
        val sanitizer = ZhihuSanitizer()

        "invoke" should
            {
                "remove tracking parameters from Zhihu URL" {
                    val result =
                        sanitizer(
                            "https://www.zhihu.com/question/123?share_redirect=1&share_code=abc&keep=yes"
                        )
                    result shouldBe "https://www.zhihu.com/question/123?keep=yes"
                }
            }

        "matchesDomain" should
            {
                "match zhihu.com domain" {
                    sanitizer.matchesDomain("zhihu.com") shouldBe true
                    sanitizer.matchesDomain("www.zhihu.com") shouldBe true
                }
            }
    })
