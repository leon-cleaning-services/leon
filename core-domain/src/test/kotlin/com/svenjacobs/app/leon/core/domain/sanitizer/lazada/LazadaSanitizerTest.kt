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
package com.svenjacobs.app.leon.core.domain.sanitizer.lazada

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class LazadaSanitizerTest :
    WordSpec({
        val sanitizer = LazadaSanitizer()

        "invoke" should
            {
                "remove all parameters" {
                    val result =
                        sanitizer(
                            "https://www.lazada.com.my/products/100-cotton-jogger-6-poket-kain-teba" +
                                "l-cargo-pant-multi-pocket-seluar-kerja-i3103291614-s15547124539.html?dso" +
                                "urce=share&laz_share_info=9532664_100_100_5449387_9533664_null&laz_token" +
                                "=d994010eeff70e9df2d3f465d5b0e727&exlaz=e_RtJqWofx9nLGip8qo24MCTJQcHdSbi" +
                                "l0861t6ae%2Fu4vCXvTKdNAtBWNri5zqcNlYb9yEAKGsy7Oyw%2BVOW%2F%2FthI73bSZ6Yk" +
                                "VRD3WnASIas1Y%3D&sub_aff_id=social_share&sub_id2=9532664&sub_id3=5449387" +
                                "&sub_id6=CPI_EXLAZ"
                        )

                    result shouldBe
                        "https://www.lazada.com.my/products/100-cotton-jogger-6-poket-kai" +
                            "n-tebal-cargo-pant-multi-pocket-seluar-kerja-i3103291614-s15547124539.html"
                }
            }

        "matchesDomain" should
            {
                "match for lazada.com.my" {
                    sanitizer.matchesDomain("https://www.lazada.com.my") shouldBe true
                }
            }
    })
