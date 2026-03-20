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

package com.svenjacobs.app.leon.core.domain.sanitizer.google

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class GoogleSearchSanitizerTest :
    WordSpec(
        {

            val sanitizer = GoogleSearchSanitizer()

            "invoke" should {

                "extract URL from Google search link (\"url\" parameter)" {
                    val result = sanitizer(
                        "https://www.google.com/url?sa=t&source=web&rct=j&url=https://www.regex" +
                            "tester.com/&ved=2ahUKEwiTpvflqP34AhXOgv0HHSNQCOIQFnoECAcQAQ&usg=AOvVaw1w" +
                            "BmEA7TD90QkZPu7zcsOa",
                    )

                    result shouldBe "https://www.regextester.com/"
                }

                "extract URL from Google search link (\"q\" parameter)" {
                    val result = sanitizer(
                        "https://www.google.com/url?sa=t&source=web&rct=j&q=https://www.regexte" +
                            "ster.com/&ved=2ahUKEwiTpvflqP34AhXOgv0HHSNQCOIQFnoECAcQAQ&usg=AOvVaw1wBm" +
                            "EA7TD90QkZPu7zcsOa",
                    )

                    result shouldBe "https://www.regextester.com/"
                }
            }
        },
    )
