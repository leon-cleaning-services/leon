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

package com.svenjacobs.app.leon.core.domain.sanitizer.youtube

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class YoutubeSanitizerTest :
	WordSpec(
		{
			val sanitizer = YoutubeSanitizer()

			"invoke" should {

				"remove all parameters except v= from video URLs" {
					sanitizer(
						"https://m.youtube.com/watch?v=CvFH_6DNRCY&pp=ygUHZGVidXNzeQ%3D%3D",
					) shouldBe "https://m.youtube.com/watch?v=CvFH_6DNRCY"
				}

				"remove all parameters except search_query= from search URLs" {
					sanitizer("https://m.youtube.com/results?sp=mAEA&search_query=funny+dog+video") shouldBe
						"https://m.youtube.com/results&search_query=funny+dog+video"
				}
			}

			"matchesDomain" should {

				"match youtube.com domain" {
					sanitizer.matchesDomain("https://youtube.com/") shouldBe true
				}

				"match m.youtube.com domain" {
					sanitizer.matchesDomain("https://m.youtube.com/") shouldBe true
				}
			}
		},
	)
