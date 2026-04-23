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
package com.svenjacobs.app.leon.core.domain.sanitizer.douyin

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DouyinSanitizerTest :
    WordSpec({
        val sanitizer = DouyinSanitizer()

        "invoke" should
            {
                "extract URL and remove password suffix from Douyin share text" {
                    val result = sanitizer("分享视频&nbsp;https://v.douyin.com/abc123/Ipd:abcde")
                    result shouldBe "https://v.douyin.com/abc123"
                }
            }

        "matchesDomain" should
            {
                "match douyin.com, v.douyin.com and iesdouyin.com domains" {
                    sanitizer.matchesDomain("douyin.com/video") shouldBe true
                    sanitizer.matchesDomain("v.douyin.com/abc") shouldBe true
                    sanitizer.matchesDomain("iesdouyin.com/share") shouldBe true
                }
            }
    })
