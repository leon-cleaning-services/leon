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
package com.svenjacobs.app.leon.core.domain.sanitizer.adobemarketo

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class AdobeMarketoEngageSanitizerTest :
    WordSpec({
        val sanitizer = AdobeMarketoEngageSanitizer()

        "invoke" should
            {
                "remove mkt_* parameters" {
                    sanitizer("https://www.example.com/page?mkt_tok=abc123&keep=123") shouldBe
                        "https://www.example.com/page&keep=123"
                }

                "remove multiple mkt_* parameters" {
                    sanitizer(
                        "https://www.example.com/page?mkt_tok=abc&mkt_unsubscribe=1&keep=123"
                    ) shouldBe "https://www.example.com/page&keep=123"
                }
            }
    })
