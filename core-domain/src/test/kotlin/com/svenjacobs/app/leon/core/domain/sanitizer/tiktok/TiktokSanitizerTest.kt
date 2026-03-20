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

package com.svenjacobs.app.leon.core.domain.sanitizer.tiktok

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class TiktokSanitizerTest :
    WordSpec(
        {
            val sanitizer = TiktokSanitizer()

            "invoke" should {

                "remove all parameters" {
                    var result = sanitizer(
                        "https://www.tiktok.com/@lihayk/video/7271645505522879751?" +
                            "is_from_webapp=1&sender_device=pc&web_id=7098566637619288452",
                    )

                    result shouldBe "https://www.tiktok.com/@lihayk/video/7271645505522879751"

                    result =
                        sanitizer(
                            "https://www.tiktok.com/@conqressesquotes/video/7284563007244389664" +
                                "?_t=3tmYqC7L494&_r=1",
                        )

                    result shouldBe
                        "https://www.tiktok.com/@conqressesquotes/video/7284563007244389664"

                    result = sanitizer("https://www.tiktok.com/@elaine_carroll?_t=8gneIBdmRJ1&_r=1")

                    result shouldBe "https://www.tiktok.com/@elaine_carroll"
                }
            }

            "matchesDomain" should {

                "match tiktok.com" {
                    sanitizer.matchesDomain("https://tiktok.com") shouldBe true
                }

                "match www.tiktok.com" {
                    sanitizer.matchesDomain("https://www.tiktok.com") shouldBe true
                }
            }
        },
    )
