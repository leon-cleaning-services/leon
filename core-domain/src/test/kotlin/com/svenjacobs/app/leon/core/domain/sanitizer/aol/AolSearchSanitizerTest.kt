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

package com.svenjacobs.app.leon.core.domain.sanitizer.aol

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class AolSearchSanitizerTest :
    WordSpec(
        {

            "invoke" should {

                "extract URL from AOL search link" {
                    val sanitizer = AolSearchSanitizer()

                    val result = sanitizer(
                        "https://search.aol.com/click/_ylt=A0geK.HAoltiwykAlAR8CWVH;_ylu=Y29sbw" +
                            "NiZjEEcG9zAzQEdnRpZAMEc2VjA3Ny/RV=2/RE=1650201408/RO=10/RU=https%3a%2f%2" +
                            "fgithub.com%2fsvenjacobs%2fleon/RK=0/RS=cXRWej4shdsEIIDm147.G4CRZEo-",
                    )

                    result shouldBe "https://github.com/svenjacobs/leon"
                }
            }
        },
    )
