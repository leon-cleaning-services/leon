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

package com.svenjacobs.app.leon.core.domain.sanitizer.sessionids

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SessionIdsSanitizerTest :
    WordSpec(
        {

            val sanitizer = SessionIdsSanitizer()

            "invoke" should {

                "remove \"sessionid\" parameter" {
                    val result = sanitizer(
                        "https://www.buch24.de/shopdirekt.cgi?sessionid=1650808921-319962258&id=13241297",
                    )

                    result shouldBe "https://www.buch24.de/shopdirekt.cgi&id=13241297"
                }

                "remove \"jsessionid\" parameter" {
                    val result = sanitizer(
                        "https://www.buch24.de/shopdirekt.cgi?jsessionid=1650808921-319962258&id=13241297",
                    )

                    result shouldBe "https://www.buch24.de/shopdirekt.cgi&id=13241297"
                }
            }
        },
    )
