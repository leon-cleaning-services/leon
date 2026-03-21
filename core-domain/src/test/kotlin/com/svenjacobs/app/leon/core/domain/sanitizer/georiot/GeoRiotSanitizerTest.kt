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
package com.svenjacobs.app.leon.core.domain.sanitizer.georiot

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class GeoRiotSanitizerTest :
    WordSpec({
        val sanitizer = GeoRiotSanitizer()

        "invoke" should
            {
                "extract URL from GeoRiot link (\"GB_URL\" parameter)" {
                    val result =
                        sanitizer(
                            "https://target.georiot.com/Proxy.ashx?tsid=8429&GR_URL=https%3A%2F%2Fw" +
                                "ww.amazon.com%2Fdp%2FB0B3Q23BT4%3Ftag%3Dhawk-future-20%26linkCode%3Dogi%" +
                                "26th%3D1%26psc%3D1%26ascsubtag%3Dcbq-us-custom-tracking-20"
                        )

                    result shouldBe
                        "https://www.amazon.com/dp/B0B3Q23BT4?tag=hawk-future-20&linkCode" +
                            "=ogi&th=1&psc=1&ascsubtag=cbq-us-custom-tracking-20"
                }
            }
    })
