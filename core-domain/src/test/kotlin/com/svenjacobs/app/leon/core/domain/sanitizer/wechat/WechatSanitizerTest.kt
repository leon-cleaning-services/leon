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
package com.svenjacobs.app.leon.core.domain.sanitizer.wechat

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WechatSanitizerTest :
    WordSpec({
        val sanitizer = WechatSanitizer()

        "invoke" should
            {
                "remove tracking parameters from WeChat URL" {
                    val result =
                        sanitizer(
                            "https://weixin.qq.com/s?__biz=abc&mid=123&idx=1&sn=def&scene=2&wx_header=3&keep=yes"
                        )
                    result shouldBe "https://weixin.qq.com/s?keep=yes"
                }
            }

        "matchesDomain" should
            {
                "match weixin.qq.com and url.cn domains" {
                    sanitizer.matchesDomain("weixin.qq.com/s") shouldBe true
                    sanitizer.matchesDomain("url.cn/abc") shouldBe true
                }
            }
    })