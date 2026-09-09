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
package com.svenjacobs.app.leon.core.domain.sanitizer.humblebundle

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.HumbleBundle
import io.kotest.matchers.shouldBe

class HumbleBundleTest :
    SanitizerSpec(
        HumbleBundle,
        {
            "clean" should
                {
                    "remove all parameters" {
                        clean(
                            "https://www.humblebundle.com/books/no-kings-library-berrettkoehler-books?mcI" +
                                "D=102:69f1456f540ada90b00cd16c:ot:668703f5cbbc2d1867d8cdcc:1&linkID=69f14" +
                                "7a276fa2560470c48b4&utm_source=Humble+Bundle+Newsletter&utm_content=cta_b" +
                                "utton&utm_medium=email&utm_campaign=nokingslibraryberrettkoehler_bookbundle"
                        ) shouldBe
                            "https://www.humblebundle.com/books/no-kings-library-berrettkoehler-books"
                    }
                }

            "matches" should
                {
                    "match humblebundle.com" { matches("https://humblebundle.com") shouldBe true }

                    "match www.humblebundle.com" {
                        matches("https://www.humblebundle.com") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("https://humblebundle.com.evil.com") shouldBe false
                    }
                }
        },
    )
