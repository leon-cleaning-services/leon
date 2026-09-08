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
        "decodeUrl" should
            {
                "decode UTF-8 multi-byte sequences without keepStructure" {
                    decodeUrl("M%C3%BCnchen") shouldBe "München"
                }

                "decode UTF-8 multi-byte sequences with keepStructure" {
                    decodeUrl("M%C3%BCnchen", keepStructure = true) shouldBe "München"
                }

                "decode + as space without keepStructure" {
                    decodeUrl("Hello+World") shouldBe "Hello World"
                }

                "keep a literal + as + with keepStructure" {
                    decodeUrl("Hello+World", keepStructure = true) shouldBe "Hello+World"
                }

                "decode %20 as space without keepStructure" {
                    decodeUrl("Hello%20World") shouldBe "Hello World"
                }

                "keep %20 as %20 with keepStructure" {
                    decodeUrl("Hello%20World", keepStructure = true) shouldBe "Hello%20World"
                }

                "decode %2F without keepStructure" {
                    decodeUrl("Hello%2FWorld") shouldBe "Hello/World"
                }

                "keep %2F as %2F with keepStructure" {
                    decodeUrl("Hello%2FWorld", keepStructure = true) shouldBe "Hello%2FWorld"
                }

                "keep %26 as %26 with keepStructure" {
                    decodeUrl("a%26b", keepStructure = true) shouldBe "a%26b"
                }

                "keep %3F as %3F with keepStructure" {
                    decodeUrl("a%3Fb", keepStructure = true) shouldBe "a%3Fb"
                }

                "keep %23 as %23 with keepStructure" {
                    decodeUrl("a%23b", keepStructure = true) shouldBe "a%23b"
                }

                "keep %25 as %25 with keepStructure" {
                    decodeUrl("a%25b", keepStructure = true) shouldBe "a%25b"
                }

                "keep %3D as %3D with keepStructure" {
                    decodeUrl("a%3Db", keepStructure = true) shouldBe "a%3Db"
                }

                "keep %3A as %3A with keepStructure" {
                    decodeUrl("a%3Ab", keepStructure = true) shouldBe "a%3Ab"
                }

                "preserve the original escape casing with keepStructure" {
                    decodeUrl("a%2fb%2Fc", keepStructure = true) shouldBe "a%2fb%2Fc"
                }

                "return a malformed trailing % unchanged without keepStructure" {
                    decodeUrl("100%") shouldBe "100%"
                }

                "return a malformed trailing % unchanged with keepStructure" {
                    decodeUrl("100%", keepStructure = true) shouldBe "100%"
                }

                "return text without escapes unchanged" {
                    decodeUrl("plain-text") shouldBe "plain-text"
                }
            }
    })
