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

package com.svenjacobs.app.leon.core.domain.sanitizer.kogan

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class KoganSanitizerTest :
	WordSpec({
		val sanitizer = KoganSanitizer()

		"invoke" should {
			"remove all parameters from kogan.com URL" {
				val result =
					sanitizer(
						"https://www.kogan.com/au/buy/heybattery-samsung-galaxy-watch-7-44mm-bluetooth-l310-silver-ss-l310-sv/?ssid=201.bae7632c-5c90-46cf-a071-416f0879ecb5&click_id=VWDA74xN4CIffN9Rb_8l-46p",
					)
				result shouldBe
					"https://www.kogan.com/au/buy/heybattery-samsung-galaxy-watch-7-44mm-bluetooth-l310-silver-ss-l310-sv/"
			}
		}

		"matchesDomain" should {
			"match for kogan.com" {
				sanitizer.matchesDomain("https://www.kogan.com/au/buy/kogan-4k-led-tv/") shouldBe true
			}
			"not match for other domains" {
				sanitizer.matchesDomain("https://www.example.com/product/123") shouldBe false
			}
		}
	})
