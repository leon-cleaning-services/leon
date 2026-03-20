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

package com.svenjacobs.app.leon.core.domain.sanitizer.youtube

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class YoutubeRedirectSanitizerTest :
    WordSpec(
        {

            "invoke" should {

                "extract URL from YouTube redirect link" {
                    val sanitizer = YoutubeRedirectSanitizer()
                    val result = sanitizer(
                        "https://www.youtube.com/redirect?event=channel_description&redir_token" +
                            "=QUFFLUhqa1JoZzZUczlhMWJCaTBoc1lqa3ZtX2Rpd0ZPUXxBQ3Jtc0tsYVhpenF1czV5Vjl" +
                            "wZm5pemZGdm4zNHVXSldEUlR6dHNhZzI0UkFvLXo0cEVyUk0yaHR5LVhGWEFCLVdzdmlZWGU" +
                            "3eUY5ZWdUZTBUbEw3MVg4UDZCRzdkXzdaaGczT25Ka3Q5bjUzTmxWVHF3Tll6MA&q=http%3" +
                            "A%2F%2Fwww.google.com%2Fabout%2F&html_redirect=1",
                    )

                    result shouldBe "http://www.google.com/about/"
                }
            }
        },
    )
