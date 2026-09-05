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
package com.svenjacobs.app.leon.core.domain.sanitizer.avantlink

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AvantLink
import io.kotest.matchers.shouldBe

class AvantLinkTest :
    SanitizerSpec(
        AvantLink,
        {
            "clean" should
                {
                    "extract target from click.php referral link" {
                        clean(
                            "https://www.avantlink.com/click.php?tt=cl&merchant_id=68286571-393d-411e-9" +
                                "a0f-d1c2fa0648d6&website_id=37e930a6-52be-4816-9903-81d2e873459d&url=http" +
                                "s%3A%2F%2Fgreymantactical.com%2Fcollections%2Fshoprmpseries"
                        ) shouldBe "https://greymantactical.com/collections/shoprmpseries"
                    }
                }

            "matches" should
                {
                    "match avantlink.com click.php link" {
                        matches(
                            "https://www.avantlink.com/click.php?url=https%3A%2F%2Fexample.com"
                        ) shouldBe true
                    }

                    "not match a lookalike host" {
                        matches(
                            "https://avantlink.com.evil.com/click.php?url=https%3A%2F%2Fexample.com"
                        ) shouldBe false
                    }

                    "not match other avantlink.com paths" {
                        matches("https://www.avantlink.com/") shouldBe false
                    }
                }
        },
    )
