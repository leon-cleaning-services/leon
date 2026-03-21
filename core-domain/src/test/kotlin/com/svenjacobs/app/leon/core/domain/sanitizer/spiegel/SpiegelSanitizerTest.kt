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
package com.svenjacobs.app.leon.core.domain.sanitizer.spiegel

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SpiegelSanitizerTest :
    WordSpec({
        "invoke" should
            {
                "remove all parameters from Spiegel URL" {
                    val sanitizer = SpiegelSanitizer()
                    val result =
                        sanitizer(
                            "https://www.spiegel.de/netzwelt/elon-musk-twitter-sperrt-konten-mehrerer" +
                                "-journalisten-von-new-york-times-washington-post-etc-a-040213a0-aa1e-4" +
                                "627-9a5a-69d5f4f929fb?sara_ecid=soci_upd_KsBF0AFjflf0DZCxpPYDCQgO1dEMph1"
                        )

                    result shouldBe
                        "https://www.spiegel.de/netzwelt/elon-musk-twitter-sperrt-k" +
                            "onten-mehrerer-journalisten-von-new-york-times-washington-post-etc-a-0" +
                            "40213a0-aa1e-4627-9a5a-69d5f4f929fb"
                }
            }
    })
