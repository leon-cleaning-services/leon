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

package com.svenjacobs.app.leon.core.domain.sanitizer.change

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ChangeSanitizerTest : WordSpec(
	{

		"invoke" should {

			"remove all parameters from Change petition URL" {
				val sanitizer = ChangeSanitizer()
				val result = sanitizer(
					"https://www.change.org/p/verbot-von-silvesterfeuerwerk-f%C3%BCr-privatpers" +
						"onen-staedtetag-bmuv?utm_content=cl_sharecopy_12878233_de-DE%3Av3&recr" +
						"uiter=44645781&recruited_by_id=29ffed30-7385-0130-ec6e-3c764e044e9e&ut" +
						"m_source=share_petition&utm_medium=copylink&utm_campaign=psf_combo_sha" +
						"re_initial&pt=AVBldGl0aW9uAJmBxABBBBBAAAY7fdDHYupoE1NzU0NzE3Zg%3D%3D",
				)

				result shouldBe "https://www.change.org/p/verbot-von-silvesterfeuerwerk-f%C3%BC" +
					"r-privatpersonen-staedtetag-bmuv"
			}
		}
	},
)
