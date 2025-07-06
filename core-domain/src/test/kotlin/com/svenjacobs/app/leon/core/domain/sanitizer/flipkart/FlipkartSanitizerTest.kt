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

package com.svenjacobs.app.leon.core.domain.sanitizer.flipkart

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class FlipkartSanitizerTest :
	WordSpec(
		{
			val sanitizer = FlipkartSanitizer()

			"invoke" should {

				"remove Flipkart parameters" {

					val result = sanitizer(
						"https://www.flipkart.com/msi-gf63-thin-core-i5-10th-gen-8-gb-512-gb-ss" +
							"d-windows-10-home-4-graphics-nvidia-geforce-rtx-3050-144-hz-10uc-607in-g" +
							"aming-laptop/p/itm92565651dc3ed?pid=COMG5AWT9CKRMEFU&lid=LSTCOMG5AWT9CKR" +
							"MEFULV38QB&marketplace=FLIPKART&q=gaming+laptop&store=6bo%2Fb5g&srno=s_1" +
							"_15&otracker=AS_QueryStore_OrganicAutoSuggest_1_9_na_na_na&otracker1=AS_" +
							"QueryStore_OrganicAutoSuggest_1_9_na_na_na&fm=SEARCH&iid=22ff2dee-d44e-4" +
							"0e3-9811-5d229fa974fc.COMG5AWT9CKRMEFU.SEARCH&ppt=hp&ppn=homepage&ssid=z" +
							"v1f42w4bs3azitc1635436210945&qH=da5ee6f53d84b3c2",
					)

					result shouldBe "https://www.flipkart.com/msi-gf63-thin-core-i5-10th-gen-8-gb-512" +
						"-gb-ssd-windows-10-home-4-graphics-nvidia-geforce-rtx-3050-144-hz-10uc-607in" +
						"-gaming-laptop/p/itm92565651dc3ed"
				}
			}

			"matchesDomain" should {

				"match *.flipkart.com" {
					sanitizer.matchesDomain("https://aa.flipkart.com") shouldBe true
				}
			}
		},
	)
