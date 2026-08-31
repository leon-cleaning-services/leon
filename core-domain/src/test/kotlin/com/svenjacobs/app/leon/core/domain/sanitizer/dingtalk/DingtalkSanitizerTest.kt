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
package com.svenjacobs.app.leon.core.domain.sanitizer.dingtalk

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.DingtalkSanitizer
import io.kotest.matchers.shouldBe

class DingtalkSanitizerTest :
    SanitizerSpec(
        DingtalkSanitizer,
        {
            "clean" should
                {
                    "remove tracking parameters from DingTalk URL" {
                        val result =
                            clean(
                                "https://www.dingtalk.com/page?from=share&scene=2&channel=app&source=qr&refer=user"
                            )
                        result shouldBe "https://www.dingtalk.com/page"
                    }
                }

            "matches" should
                {
                    "match dingtalk.com and dingtalk.cn domains with subdomains" {
                        matches("dingtalk.com") shouldBe true
                        matches("www.dingtalk.com") shouldBe true
                        matches("dingtalk.cn") shouldBe true
                        matches("sub.dingtalk.cn") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("dingtalk.com.evil.com") shouldBe false
                        matches("notdingtalk.com") shouldBe false
                    }
                }
        },
    )
