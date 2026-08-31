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
package com.svenjacobs.app.leon.core.domain.sanitizer

import com.svenjacobs.app.leon.core.domain.matches
import com.svenjacobs.app.leon.core.domain.url.Url
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class MatchTest :
    WordSpec({
        fun Match.appliesTo(url: String): Boolean = matches(requireNotNull(Url.parse(url)))

        "Any" should
            {
                "match every host" {
                    Match().appliesTo("https://example.com/path") shouldBe true
                    Match().appliesTo("http://anything.at.all") shouldBe true
                }
            }

        "Domain" should
            {
                val match = Match(HostMatch.Domain("some.example.com"))

                "match with either scheme" {
                    match.appliesTo("https://some.example.com/path") shouldBe true
                    match.appliesTo("http://some.example.com/path") shouldBe true
                }

                "ignore a leading www" {
                    match.appliesTo("https://www.some.example.com") shouldBe true
                }

                "ignore the case of the host" {
                    match.appliesTo("https://Some.Example.COM") shouldBe true
                }

                "match with port, query and fragment" {
                    Match(HostMatch.Domain("example.com"))
                        .appliesTo("https://example.com:8443/path?a=1#top") shouldBe true
                }

                "not match another host" {
                    match.appliesTo("https://other.example.com") shouldBe false
                }

                "not match a host which only starts with the domain" {
                    val match = Match(HostMatch.Domain("example.com"))

                    match.appliesTo("https://example.com.evil.com/path") shouldBe false
                    match.appliesTo("https://example.com.evil.com:443/p") shouldBe false
                    match.appliesTo("https://example.computer/path") shouldBe false
                    match.appliesTo("https://example.com-evil.com") shouldBe false
                }

                "not match the domain in user information" {
                    Match(HostMatch.Domain("example.com"))
                        .appliesTo("https://example.com@evil.com/path") shouldBe false
                }

                "not match a subdomain" {
                    Match(HostMatch.Domain("example.com"))
                        .appliesTo("https://sub.example.com") shouldBe false
                }
            }

        "Subdomains" should
            {
                val match = Match(HostMatch.Subdomains("example.com"))

                "match the domain itself and nested subdomains" {
                    match.appliesTo("https://example.com/path") shouldBe true
                    match.appliesTo("https://sub.example.com") shouldBe true
                    match.appliesTo("https://a.b.example.com") shouldBe true
                    match.appliesTo("https://www.example.com") shouldBe true
                }

                "not match a lookalike host" {
                    match.appliesTo("https://example.com.evil.com") shouldBe false
                    match.appliesTo("https://notexample.com") shouldBe false
                    match.appliesTo("https://sub.example2.com") shouldBe false
                    match.appliesTo("https://evil.com/?u=example.com") shouldBe false
                }
            }

        "Pattern" should
            {
                "match a host by regular expression" {
                    val match = Match(HostMatch.Pattern("google\\.[^.]+"))

                    match.appliesTo("https://google.com/search") shouldBe true
                    match.appliesTo("https://www.google.de/search") shouldBe true
                    match.appliesTo("https://google.co.uk/search") shouldBe false
                }

                "not match when the pattern covers only part of the host" {
                    val match = Match(HostMatch.Pattern("example\\.com"))

                    match.appliesTo("https://example.com.evil.com") shouldBe false
                    match.appliesTo("https://sub.example.com") shouldBe false
                }
            }

        "pathPrefix" should
            {
                val match = Match(HostMatch.Domain("youtube.com"), pathPrefix = "/redirect")

                "match when the path starts with the prefix" {
                    match.appliesTo("https://www.youtube.com/redirect?q=x") shouldBe true
                    match.appliesTo("https://www.youtube.com/redirect/more") shouldBe true
                }

                "not match another path" {
                    match.appliesTo("https://www.youtube.com/watch?v=x") shouldBe false
                    match.appliesTo("https://www.youtube.com/") shouldBe false
                }

                "not match when only the host matches" {
                    Match(HostMatch.Pattern("google\\.[^.]+"), pathPrefix = "/url")
                        .appliesTo("https://www.google.com/search?q=x") shouldBe false
                }
            }
    })
