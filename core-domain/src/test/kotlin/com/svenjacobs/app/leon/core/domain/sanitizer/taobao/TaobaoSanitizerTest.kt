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

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Taobao
import io.kotest.matchers.shouldBe

class TaobaoSanitizerTest :
    SanitizerSpec(
        Taobao,
        {
            "clean" should
                {
                    "remove tracking parameters and handle &amp; entity" {
                        val result =
                            clean(
                                "https://item.taobao.com/item.htm?id=123&amp;smid=abc&ut_ma=1&track_id=2&spm=3&share_crt_v=4&tbkt=5&isg=6&tk=7&keep=yes"
                            )
                        result shouldBe "https://item.taobao.com/item.htm?id=123&keep=yes"
                    }
                }

            "matches" should
                {
                    "match taobao.com, tmall.com, tb.cn domains" {
                        matches("taobao.com/item") shouldBe true
                        matches("tmall.com") shouldBe true
                        matches("tb.cn") shouldBe true
                        matches("e.tb.cn") shouldBe true
                        matches("m.tb.cn") shouldBe true
                    }
                }
        },
    )
