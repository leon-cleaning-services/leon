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
package com.svenjacobs.app.leon.core.domain.sanitizer.emptyparameters

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class EmptyParametersSanitizerTest :
    WordSpec({
        "invoke" should
            {
                "remove empty parameters" {
                    val sanitizer = EmptyParametersSanitizer()
                    val result =
                        sanitizer(
                            "https://www.buch24.de/shopdirekt.cgi?sessionid=1650808921-319962258&id" +
                                "=13241297&p=3&sid=&static=0&nav="
                        )

                    result shouldBe
                        "https://www.buch24.de/shopdirekt.cgi?sessionid=1650808921-319962" +
                            "258&id=13241297&p=3&static=0"
                }
            }
    })
