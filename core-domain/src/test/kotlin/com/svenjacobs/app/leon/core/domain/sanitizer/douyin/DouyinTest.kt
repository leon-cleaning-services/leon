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
package com.svenjacobs.app.leon.core.domain.sanitizer.douyin

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Douyin
import io.kotest.matchers.shouldBe

class DouyinTest :
    SanitizerSpec(
        Douyin,
        {
            "clean" should
                {
                    "remove the password suffix from a Douyin URL" {
                        val result = clean("https://v.douyin.com/abc123/Ipd:abcde")
                        result shouldBe "https://v.douyin.com/abc123"
                    }
                }

            "matches" should
                {
                    "match douyin.com, v.douyin.com and iesdouyin.com domains" {
                        matches("douyin.com/video") shouldBe true
                        matches("v.douyin.com/abc") shouldBe true
                        matches("iesdouyin.com/share") shouldBe true
                    }
                }
        },
    )
