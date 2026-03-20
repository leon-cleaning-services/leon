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

package com.svenjacobs.app.leon.core.domain.sanitizer.reddit

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class RedditSanitizerTest :
    WordSpec(
        {
            val sanitizer = RedditSanitizer()

            "invoke" should {

                "clean reddit.com URLs" {
                    sanitizer(
                        "https://www.reddit.com/r/fossdroid/comments/1659ic4/material_files_is_" +
                            "still_maintained/?share_id=Toc_TMpn88yOUd7Z-y0xv&utm_content=1&utm_mediu" +
                            "m=android_app&utm_name=androidcss&utm_source=share&utm_term=1",
                    ) shouldBe
                        "https://www.reddit.com/r/fossdroid/comments/1659ic4/material_files_is" +
                        "_still_maintained/"
                }
            }

            "matchesDomain" should {

                "match for reddit.com" {
                    sanitizer.matchesDomain("https://reddit.com") shouldBe true
                }

                "not match for out.reddit.com" {
                    sanitizer.matchesDomain("https://out.reddit.com") shouldBe false
                }
            }
        },
    )
