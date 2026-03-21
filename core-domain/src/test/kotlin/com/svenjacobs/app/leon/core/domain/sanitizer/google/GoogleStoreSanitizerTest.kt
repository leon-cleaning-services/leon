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
package com.svenjacobs.app.leon.core.domain.sanitizer.google

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class GoogleStoreSanitizerTest :
    WordSpec({
        val sanitizer = GoogleStoreSanitizer()

        "invoke" should
            {
                "remove \"hl\" and \"selections\" parameters" {
                    val result =
                        sanitizer(
                            "https://store.google.com/gb/product/chromecast_google_tv?hl=en-GB" +
                                "&selections=eyJwcm9kdWN0RmFtaWx5IjoiWTJoeWIyMWxZMkZ6ZEY5bmIyOW5iR1" +
                                "ZmZEhZPSIsImhlcm9Qcm9kdWN0cyI6W1siY0hKa1h6YzRNekpmTXprMU1nPT0iLDEs" +
                                "bnVsbF1dfQ%3D%3D"
                        )

                    result shouldBe "https://store.google.com/gb/product/chromecast_google_tv"
                }
            }
    })
