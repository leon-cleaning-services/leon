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
package com.svenjacobs.app.leon.core.domain.sanitizer.taobao

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TaobaoSanitizerTest :
    WordSpec({
        val sanitizer = TaobaoSanitizer()

        "invoke" should
            {
                "remove tracking parameters and handle &amp; entity" {
                    val result =
                        sanitizer(
                            "https://item.taobao.com/item.htm?id=123&amp;smid=abc&ut_ma=1&track_id=2&spm=3&share_crt_v=4&tbkt=5&isg=6&tk=7&keep=yes"
                        )
                    result shouldBe "https://item.taobao.com/item.htm?id=123&keep=yes"
                }
            }

        "matchesDomain" should
            {
                "match taobao.com, tmall.com, tb.cn domains" {
                    sanitizer.matchesDomain("taobao.com/item") shouldBe true
                    sanitizer.matchesDomain("tmall.com") shouldBe true
                    sanitizer.matchesDomain("tb.cn") shouldBe true
                    sanitizer.matchesDomain("e.tb.cn") shouldBe true
                    sanitizer.matchesDomain("m.tb.cn") shouldBe true
                }
            }
    })