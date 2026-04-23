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
package com.svenjacobs.app.leon.core.common.domain

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DomainExtensionsTest :
    WordSpec({
        "matchesDomain" should
            {
                "match domain with https://" {
                    "https://some.example.com/path".matchesDomain("some.example.com") shouldBe true
                }

                "match domain with http://" {
                    "http://some.example.com/path".matchesDomain("some.example.com") shouldBe true
                }

                "match domain with www" {
                    "https://www.some.example.com".matchesDomain("some.example.com") shouldBe true
                    "http://www.some.example.com".matchesDomain("some.example.com") shouldBe true
                }

                "match domain without http(s)" {
                    "some.example.com".matchesDomain("some.example.com") shouldBe true
                    "www.some.example.com".matchesDomain("some.example.com") shouldBe true
                }

                "match domain with regular expression values" {
                    "https://aliexpress.com/item/32948511896"
                        .matchesDomainRegex(domain = "aliexpress\\..+/item/") shouldBe true
                }

                "not match domain" {
                    "other.example.com".matchesDomain("some.example.com") shouldBe false
                }

                "match subdomains" {
                    "sub.example.com".matchesSubdomains("example.com") shouldBe true
                    "sub.example2.com".matchesSubdomains("example.com") shouldBe false
                }
            }
    })
