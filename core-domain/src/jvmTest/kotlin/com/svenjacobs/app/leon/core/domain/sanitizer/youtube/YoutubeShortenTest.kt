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
package com.svenjacobs.app.leon.core.domain.sanitizer.youtube

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.YoutubeShorten
import io.kotest.matchers.shouldBe

class YoutubeShortenTest :
    SanitizerSpec(
        YoutubeShorten,
        {
            "clean" should
                {
                    "shorten a watch URL" {
                        clean("https://www.youtube.com/watch?v=dQw4w9WgXcQ") shouldBe
                            "https://youtu.be/dQw4w9WgXcQ"
                    }

                    "shorten a watch URL without www" {
                        clean("https://youtube.com/watch?v=dQw4w9WgXcQ") shouldBe
                            "https://youtu.be/dQw4w9WgXcQ"
                    }

                    "shorten a mobile watch URL" {
                        clean("https://m.youtube.com/watch?v=dQw4w9WgXcQ") shouldBe
                            "https://youtu.be/dQw4w9WgXcQ"
                    }

                    "keep the time and the playlist, and drop everything else" {
                        clean(
                            "https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=42&si=abc&list=PL123" +
                                "&feature=share&pp=xyz",
                        ) shouldBe "https://youtu.be/dQw4w9WgXcQ?t=42&list=PL123"
                    }

                    "leave a watch URL without video id alone" {
                        clean("https://www.youtube.com/watch?t=42") shouldBe
                            "https://www.youtube.com/watch?t=42"
                    }

                    "not use a video id of unexpected shape" {
                        clean("https://www.youtube.com/watch?v=a%20b") shouldBe
                            "https://www.youtube.com/watch?v=a%20b"
                    }
                }

            "matches" should
                {
                    "match youtube.com watch URLs" {
                        matches("https://youtube.com/watch?v=x") shouldBe true
                        matches("https://www.youtube.com/watch?v=x") shouldBe true
                    }

                    "match m.youtube.com watch URLs" {
                        matches("https://m.youtube.com/watch?v=x") shouldBe true
                    }

                    "not match other paths" {
                        matches("https://youtube.com/channel/abc") shouldBe false
                    }

                    "not match a lookalike host" {
                        matches("https://youtube.com.evil.com/watch?v=x") shouldBe false
                    }

                    "not match youtu.be" { matches("https://youtu.be/x") shouldBe false }
                }
        },
    )
