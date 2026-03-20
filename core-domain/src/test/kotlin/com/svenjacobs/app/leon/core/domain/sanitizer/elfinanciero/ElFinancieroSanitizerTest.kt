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

package com.svenjacobs.app.leon.core.domain.sanitizer.elfinanciero

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ElFinancieroSanitizerTest :
    WordSpec(
        {
            val sanitizer = ElFinancieroSanitizer()

            "invoke" should {

                "remove \"outputType\" parameter" {

                    val result = sanitizer(
                        "https://www.elfinanciero.com.mx/food-and-drink/2023/01/04/" +
                            "dia-de-reyes-2023-donde-comprar-rosca-de-tacos-en-la-cdmx/?outputType=amp",
                    )

                    result shouldBe "https://www.elfinanciero.com.mx/food-and-drink/2023/01/04/" +
                        "dia-de-reyes-2023-donde-comprar-rosca-de-tacos-en-la-cdmx/"
                }
            }

            "matchesDomain" should {

                "match for elfinanciero.com.mx" {
                    sanitizer.matchesDomain("https://www.elfinanciero.com.mx") shouldBe true
                }
            }
        },
    )
