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

package com.svenjacobs.app.leon.core.domain.sanitizer.cargurus

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class CarGurusSanitizerTest :
    WordSpec(
        {
            val sanitizer = CarGurusSanitizer()

            "invoke" should {

                "keep only listingId and entitySelectingHelper.selectedEntity" {
                    val result = sanitizer(
                        "https://www.cargurus.co.uk/Cars/inventorylisting/vdp.action" +
                            "?listingId=156943295&insuranceGroupLevels=11-20" +
                            "&insuranceGroupLevels=1-10&ratings=2&ratings=1&ratings=5" +
                            "&fuelTypes=2&newUsed=2&newUsed=3&transmissionTypes=AUTOMATIC" +
                            "&zip=NW10&distance=50&sourceContext=untrackedExternal_false_0" +
                            "&hideWithoutPhotos=true&maxAccidents=0&hideFleet=true" +
                            "&sortDir=ASC&sortType=DEAL_SCORE&srpVariation=DEFAULT_SEARCH" +
                            "&isDeliveryEnabled=true&nonShippableBaseline=0&maxPrice=10000" +
                            "&minPrice=5000&maxMileage=100000&startYear=2014" +
                            "&ulezCompliantOnly=true&minEngineDisplacement=1.4" +
                            "&entitySelectingHelper.selectedEntity=AUTO" +
                            "#listing=156943295/NONE/DEFAULT",
                    )

                    result shouldBe "https://www.cargurus.co.uk/Cars/inventorylisting/vdp.action" +
                        "?listingId=156943295" +
                        "&entitySelectingHelper.selectedEntity=AUTO" +
                        "#listing=156943295/NONE/DEFAULT"
                }
            }

            "matchesDomain" should {

                "match CarGurus VDP URL" {
                    sanitizer.matchesDomain(
                        "https://www.cargurus.co.uk/Cars/inventorylisting/vdp.action?listingId=156943295",
                    ) shouldBe true
                }
            }
        },
    )
