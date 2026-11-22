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
package com.svenjacobs.app.leon.core.domain.sanitizer.google

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.GooglePlay
import io.kotest.matchers.shouldBe

class GooglePlayTest :
    SanitizerSpec(
        GooglePlay,
        {
            "clean" should
                {
                    "remove the referrer parameter" {
                        clean(
                            "https://play.google.com/store/apps/details?id=com.samruston.buzzkill" +
                                "&referrer=utm_source=buzzkillapp.com%26utm_medium=referral" +
                                "%26utm_campaign=home",
                        ) shouldBe
                            "https://play.google.com/store/apps/details?id=com.samruston.buzzkill"
                    }
                }

            "matches" should
                {
                    "match an app page" {
                        matches("https://play.google.com/store/apps/details") shouldBe true
                    }

                    "not match other pages of Google Play" {
                        matches("https://play.google.com/about") shouldBe false
                    }

                    "not match a lookalike host" {
                        matches("https://play.google.com.evil.com/store/apps/details") shouldBe
                            false
                    }

                    "not match the Google Store" {
                        matches("https://store.google.com/store/apps/details") shouldBe false
                    }
                }
        },
    )
