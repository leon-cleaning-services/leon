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
package com.svenjacobs.app.leon.core.domain.sanitizer.ikea

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Ikea
import io.kotest.matchers.shouldBe

class IkeaTest :
    SanitizerSpec(
        Ikea,
        {
            "clean" should
                {
                    "remove all parameters from ikea.com URL" {
                        val result =
                            clean(
                                "https://www.ikea.com/ch/en/p/billy-bookcase-white-30263844/?gad_source=1&extProvId=5"
                            )
                        result shouldBe
                            "https://www.ikea.com/ch/en/p/billy-bookcase-white-30263844/"
                    }
                }

            "matches" should
                {
                    "match for ikea.com" {
                        matches(
                            "https://www.ikea.com/ch/en/p/billy-bookcase-white-30263844/"
                        ) shouldBe true
                    }
                    "not match for other domains" {
                        matches("https://www.example.com/product/123") shouldBe false
                    }
                }
        },
    )
