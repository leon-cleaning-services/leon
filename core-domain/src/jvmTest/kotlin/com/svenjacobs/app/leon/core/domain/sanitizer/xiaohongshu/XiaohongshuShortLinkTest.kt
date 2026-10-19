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
package com.svenjacobs.app.leon.core.domain.sanitizer.xiaohongshu

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.XiaohongshuShortLink
import io.kotest.matchers.shouldBe

class XiaohongshuShortLinkTest :
    SanitizerSpec(
        XiaohongshuShortLink,
        {
            "clean" should
                {
                    "replace a resolved short link with its target" {
                        clean(
                            "http://xhslink.cn/o/AbC123",
                            resolved =
                                mapOf(
                                    "http://xhslink.cn/o/AbC123" to
                                        "https://www.xiaohongshu.com/discovery/item/64f0000000000000",
                                ),
                        ) shouldBe "https://www.xiaohongshu.com/discovery/item/64f0000000000000"
                    }

                    "keep a short link which was not resolved" {
                        clean("http://xhslink.com/a/AbC123") shouldBe "http://xhslink.com/a/AbC123"
                    }
                }

            "isShortLink" should
                {
                    "recognize a short link" {
                        isShortLink("http://xhslink.cn/o/AbC123") shouldBe true
                        isShortLink("http://xhslink.com/a/AbC123") shouldBe true
                        isShortLink("http://xhslink.com/AbC123") shouldBe true
                        isShortLink("http://xhslink.com/a/AbC-12_3") shouldBe true
                    }

                    "not consider the bare host a short link" {
                        isShortLink("http://xhslink.com/") shouldBe false
                        isShortLink("http://xhslink.cn/o/") shouldBe false
                    }
                }

            "matches" should
                {
                    "match xhslink.com and xhslink.cn" {
                        matches("http://xhslink.com/a/AbC123") shouldBe true
                        matches("http://xhslink.cn/o/AbC123") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("http://xhslink.com.evil.com/a/AbC123") shouldBe false
                        matches("http://xhslink.cn.evil.com/o/AbC123") shouldBe false
                    }
                }
        },
    )
