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
package com.svenjacobs.app.leon.core.domain.sanitizer.pinduoduo

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class PddSanitizerTest :
    WordSpec({
        val sanitizer = PddSanitizer()

        "invoke" should
            {
                "remove tracking parameters from Pinduoduo URL" {
                    val result =
                        sanitizer(
                            "https://mobile.yangkeduo.com/goods.html?pid=123&share_uin=456&track_id=789&goods_sign=abc"
                        )
                    result shouldBe "https://mobile.yangkeduo.com/goods.html"
                }
            }
        "matchesDomain" should
            {
                "match pinduoduo.com, pdd.com and yangkeduo.com domains" {
                    sanitizer.matchesDomain("pinduoduo.com") shouldBe true
                    sanitizer.matchesDomain("pdd.com") shouldBe true
                    sanitizer.matchesDomain("yangkeduo.com") shouldBe true
                }
            }
    })
