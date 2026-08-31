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

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.YoutubeMusicSanitizer
import io.kotest.matchers.shouldBe

class YoutubeMusicSanitizerTest :
    SanitizerSpec(
        YoutubeMusicSanitizer,
        {
            "matches" should
                {
                    "match music.youtube.com domain" {
                        matches("https://music.youtube.com/") shouldBe true
                    }

                    "not match regular youtube.com domain" {
                        matches("https://youtube.com/") shouldBe false
                    }
                }

            "clean" should
                {
                    "convert music.youtube.com domain to youtube.com" {
                        clean(
                            "https://music.youtube.com/playlist?list=RDCLAK5uy_mPolD_J22gS1SKxufARW" +
                                "cTZd1UrAH_0ZI"
                        ) shouldBe
                            "https://youtube.com/playlist?list=RDCLAK5uy_mPolD_J22gS1SKxufARWcTZd1" +
                                "UrAH_0ZI"
                    }
                }

            "matches" should
                {
                    "match music.youtube.com" {
                        matches("https://music.youtube.com/playlist?list=a") shouldBe true
                    }

                    "not match music.youtube.com inside another URL" {
                        matches("https://evil.com/?u=music.youtube.com") shouldBe false
                    }
                }
        },
    )
