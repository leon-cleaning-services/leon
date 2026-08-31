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
package com.svenjacobs.app.leon.core.domain.sanitizer.kuaishou

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Kuaishou
import io.kotest.matchers.shouldBe

class KuaishouTest :
    SanitizerSpec(
        Kuaishou,
        {
            "clean" should
                {
                    "remove tracking parameters from Kuaishou URL" {
                        val result =
                            clean(
                                "https://www.kuaishou.com/short-video/abc?share=1&userId=123&photoId=456"
                            )
                        result shouldBe "https://www.kuaishou.com/short-video/abc"
                    }
                }
            "matches" should
                {
                    "match kuaishou.com and v.kuaishou.com domains" {
                        matches("kuaishou.com") shouldBe true
                        matches("v.kuaishou.com") shouldBe true
                    }
                }
        },
    )
