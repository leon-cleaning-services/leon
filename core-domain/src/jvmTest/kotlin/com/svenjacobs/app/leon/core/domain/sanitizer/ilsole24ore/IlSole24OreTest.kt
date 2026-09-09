/*
 * Léon - The URL Cleaner
 * Copyright (C) 2026 Sven Jacobs
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
package com.svenjacobs.app.leon.core.domain.sanitizer.ilsole24ore

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.IlSole24Ore
import io.kotest.matchers.shouldBe

class IlSole24OreTest :
    SanitizerSpec(
        IlSole24Ore,
        {
            "clean" should
                {
                    "remove all parameters" {
                        clean(
                            "https://www.ilsole24ore.com/art/la-rivoluzione-farmaci-dimagranti-che-potreb" +
                                "bero-prevenire-anche-cancro-AImYSJBC?cmpid=waw"
                        ) shouldBe
                            "https://www.ilsole24ore.com/art/la-rivoluzione-farmaci-dimagranti-che-potreb" +
                                "bero-prevenire-anche-cancro-AImYSJBC"
                    }
                }

            "matches" should
                {
                    "match ilsole24ore.com" { matches("https://ilsole24ore.com") shouldBe true }

                    "match www.ilsole24ore.com" {
                        matches("https://www.ilsole24ore.com") shouldBe true
                    }

                    "not match a lookalike host" {
                        matches("https://ilsole24ore.com.evil.com") shouldBe false
                    }
                }
        },
    )
