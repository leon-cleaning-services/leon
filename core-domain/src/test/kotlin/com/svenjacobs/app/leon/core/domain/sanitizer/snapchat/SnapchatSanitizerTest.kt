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
package com.svenjacobs.app.leon.core.domain.sanitizer.snapchat

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.SnapchatSanitizer
import io.kotest.matchers.shouldBe

class SnapchatSanitizerTest :
    SanitizerSpec(
        SnapchatSanitizer,
        {
            "clean" should
                {
                    "remove all query parameters from Snapchat URLs" {
                        clean(
                            "https://www.snapchat.com/add/thesmileybunch?share_id=tUPMpk8AeX0&locale=fi-Fl-u-fw-mon-mu-celsius"
                        ) shouldBe "https://www.snapchat.com/add/thesmileybunch"
                    }
                }

            "matches" should
                {
                    "match snapchat.com" { matches("https://www.snapchat.com") shouldBe true }

                    "not match other domains" { matches("https://www.example.com") shouldBe false }
                }
        },
    )
