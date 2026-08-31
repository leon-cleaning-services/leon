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
package com.svenjacobs.app.leon.core.domain.sanitizer.jd

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Jd
import io.kotest.matchers.shouldBe

class JdSanitizerTest :
    SanitizerSpec(
        Jd,
        {
            "clean" should
                {
                    "remove tracking parameters and handle &amp; entity" {
                        val result = clean("https://item.jd.com/123.html?share=1&amp;jkl=2")
                        result shouldBe "https://item.jd.com/123.html"
                    }
                }
            "matches" should
                {
                    "match jd.com and 3.cn domains" {
                        matches("jd.com") shouldBe true
                        matches("3.cn") shouldBe true
                    }
                }
        },
    )
