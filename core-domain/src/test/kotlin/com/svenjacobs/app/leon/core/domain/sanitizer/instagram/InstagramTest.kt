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
package com.svenjacobs.app.leon.core.domain.sanitizer.instagram

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Instagram
import io.kotest.matchers.shouldBe

class InstagramTest :
    SanitizerSpec(
        Instagram,
        {
            "clean" should
                {
                    "remove \"igsh\" parameter" {
                        val result =
                            clean("https://www.instagram.com/reel/Ceeg-VgI4yF/?igsh=YmMyMTA2M2Y=")

                        result shouldBe "https://www.instagram.com/reel/Ceeg-VgI4yF/"
                    }

                    "remove \"igsi\" parameter" {
                        val result =
                            clean("https://www.instagram.com/reel/Ceeg-VgI4yF/?igsi=YmMyMTA2M2Y=")

                        result shouldBe "https://www.instagram.com/reel/Ceeg-VgI4yF/"
                    }

                    "keep other parameters" {
                        val result =
                            clean("https://www.instagram.com/p/Ceeg-VgI4yF/?igsi=abc&img_index=2")

                        result shouldBe "https://www.instagram.com/p/Ceeg-VgI4yF/?img_index=2"
                    }
                }

            "matches" should
                {
                    "match instagram.com" {
                        matches("https://www.instagram.com/reel/Ceeg-VgI4yF/") shouldBe true
                    }

                    "not match other.com" {
                        matches("https://www.other.com/reel/Ceeg-VgI4yF/") shouldBe false
                    }
                }
        },
    )
