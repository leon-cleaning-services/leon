/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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

class YoutubeSanitizerTest :
    WordSpec(
        {
            val sanitizer = YoutubeSanitizer()

            "invoke" should {

                "remove all parameters except \"v\" from video URLs" {
                    sanitizer(
                        "https://m.youtube.com/watch?v=CvFH_6DNRCY&pp=ygUHZGVidXNzeQ%3D%3D",
                    ) shouldBe "https://m.youtube.com/watch?v=CvFH_6DNRCY"
                }

                "remove all parameters except \"t\" from video URLs" {
                    sanitizer(
                        "https://m.youtube.com/watch?v=CvFH_6DNRCY&t=125",
                    ) shouldBe "https://m.youtube.com/watch?v=CvFH_6DNRCY&t=125"
                }

                "remove all parameters except \"search_query\" from search URLs" {
                    sanitizer(
                        "https://m.youtube.com/results?sp=mAEA&search_query=funny+dog+video",
                    ) shouldBe
                        "https://m.youtube.com/results&search_query=funny+dog+video"
                }

                "remove all parameters except \"list\" from playlist URLs" {
                    sanitizer(
                        "https://youtube.com/playlist?list=PLkqz3S84Tw-QYEdfTLBzxJ1FAprtqeE" +
                            "pJ&si=2tDDmSKejG2GTtj5",
                    ) shouldBe
                        "https://youtube.com/playlist?list=PLkqz3S84Tw-QYEdfTLBzxJ1FAprtqeEpJ"
                }

                "remove all parameters except \"v\" from YouTube Music URLs" {
                    sanitizer(
                        "https://music.youtube.com/watch?v=KGFkMD2zotU&si=JrZ7QzX4VeMrfzp8",
                    ) shouldBe
                        "https://music.youtube.com/watch?v=KGFkMD2zotU"
                }

                "remove parameters from youtu.be domain" {
                    sanitizer(
                        "https://youtu.be/RvRhUHTV_8k?si=OXYBmGTMXib1jlA2?si=UA0UVnrmPbK612Lu",
                    ) shouldBe "https://youtu.be/RvRhUHTV_8k"
                }

                "not remove \"channel_id\" from feed URLs" {
                    sanitizer(
                        "https://www.youtube.com/feeds/videos.xml?channel_id=UCOfZQq8eZiMjyNfmnaVlBbw",
                    ) shouldBe "https://www.youtube.com/feeds/videos.xml?channel_id=UCOfZQq8eZiMjyNfmnaVlBbw"
                }
            }

            "matchesDomain" should {

                "match youtube.com domain" {
                    sanitizer.matchesDomain("https://youtube.com/") shouldBe true
                }

                "match m.youtube.com domain" {
                    sanitizer.matchesDomain("https://m.youtube.com/") shouldBe true
                }

                "match music.youtube.com domain" {
                    sanitizer.matchesDomain("https://music.youtube.com/") shouldBe true
                }

                "match youtu.be domain" {
                    sanitizer.matchesDomain("https://youtu.be/") shouldBe true
                }
            }
        },
    )
