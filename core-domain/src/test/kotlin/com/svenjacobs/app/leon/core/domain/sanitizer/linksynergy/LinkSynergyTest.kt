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
package com.svenjacobs.app.leon.core.domain.sanitizer.linksynergy

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.LinkSynergy
import io.kotest.matchers.shouldBe

class LinkSynergyTest :
    SanitizerSpec(
        LinkSynergy,
        {
            "clean" should
                {
                    "extract URL from LinkSynergy link (\"murl\" parameter)" {
                        val result =
                            clean(
                                "https://click.linksynergy.com/link?id=kXQk6%2AivFEQ&offerid=1123623.20" +
                                    "516002704&type=15&murl=https%3A%2F%2Fwww.newegg.com%2Fp%2F23B-001E-003S3" +
                                    "%3Fitem%3D9SIAGREJ3S5851&u1=cbq-us-custom-tracking"
                            )

                        result shouldBe
                            "https://www.newegg.com/p/23B-001E-003S3?item=9SIAGREJ3S5851"
                    }
                }

            "matches" should
                {
                    "match LinkSynergy link" {
                        matches("https://click.linksynergy.com/link?id=a") shouldBe true
                    }

                    "not match host which continues after the domain" {
                        matches("https://linksynergy.evil.com?u=/link") shouldBe false
                    }
                }
        },
    )
