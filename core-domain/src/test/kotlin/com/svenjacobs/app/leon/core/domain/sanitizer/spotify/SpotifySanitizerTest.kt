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

package com.svenjacobs.app.leon.core.domain.sanitizer.spotify

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SpotifySanitizerTest :
    WordSpec(
        {
            val sanitizer = SpotifySanitizer()

            "invoke" should {

                "remove all parameters" {

                    var result = sanitizer(
                        "https://open.spotify.com/album/5N2BIKomahKMAAirp8tiBN?si=BICcHVzTTqmqt" +
                            "82Y6f2e_A&utm_source=native-share-menu",
                    )

                    result shouldBe "https://open.spotify.com/album/5N2BIKomahKMAAirp8tiBN"

                    result = sanitizer(
                        "https://open.spotify.com/track/5LEbg97KkVmAv9qHR7bS59?si=CXCVCQplRkqNt" +
                            "DWW42dXgA&context=spotify%3Aplaylist%3A37i9dQZF1EpjSENbNnZRJr",
                    )

                    result shouldBe "https://open.spotify.com/track/5LEbg97KkVmAv9qHR7bS59"
                }
            }

            "matchesDomain" should {

                "match spotify.com" {
                    sanitizer.matchesDomain("https://spotify.com") shouldBe true
                }

                "match open.spotify.com" {
                    sanitizer.matchesDomain("https://open.spotify.com") shouldBe true
                }
            }
        },
    )
