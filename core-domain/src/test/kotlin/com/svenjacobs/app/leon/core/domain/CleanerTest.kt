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
package com.svenjacobs.app.leon.core.domain

import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleSearch
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf

class CleanerTest :
    WordSpec({
        val repository = mockk<SanitizerRepository>()

        /** A sanitizer which removes exactly one parameter, from every URL. */
        fun fake(name: String, parameter: String) =
            Sanitizer(
                id = SanitizerId(name),
                name = name,
                rules = persistentListOf(Rule.RemoveParameters(parameter)),
            )

        val service =
            Cleaner(
                sanitizers =
                    persistentListOf(
                        fake("fake1", "paramA"),
                        fake("fake2", "paramB"),
                        fake("fake3", "paramC"),
                    ),
                repository = repository,
            )

        beforeEach {
            clearMocks(repository)

            coEvery { repository.isEnabled(SanitizerId(any())) } returns true
        }

        "clean" should
            {
                "remove tracking parameters" {
                    val result = service.clean("https://www.some.site/?paramA=A&paramB=B")

                    result.cleanedText shouldBe "https://www.some.site/"
                    result.urls shouldHaveSize 1
                    result.urls.first().cleaned.toString() shouldBe "https://www.some.site/"
                }

                "keep non-tracking parameters" {
                    val result =
                        service.clean("https://www.some.site/?paramA=A&paramB=B&page=1&q=query")

                    result.cleanedText shouldBe "https://www.some.site/?page=1&q=query"
                    result.urls shouldHaveSize 1
                    result.urls.first().cleaned.toString() shouldBe
                        "https://www.some.site/?page=1&q=query"
                }

                "keep anchor tags after cleaned parameters" {
                    val result = service.clean("https://www.some.site/?paramA=A&paramB=B#anchor")

                    result.cleanedText shouldBe "https://www.some.site/#anchor"
                    result.urls.first().cleaned.toString() shouldBe "https://www.some.site/#anchor"
                }

                "clean multiple URLs in text" {
                    val result =
                        service.clean(
                            "Hey, I would like to share this " +
                                "https://www.some.site/?paramA=A&paramB=B link as well as this " +
                                "https://www.some2.site?paramC=C link :)"
                        )

                    result.cleanedText shouldBe
                        "Hey, I would like to share this " +
                            "https://www.some.site/ link as well as this https://www.some2.site link :)"
                    result.urls shouldHaveSize 2
                    result.urls[0].cleaned.toString() shouldBe "https://www.some.site/"
                    result.urls[1].cleaned.toString() shouldBe "https://www.some2.site"
                }

                "URL decode when enabled" {
                    val result =
                        service.clean(
                            text = "https://www.some.site/Hello%2FWorld?paramA=A&paramB=B",
                            decodeUrl = true,
                        )

                    result.cleanedText shouldBe "https://www.some.site/Hello/World"
                }

                "repeat cleaning until iteration doesn't yield new value" {
                    val googleService =
                        Cleaner(
                            sanitizers = persistentListOf(GoogleSearch),
                            repository = repository,
                        )

                    googleService
                        .clean(
                            text =
                                "https://www.google.com/url?q=https://www.google.com/url?rct%3Dj%26sa%" +
                                    "3Dt%26url%3Dhttps://www.inferse.com/268929/prime-day-2022-phone-deals-al" +
                                    "l-the-best-amazon-prime-early-access-phone-deals-bgr/%26ct%3Dga%26cd%3DC" +
                                    "AEYACoUMTA3NzY4NTE0MDEwNTAyODc4MzMyHDMxNGQyNzdmOTRjOGU5MDE6Y29tOmVuOlVTO" +
                                    "lI%26usg%3DAOvVaw0ib2-8ukc8GkNkCxTAEkb6&source=gmail&ust=166582840354400" +
                                    "0&usg=AOvVaw0R6tQNh7vvWCHGibWcIz_k"
                        )
                        .cleanedText shouldBe
                        "https://www.inferse.com/268929/prime-day-2022-phone-deals" +
                            "-all-the-best-amazon-prime-early-access-phone-deals-bgr/"
                }

                "return result after max iterations" {
                    // Appends a path segment on every pass, so cleaning never settles by itself.
                    val endless =
                        Sanitizer(
                            id = SanitizerId("endless"),
                            name = "endless",
                            rules = persistentListOf(Rule.RewritePath("(.*)", "$1/x")),
                        )

                    Cleaner(sanitizers = persistentListOf(endless), repository = repository)
                        .clean("http://www.example.com")
                        .cleanedText shouldBe "http://www.example.com/x/x/x/x/x"
                }
            }
    })
