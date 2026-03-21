/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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
package com.svenjacobs.app.leon.core.domain.sanitizer.autotrader

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class AutoTraderSanitizerTest :
    WordSpec({
        val sanitizer = AutoTraderSanitizer()

        "invoke" should
            {
                "remove all parameters from AutoTrader car details URL" {
                    val result =
                        sanitizer(
                            "https://www.autotrader.co.uk/car-details/202602280303309?sort=relevance" +
                                "&twcs=true&searchId=1217a105-bfdf-458d-a7e9-5d01e59b2604" +
                                "&exclude-writeoff-categories=on&fuel-type=Petrol&insuranceGroup=20U" +
                                "&maximum-mileage=100000&minimum-badge-engine-size=1.4&page=1" +
                                "&postcode=w148nw&price-from=5000&price-to=10000" +
                                "&transmission=Automatic&year-from=2014" +
                                "&advertising-location=at_cars&fromsra=" +
                                "&backLinkQueryParams=exclude-writeoff-categories%3Don%26fuel-type" +
                                "%3DPetrol%26insuranceGroup%3D20U%26maximum-mileage%3D100000" +
                                "%26minimum-badge-engine-size%3D1.4%26postcode%3Dw148nw" +
                                "%26price-from%3D5000%26price-to%3D10000%26sort%3Drelevance" +
                                "%26transmission%3DAutomatic%26year-from%3D2014%26refresh%3Dtrue" +
                                "%26flrfc%3D1&calc-deposit=700&calc-term=48&calc-mileage=10000"
                        )

                    result shouldBe "https://www.autotrader.co.uk/car-details/202602280303309"
                }
            }

        "matchesDomain" should
            {
                "match AutoTrader car details URL" {
                    sanitizer.matchesDomain(
                        "https://www.autotrader.co.uk/car-details/202602280303309"
                    ) shouldBe true
                }
            }
    })
