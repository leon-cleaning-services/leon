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

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.PddSanitizer
import io.kotest.matchers.shouldBe

class PddSanitizerTest :
    SanitizerSpec(
        PddSanitizer,
        {
            "clean" should
                {
                    "remove tracking parameters from Pinduoduo URL" {
                        val result =
                            clean(
                                "https://mobile.yangkeduo.com/goods.html?pid=123&share_uin=456&track_id=789&goods_sign=abc"
                            )
                        result shouldBe "https://mobile.yangkeduo.com/goods.html"
                    }
                }
            "matches" should
                {
                    "match pinduoduo.com, pdd.com and yangkeduo.com domains" {
                        matches("pinduoduo.com") shouldBe true
                        matches("pdd.com") shouldBe true
                        matches("yangkeduo.com") shouldBe true
                    }
                }
        },
    )
