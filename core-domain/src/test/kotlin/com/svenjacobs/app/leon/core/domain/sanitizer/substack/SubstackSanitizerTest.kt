/*
 * Léon - The URL Cleaner
 * Copyright (C) 2025 Sven Jacobs
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

package com.svenjacobs.app.leon.core.domain.sanitizer.substack

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SubstackSanitizerTest :
	WordSpec(
		{
			val sanitizer = SubstackSanitizer()

			"invoke" should {

				"remove parameters from substack.com" {
					sanitizer(
						"https://substack.com/@sebastianbarros/note/c-190523061?r=c0obe&utm_source=notes-share-action&utm_medium=web",
					) shouldBe "https://substack.com/@sebastianbarros/note/c-190523061"
				}

				"remove parameters from open.substack.com" {
					sanitizer(
						"https://open.substack.com/pub/fosspost/p/open-up-your-android-smartphone?utm_campaign=post-expanded-share&utm_medium=web",
					) shouldBe "https://open.substack.com/pub/fosspost/p/open-up-your-android-smartphone"
				}
			}

			"matchesDomain" should {

				"match substack.com" {
					sanitizer.matchesDomain("https://substack.com") shouldBe true
				}

				"match open.substack.com" {
					sanitizer.matchesDomain("https://open.substack.com") shouldBe true
				}
			}
		},
	)
