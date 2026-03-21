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
package com.svenjacobs.app.leon.core.domain.sanitizer.x

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class XSanitizerTest :
    WordSpec({
        val sanitizer = XSanitizer()

        "invoke" should
            {
                "remove all parameters (Twitter)" {
                    var result =
                        sanitizer(
                            "https://twitter.com/AndroidDev/status/1453763770334027781?t=QEv2BUR2LOumjgK18S72bA&s=09"
                        )

                    result shouldBe "https://twitter.com/AndroidDev/status/1453763770334027781"

                    result =
                        sanitizer(
                            "https://twitter.com/fantasm_finance/status/1501569232881995785?ref" +
                                "_src=twsrc%5Etfw%7Ctwcamp%5Etweetembed%7Ctwterm%5E150156923288199578" +
                                "5%7Ctwgr%5E%7Ctwcon%5Es1_&ref_url=https%3A%2F%2Fwww.coindesk.com%2Ft" +
                                "ech%2F2022%2F03%2F10%2Ffantom-based-algo-protocol-fantasm-exploited-" +
                                "for-26m%2F"
                        )

                    result shouldBe "https://twitter.com/fantasm_finance/status/1501569232881995785"
                }

                "remove all parameters (X.com)" {
                    var result =
                        sanitizer(
                            "https://x.com/AndroidDev/status/1453763770334027781?t=QEv2BUR2LOumjgK18S72bA&s=09"
                        )

                    result shouldBe "https://x.com/AndroidDev/status/1453763770334027781"

                    result =
                        sanitizer(
                            "https://x.com/fantasm_finance/status/1501569232881995785?ref" +
                                "_src=twsrc%5Etfw%7Ctwcamp%5Etweetembed%7Ctwterm%5E150156923288199578" +
                                "5%7Ctwgr%5E%7Ctwcon%5Es1_&ref_url=https%3A%2F%2Fwww.coindesk.com%2Ft" +
                                "ech%2F2022%2F03%2F10%2Ffantom-based-algo-protocol-fantasm-exploited-" +
                                "for-26m%2F"
                        )

                    result shouldBe "https://x.com/fantasm_finance/status/1501569232881995785"
                }
            }

        "matchesDomain" should
            {
                "match twitter.com" { sanitizer.matchesDomain("https://twitter.com") shouldBe true }

                "match x.com" { sanitizer.matchesDomain("https://x.com") shouldBe true }
            }
    })
