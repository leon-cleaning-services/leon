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
package com.svenjacobs.app.leon.core.domain.sanitizer.mydealz

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class MyDealzParametersSanizierTest :
    WordSpec({
        "invoke" should
            {
                "convert www.mydealz.de app URLs (with ad redirect) into base/direct URLs" {
                    val sanitizer = MyDealzParametersSanitizer()
                    val result =
                        sanitizer(
                            "https://www.mydealz.de/diskussion/gratis-adidas-fussball-trikot-" +
                                "2383743?pprmrkntfctnsrd=123456789&UATypeId=18"
                        )
                    result shouldBe
                        "https://www.mydealz.de/diskussion/gratis-adidas-fussball-" +
                            "trikot-2383743"
                }
            }
    })
