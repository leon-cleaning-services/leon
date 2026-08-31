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
package com.svenjacobs.app.leon.core.domain.sanitizer.facebook

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.Facebook
import io.kotest.matchers.shouldBe

class FacebookTest :
    SanitizerSpec(
        Facebook,
        {
            "clean" should
                {
                    "clean facebook.com reel URLs" {
                        clean(
                            "https://www.facebook.com/reel/1242384407160280?sfnsn=scwspmo"
                        ) shouldBe "https://www.facebook.com/reel/1242384407160280"
                    }

                    "clean m.facebook.com story URLs" {
                        clean(
                            "https://m.facebook.com/story.php?story_fbid=pfbid0HqS6zLZvNrQt6ACvjv3h" +
                                "Kq6khpVse437nWSq2jBifKRD5sVH2XRLC3zz8aA7TKkWl&id=4&sfnsn=wiwspmo&mibext" +
                                "id=XzsMCV"
                        ) shouldBe
                            "https://m.facebook.com/story.php?story_fbid=pfbid0HqS6zLZvNrQt6ACvjv" +
                                "3hKq6khpVse437nWSq2jBifKRD5sVH2XRLC3zz8aA7TKkWl&id=4"
                    }
                }

            "matches" should
                {
                    "match facebook.com" { matches("https://facebook.com") shouldBe true }

                    "match m.facebook.com" { matches("https://m.facebook.com") shouldBe true }

                    "not match host which only starts with facebook.com" {
                        matches("https://facebook.com.evil.com") shouldBe false
                    }

                    "not match host where the dot is another character" {
                        matches("https://facebook-com") shouldBe false
                    }
                }
        },
    )
