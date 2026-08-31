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
package com.svenjacobs.app.leon.core.domain.sanitizer.mydealz

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerSpec
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.MyDealzRedirects
import io.kotest.matchers.shouldBe

class MyDealzRedirectsTest :
    SanitizerSpec(
        MyDealzRedirects,
        {
            "clean" should
                {
                    "convert www.mydealz.de app URLs (with ad redirect) into base/direct URLs" {
                        val result = clean("https://www.mydealz.de/share-deal-from-app/2117879")
                        result shouldBe "https://www.mydealz.de/deals/a-2117879"
                    }

                    "convert mydealz.de app URLs (with ad redirect) into base/direct URLs" {
                        val result = clean("https://mydealz.de/share-deal-from-app/2117879")
                        result shouldBe "https://mydealz.de/deals/a-2117879"
                    }

                    "convert preisjaeger.at app URLs (with ad redirect) into base/direct URLs" {
                        val result = clean("https://preisjaeger.at/share-deal-from-app/2117879")
                        result shouldBe "https://preisjaeger.at/deals/a-2117879"
                    }
                }

            "matches" should
                {
                    "match MyDealz domains" {
                        matches("https://www.mydealz.de/deals/a-1") shouldBe true
                        matches("https://www.hotukdeals.com/deals/a-1") shouldBe true
                    }

                    "not match MyDealz domain inside another URL" {
                        matches("https://evil.com/?u=mydealz.de") shouldBe false
                    }
                }
        },
    )
