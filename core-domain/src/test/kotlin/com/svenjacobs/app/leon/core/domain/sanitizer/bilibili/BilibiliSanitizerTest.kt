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
package com.svenjacobs.app.leon.core.domain.sanitizer.bilibili

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class BilibiliSanitizerTest :
    WordSpec({
        val sanitizer = BilibiliSanitizer()

        "invoke" should
            {
                "remove tracking parameters from Bilibili URL" {
                    val result =
                        sanitizer(
                            "https://www.bilibili.com/video/BV1xx?vd_source=abc&seid=456&from=spm&share_source=copy&copy_link=789&keep=yes"
                        )
                    result shouldBe "https://www.bilibili.com/video/BV1xx?keep=yes"
                }
            }

        "matchesDomain" should
            {
                "match bilibili.com domain" {
                    sanitizer.matchesDomain("bilibili.com/video/123") shouldBe true
                    sanitizer.matchesDomain("www.bilibili.com") shouldBe true
                }
            }
    })
