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
package com.svenjacobs.app.leon.core.domain.sanitizer.google

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleShare
import io.kotest.matchers.shouldBe

class GoogleShareTest :
    SanitizerSpec(
        GoogleShare,
        {
            "clean" should
                {
                    "remove \"shem\" from the target of a shared link" {
                        clean(
                            "https://www.sciencedaily.com/releases/2026/11/261102075806.htm" +
                                "?shem=dsdf,sharefoc,agadiscoversdl,,sh/x/discover/m1/4",
                        ) shouldBe "https://www.sciencedaily.com/releases/2026/11/261102075806.htm"
                    }

                    "keep other parameters" {
                        clean("https://www.example.com/article?id=42&shem=dsdf&shema=1") shouldBe
                            "https://www.example.com/article?id=42&shema=1"
                    }
                }
        },
    )
