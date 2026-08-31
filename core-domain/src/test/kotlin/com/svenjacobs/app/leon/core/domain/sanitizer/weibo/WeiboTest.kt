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
package com.svenjacobs.app.leon.core.domain.sanitizer.weibo

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Weibo
import io.kotest.matchers.shouldBe

class WeiboTest :
    SanitizerSpec(
        Weibo,
        {
            "clean" should
                {
                    "remove tracking parameters from Weibo URL" {
                        val result =
                            clean(
                                "https://weibo.com/123/profile?from=share&refer=user&share_token=abc"
                            )
                        result shouldBe "https://weibo.com/123/profile"
                    }
                }

            "matches" should
                {
                    "match weibo.com and m.weibo.cn domains" {
                        matches("weibo.com") shouldBe true
                        matches("m.weibo.cn") shouldBe true
                    }
                }
        },
    )
