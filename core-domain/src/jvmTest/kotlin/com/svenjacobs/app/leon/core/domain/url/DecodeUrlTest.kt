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

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class DecodeUrlTest :
    WordSpec({
        /** Decodes [url] the way the "Decode URL" option displays it. */
        fun decoded(url: String) = Url.parse(url)!!.decoded().toString()

        "decodeUrl" should
            {
                "decode UTF-8 multi-byte sequences" {
                    decodeUrl("M%C3%BCnchen") shouldBe "München"
                }

                "decode + as space" { decodeUrl("Hello+World") shouldBe "Hello World" }

                "decode %20 as space" { decodeUrl("Hello%20World") shouldBe "Hello World" }

                "decode every delimiter" {
                    decodeUrl("https%3A%2F%2Fa.site%2Fb%3Fc%3Dd%26e") shouldBe
                        "https://a.site/b?c=d&e"
                }

                "return a malformed trailing % unchanged" { decodeUrl("100%") shouldBe "100%" }

                "return input without escapes unchanged" {
                    decodeUrl("plain-text") shouldBe "plain-text"
                }
            }

        "decoded" should
            {
                "decode an encoded URL inside a parameter value" {
                    decoded(
                        "https://a.site/gp/r.html?U=https%3A%2F%2Fb.site%2Fdp%2FB01%3Fref%3Dx"
                    ) shouldBe "https://a.site/gp/r.html?U=https://b.site/dp/B01?ref=x"
                }

                "decode UTF-8 text in a parameter value" {
                    decoded("https://a.site/?q=M%C3%BCnchen") shouldBe "https://a.site/?q=München"
                }

                "decode UTF-8 text in the path" {
                    decoded("https://a.site/M%C3%BCnchen") shouldBe "https://a.site/München"
                }

                "decode UTF-8 text in the fragment" {
                    decoded("https://a.site/#M%C3%BCnchen") shouldBe "https://a.site/#München"
                }

                "keep an encoded parameter separator inside a value" {
                    decoded("https://a.site/?q=a%26b") shouldBe "https://a.site/?q=a%26b"
                }

                "keep an encoded fragment separator inside a value" {
                    decoded("https://a.site/?q=a%23b") shouldBe "https://a.site/?q=a%23b"
                }

                "keep an encoded plus inside a value, which means a space" {
                    decoded("https://a.site/?q=a%2Bb") shouldBe "https://a.site/?q=a%2Bb"
                }

                "keep a literal plus inside a value" {
                    decoded("https://a.site/?q=a+b") shouldBe "https://a.site/?q=a+b"
                }

                "keep an encoded equals sign inside a parameter name" {
                    decoded("https://a.site/?a%3Db=c") shouldBe "https://a.site/?a%3Db=c"
                }

                "keep an encoded slash inside the path" {
                    decoded("https://a.site/Hello%2FWorld") shouldBe "https://a.site/Hello%2FWorld"
                }

                "keep an encoded question mark inside the path" {
                    decoded("https://a.site/a%3Fb") shouldBe "https://a.site/a%3Fb"
                }

                "keep encoded spaces everywhere" {
                    decoded("https://a.site/a%20b?c=d%20e#f%20g") shouldBe
                        "https://a.site/a%20b?c=d%20e#f%20g"
                }

                "keep an encoded percent sign" {
                    decoded("https://a.site/?q=100%25") shouldBe "https://a.site/?q=100%25"
                }

                "keep the original escape casing of what it does not decode" {
                    decoded("https://a.site/a%2fb%2Fc") shouldBe "https://a.site/a%2fb%2Fc"
                }

                "leave the host alone" {
                    decoded("https://a%2Eb.site/") shouldBe "https://a%2Eb.site/"
                }

                "keep a parameter without a value" {
                    decoded("https://a.site/?flag&q=M%C3%BCnchen") shouldBe
                        "https://a.site/?flag&q=München"
                }

                "return a URL without escapes unchanged" {
                    decoded("https://a.site/path?q=1#f") shouldBe "https://a.site/path?q=1#f"
                }
            }
    })
