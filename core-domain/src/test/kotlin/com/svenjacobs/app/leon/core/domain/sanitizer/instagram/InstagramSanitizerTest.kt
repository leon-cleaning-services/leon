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
package com.svenjacobs.app.leon.core.domain.sanitizer.instagram

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class InstagramSanitizerTest :
    WordSpec({
        val sanitizer = InstagramSanitizer()

        "invoke" should
            {
                "remove \"igsh\" parameter" {
                    val result =
                        sanitizer("https://www.instagram.com/reel/Ceeg-VgI4yF/?igsh=YmMyMTA2M2Y=")

                    result shouldBe "https://www.instagram.com/reel/Ceeg-VgI4yF/"
                }

                "remove \"igsi\" parameter" {
                    val result =
                        sanitizer("https://www.instagram.com/reel/Ceeg-VgI4yF/?igsi=YmMyMTA2M2Y=")

                    result shouldBe "https://www.instagram.com/reel/Ceeg-VgI4yF/"
                }

                "keep other parameters" {
                    val result =
                        sanitizer("https://www.instagram.com/p/Ceeg-VgI4yF/?igsi=abc&img_index=2")

                    result shouldBe "https://www.instagram.com/p/Ceeg-VgI4yF/&img_index=2"
                }
            }

        "matchesDomain" should
            {
                "match instagram.com" {
                    sanitizer.matchesDomain("https://www.instagram.com/reel/Ceeg-VgI4yF/") shouldBe
                        true
                }

                "not match other.com" {
                    sanitizer.matchesDomain("https://www.other.com/reel/Ceeg-VgI4yF/") shouldBe
                        false
                }
            }
    })
