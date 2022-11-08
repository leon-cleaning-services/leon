/*
 * Léon - The URL Cleaner
 * Copyright (C) 2022 Sven Jacobs
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

package com.svenjacobs.app.leon.core.domain.sanitizer.ebay

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class EbaySanitizerTest : WordSpec(
	{

		"invoke" should {

			"remove all parameters from eBay article URL" {
				val sanitizer = EbaySanitizer()
				val result = sanitizer(
					"https://www.ebay.de/itm/271784973135?mkcid=16&mkevt=1&mkrid=707-127654" +
						"-2357-0&ssspo=rMbbkKXARCW&sssrc=2348624&ssuid=Bw-3_LUXSsm&widget_ver=art" +
						"emis&media=MORE",
				)

				result shouldBe "https://www.ebay.de/itm/271784973135"
			}
		}
	},
)
