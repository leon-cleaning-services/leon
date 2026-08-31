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

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Bilibili
import io.kotest.matchers.shouldBe

class BilibiliTest :
    SanitizerSpec(
        Bilibili,
        {
            "clean" should
                {
                    "remove tracking parameters from Bilibili URL" {
                        val result =
                            clean(
                                "https://www.bilibili.com/video/BV1xx?vd_source=abc&seid=456&from=spm&share_source=copy&copy_link=789"
                            )
                        result shouldBe "https://www.bilibili.com/video/BV1xx"
                    }
                }

            "matches" should
                {
                    "match bilibili.com domain" {
                        matches("bilibili.com/video/123") shouldBe true
                        matches("www.bilibili.com") shouldBe true
                    }
                }
        },
    )
