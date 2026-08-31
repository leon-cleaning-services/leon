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
package com.svenjacobs.app.leon.core.domain.sanitizer.meituan

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Meituan
import io.kotest.matchers.shouldBe

class MeituanSanitizerTest :
    SanitizerSpec(
        Meituan,
        {
            "clean" should
                {
                    "remove tracking parameters from Meituan URL" {
                        val result =
                            clean(
                                "https://www.meituan.com/deal/123?from=share&source=app&wx_openid=abc"
                            )
                        result shouldBe "https://www.meituan.com/deal/123"
                    }
                }

            "matches" should
                {
                    "match meituan.com, meituan.cn and meituan.net domains" {
                        matches("meituan.com") shouldBe true
                        matches("www.meituan.com") shouldBe true
                        matches("meituan.cn") shouldBe true
                        matches("meituan.net") shouldBe true
                    }

                    "not match a subdomain" {
                        // Deliberately Domain, not Subdomains — matching the original sanitizer.
                        matches("sub.meituan.com") shouldBe false
                    }

                    "not match a lookalike host" { matches("meituan.com.evil.com") shouldBe false }
                }
        },
    )
