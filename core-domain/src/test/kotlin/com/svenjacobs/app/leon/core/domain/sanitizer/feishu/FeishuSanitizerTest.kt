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
package com.svenjacobs.app.leon.core.domain.sanitizer.feishu

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class FeishuSanitizerTest :
    WordSpec({
        val sanitizer = FeishuSanitizer()

        "invoke" should
            {
                "remove tracking parameters from Feishu URL" {
                    val result =
                        sanitizer(
                            "https://www.feishu.cn/docx/abc?from=share&scene=2&channel=app&source=qr&refer=user&keep=yes"
                        )
                    result shouldBe "https://www.feishu.cn/docx/abc?keep=yes"
                }
            }

        "matchesDomain" should
            {
                "match feishu.cn and feishu.net domains" {
                    sanitizer.matchesDomain("feishu.cn/docx") shouldBe true
                    sanitizer.matchesDomain("www.feishu.cn") shouldBe true
                    sanitizer.matchesDomain("wiki.feishu.cn") shouldBe true
                    sanitizer.matchesDomain("feishu.net") shouldBe true
                }
            }
    })