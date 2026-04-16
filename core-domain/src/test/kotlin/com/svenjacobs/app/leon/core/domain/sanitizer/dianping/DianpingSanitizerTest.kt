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
package com.svenjacobs.app.leon.core.domain.sanitizer.dianping

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DianpingSanitizerTest :
    WordSpec({
        val sanitizer = DianpingSanitizer()

        "invoke" should
            {
                "remove tracking parameters from Dianping URL" {
                    val result =
                        sanitizer(
                            "https://www.dianping.com/shop/789?from=share&source=wx&channel=search&refer=user&wm=456&c=def&wx_extra=1"
                        )
                    result shouldBe "https://www.dianping.com/shop/789"
                }
            }

        "matchesDomain" should
            {
                "match dianping.com domain" {
                    sanitizer.matchesDomain("dianping.com/shop") shouldBe true
                    sanitizer.matchesDomain("www.dianping.com") shouldBe true
                }
            }
    })
