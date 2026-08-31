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
package com.svenjacobs.app.leon.core.domain.sanitizer.threads

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.ThreadsSanitizer
import io.kotest.matchers.shouldBe

class ThreadsSanitizerTest :
    SanitizerSpec(
        ThreadsSanitizer,
        {
            "clean" should
                {
                    "remove all parameters from threads.net" {
                        clean(
                            "https://www.threads.net/t/CufR4M8yNdJ/?igshid=NTc4MTIwNjQ2YQ=="
                        ) shouldBe "https://www.threads.net/t/CufR4M8yNdJ/"
                    }

                    "remove all parameters from threads.com" {
                        clean(
                            "https://www.threads.com/@chpapa/post/DSzhvqtkuyg?xmt=AQF0J2-TPDkD-qhbXb7usPu3mcJy6Tz8R0LhCkenCCvSOg"
                        ) shouldBe "https://www.threads.com/@chpapa/post/DSzhvqtkuyg"
                    }
                }

            "matches" should
                {
                    "match threads.net" { matches("https://threads.net") shouldBe true }

                    "match threads.com" { matches("https://www.threads.com") shouldBe true }
                }
        },
    )
