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
package com.svenjacobs.app.leon.core.domain.sanitizer.ilmessaggero

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class IlMessaggeroSanitizerTest :
    WordSpec({
        val sanitizer = IlMessaggeroSanitizer()

        "invoke" should
            {
                "remove all parameters except topic" {
                    sanitizer(
                        "https://archivio.ilmessaggero.it/articolo?topic=politica&utm_source=facebook&utm_medium=social"
                    ) shouldBe "https://archivio.ilmessaggero.it/articolo?topic=politica"
                }

                "remove all parameters when topic is absent" {
                    sanitizer(
                        "https://archivio.ilmessaggero.it/articolo?utm_source=facebook&utm_medium=social"
                    ) shouldBe "https://archivio.ilmessaggero.it/articolo"
                }
            }

        "matchesDomain" should
            {
                "match archivio.ilmessaggero.it" {
                    sanitizer.matchesDomain("https://archivio.ilmessaggero.it") shouldBe true
                }

                "not match ilmessaggero.it" {
                    sanitizer.matchesDomain("https://ilmessaggero.it") shouldBe false
                }

                "not match other.com" {
                    sanitizer.matchesDomain("https://other.com") shouldBe false
                }
            }
    })
