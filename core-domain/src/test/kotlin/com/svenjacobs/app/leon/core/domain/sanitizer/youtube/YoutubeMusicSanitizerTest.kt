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

package com.svenjacobs.app.leon.core.domain.sanitizer.youtube

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class YoutubeMusicSanitizerTest :
    WordSpec(
        {
            val sanitizer = YoutubeMusicSanitizer()

            "matchesDomain" should {

                "match music.youtube.com domain" {
                    sanitizer.matchesDomain("https://music.youtube.com/") shouldBe true
                }

                "not match regular youtube.com domain" {
                    sanitizer.matchesDomain("https://youtube.com/") shouldBe false
                }
            }

            "invoke" should {

                "convert music.youtube.com domain to youtube.com" {
                    sanitizer(
                        "https://music.youtube.com/playlist?list=RDCLAK5uy_mPolD_J22gS1SKxufARW" +
                            "cTZd1UrAH_0ZI",
                    ) shouldBe
                        "https://youtube.com/playlist?list=RDCLAK5uy_mPolD_J22gS1SKxufARWcTZd1" +
                        "UrAH_0ZI"
                }
            }
        },
    )
