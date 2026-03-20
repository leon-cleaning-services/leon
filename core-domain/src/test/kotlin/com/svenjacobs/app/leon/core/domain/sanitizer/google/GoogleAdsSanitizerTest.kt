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

package com.svenjacobs.app.leon.core.domain.sanitizer.google

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class GoogleAdsSanitizerTest :
    WordSpec(
        {

            val sanitizer = GoogleAdsSanitizer()

            "invoke" should {

                "extract URL from Google Ads link" {
                    val result = sanitizer(
                        "https://www.googleadservices.com/pagead/aclk?sa=L&ai=Ccd5fYmNxY_3JN4TQ" +
                            "o9kPwIqcyA37ibepbZuL5YvKELCQHxADIABgyY6xiZCk6A-CARdjYS1wdWItNjIxOTgxMTc0" +
                            "NzA0OTM3MaAB8OyYgSmpAt5OXuGRdag-qAMEyAMKqgS-AU_QsgWpC3X4b7LSH1pMKrb4yz70" +
                            "elntS3shophaRc0GA0363USvwfydKTGvg7sJgBeegkd_1uzEg99EtDgAbkpjEvmFZNcCJeAR" +
                            "A0iilQtb2pRgRapZIYuJVDtZJib1XVMlPnV5NyZqXeQe5uUQul1xczG2sCJcO1U7qtgHAgyH" +
                            "A5N_UZh9taO1_6Fxvs5Yrb1Y7aphw3MM1AJHp5xe1Nb-xlKSgYOtY73BYFW0GthsZKj3sYYa" +
                            "VAcaNKdGNQGSBQoIE2gBeMqzyIcEoAZu2AYCgAfwpOngA4gHAZAHApgHAqgHhAioB6jSG6gH" +
                            "tgeoB-DPG6gH6dQbqAeMzRuoB7HcG6gHpJqxAqgHkZ-xAqgHsJuxAqgH36GxAqgHgcYbqAer" +
                            "xRuoB-adsQKoB8ifsQKoB7ehsQKoB9WpsQLSCBgIhMCAQBACGAAyBIHCgA46B8qAgICAgQWx" +
                            "CdouDFc10x_4yAkAmAsBugtBCAIQBRgEIAgoATADQANIAFABWAdgAGgAcAGIAQCYAQGiARUK" +
                            "AggBKAGAAQHQAQGQAgKoAgXAAgLYAQGAAgGIAgXQCxK4DAGaDQESuBP___________8BsBQC" +
                            "wBWBgIBA0BUB2BUB4hYCCAGAFwGKFwoIAxgBKAEwATgBoBcBqRex-7nVVf8wwg&num=3&cid" +
                            "=CAESD-D2sxZZsSg9jandUiDpjA&sig=AOD64_2Kzq5JFcf_khsfs5olxZcNXN75iQ&adurl" +
                            "=https://www.evedyth.shop/&ms=CoACWubneRVfv34M04DyUIeLpuvkwlUzfXCpaunrav" +
                            "-Ai4gNVenCJflFdHYFDDR6LR7QwvKsfwOo9373FlWLUelELxRgenTytMogqTCU3Dp32taq7l" +
                            "tdunYB7MP8RcmJyHNKBHG1QNnEZpcSCdxaH4Mf8rPELdcNFeSsgR0tKY2Yfhc-fM90aG22GT" +
                            "ggyGMKgNXzziGFzmPmtPSDSiVbadhPHXoQTUB1U5NUQaR-CIV8816yqV2b_VOH4h0QZDWyab" +
                            "XhcrrfCpIQNLmEy8g39-YGcMlyiQBovndagTPNMGzoHbO6Yotf7AQeCUBgvIq9SGg-uBXmrg" +
                            "bo1UWe1t2v32dxSRIQ7MyqdMdGbezFYod08gtmhg&nb=8&nx=334&ny=15&dim=360x36",
                    )

                    result shouldBe "https://www.evedyth.shop/"
                }
            }
        },
    )
