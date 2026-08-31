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

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GoogleMaps
import io.kotest.matchers.shouldBe

class GoogleMapsSanitizerTest :
    SanitizerSpec(
        GoogleMaps,
        {
            "clean" should
                {
                    "remove tracking data from Google Maps URL" {
                        clean(
                            "https://www.google.com/maps/place/Rotonda+Principessa+Mafalda+di+" +
                                "Savoia,+40141+Bologna+BO,+Italy/@44.4596478,11.3745947,17z/data=" +
                                "!4m6!3m5!1s0x477e2add34e42e53:0x8558150e6cd5413a!8m2!3d44.459786" +
                                "!4d11.3750265!16s%2Fg%2F11g6bfwcnd"
                        ) shouldBe "https://www.google.com/maps/@44.4596478,11.3745947,17z"
                    }

                    "handle maps.google.com URL" {
                        clean(
                            "https://maps.google.com/maps/place/SomePlace/@48.8566,2.3522,13z/data=!xyz"
                        ) shouldBe "https://www.google.com/maps/@48.8566,2.3522,13z"
                    }

                    "handle URL with negative coordinates" {
                        clean(
                            "https://www.google.com/maps/place/Buenos+Aires/@-34.6037,-58.3816,12z/data=!xyz"
                        ) shouldBe "https://www.google.com/maps/@-34.6037,-58.3816,12z"
                    }

                    "return unchanged URL when no coordinates found" {
                        clean("https://www.google.com/maps/place/SomePlace") shouldBe
                            "https://www.google.com/maps/place/SomePlace"
                    }
                }

            "matches" should
                {
                    "match www.google.com/maps" {
                        matches("https://www.google.com/maps/place/SomePlace") shouldBe true
                    }

                    "match google.com/maps" {
                        matches("https://google.com/maps/place/SomePlace") shouldBe true
                    }

                    "match maps.google.com" {
                        matches("https://maps.google.com/maps/place/SomePlace") shouldBe true
                    }

                    "not match www.google.com without /maps path" {
                        matches("https://www.google.com/search?q=test") shouldBe false
                    }

                    "not match other domains" {
                        matches("https://www.other.com/maps") shouldBe false
                    }
                }
        },
    )
