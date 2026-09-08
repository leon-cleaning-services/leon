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
package com.svenjacobs.app.leon.core.domain.url

import com.svenjacobs.app.leon.core.domain.url.Url.Parameter
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf

class UrlTest :
    WordSpec({
        "parse" should
            {
                "extract all components" {
                    Url.parse("https://www.example.com:8443/some/path?a=1&b=2#anchor") shouldBe
                        Url(
                            scheme = "https",
                            host = "www.example.com",
                            port = 8443,
                            path = "/some/path",
                            parameters = persistentListOf(Parameter("a", "1"), Parameter("b", "2")),
                            fragment = "anchor",
                        )
                }

                "default the optional components" {
                    Url.parse("http://example.com") shouldBe
                        Url(scheme = "http", host = "example.com")
                }

                "keep a parameter without a value" {
                    Url.parse("https://example.com/?flag")?.parameters shouldBe
                        persistentListOf(Parameter("flag", null))
                }

                "keep a parameter with an empty value" {
                    Url.parse("https://example.com/?empty=")?.parameters shouldBe
                        persistentListOf(Parameter("empty", ""))
                }

                "keep duplicate parameter names" {
                    Url.parse("https://example.com/?a=1&a=2")?.parameters shouldBe
                        persistentListOf(Parameter("a", "1"), Parameter("a", "2"))
                }

                "keep a value containing an equals sign" {
                    Url.parse("https://example.com/?token=a=b")?.parameters shouldBe
                        persistentListOf(Parameter("token", "a=b"))
                }

                "treat an HTML escaped separator as a separator" {
                    Url.parse("https://example.com/?a=1&amp;b=2")?.parameters shouldBe
                        persistentListOf(Parameter("a", "1"), Parameter("b", "2"))
                }

                "not decode percent encoding" {
                    Url.parse("https://example.com/Hello%2FWorld?q=a%20b")?.path shouldBe
                        "/Hello%2FWorld"
                }

                "distinguish an absent fragment from an empty one" {
                    Url.parse("https://example.com/")?.fragment shouldBe null
                    Url.parse("https://example.com/#")?.fragment shouldBe ""
                }

                "parse an IPv6 host with a port" {
                    Url.parse("http://[::1]:8080/path") shouldBe
                        Url(scheme = "http", host = "[::1]", port = 8080, path = "/path")
                }

                "separate user information from the host" {
                    Url.parse("https://example.com@evil.com/path") shouldBe
                        Url(
                            scheme = "https",
                            host = "evil.com",
                            userInfo = "example.com",
                            path = "/path",
                        )

                    Url.parse("https://user:pass@example.com:8443/") shouldBe
                        Url(
                            scheme = "https",
                            host = "example.com",
                            userInfo = "user:pass",
                            port = 8443,
                            path = "/",
                        )
                }

                "return null for input which is not an HTTP URL" {
                    Url.parse("ftp://example.com") shouldBe null
                    Url.parse("example.com/path") shouldBe null
                    Url.parse("not a url") shouldBe null
                    Url.parse("") shouldBe null
                }

                "return null when the host is missing" { Url.parse("https:///path") shouldBe null }

                "return null when the port is not a number" {
                    Url.parse("https://example.com:port/path") shouldBe null
                }
            }

        "toString" should
            {
                "round trip" {
                    listOf(
                            "https://example.com",
                            "http://example.com/",
                            "https://www.example.com/some/path",
                            "https://example.com:8443/path",
                            "https://example.com/?a=1&b=2",
                            "https://example.com/?flag",
                            "https://example.com/?empty=",
                            "https://example.com/?a=1&a=2",
                            "https://example.com/#anchor",
                            "https://example.com/path?a=1#anchor",
                            "https://example.com/Hello%2FWorld?q=a%20b",
                            "http://[::1]:8080/path",
                            "https://example.com@evil.com/path",
                            "https://user:pass@example.com:8443/",
                            "https://www.some.site/?paramA=A&paramB=B#anchor",
                            "https://www.amazon.de/dp/B08J5F3G18/ref=abc?tag=xyz",
                            "https://open.substack.com/pub/example/p/slug?r=1&utm_source=mail",
                            "https://www.google.com/url?q=https%3A%2F%2Fexample.com&usg=AOvVaw0",
                        )
                        .forEach { url -> Url.parse(url).toString() shouldBe url }
                }

                "normalize an empty query section" {
                    Url.parse("https://example.com/?").toString() shouldBe "https://example.com/"
                }

                "normalize an HTML escaped separator" {
                    Url.parse("https://example.com/?a=1&amp;b=2").toString() shouldBe
                        "https://example.com/?a=1&b=2"
                }

                "normalize empty parameters" {
                    Url.parse("https://example.com/?a=1&&b=2").toString() shouldBe
                        "https://example.com/?a=1&b=2"
                }
            }
    })
