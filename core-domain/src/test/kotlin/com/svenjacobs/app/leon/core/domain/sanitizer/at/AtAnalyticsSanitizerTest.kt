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
package com.svenjacobs.app.leon.core.domain.sanitizer.at

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AtAnalyticsSanitizer
import io.kotest.matchers.shouldBe

class AtAnalyticsSanitizerTest :
    SanitizerSpec(
        AtAnalyticsSanitizer,
        {
            "clean" should
                {
                    "remove at_* parameters" {
                        clean(
                            "https://www.tagesschau.de/ausland/europa/toto-cutugno-tot-100.html" +
                                "?at_medium=mastodon&at_campaign=tagesschau.de"
                        ) shouldBe
                            "https://www.tagesschau.de/ausland/europa/toto-cutugno-tot-100.html"
                    }
                }
        },
    )
