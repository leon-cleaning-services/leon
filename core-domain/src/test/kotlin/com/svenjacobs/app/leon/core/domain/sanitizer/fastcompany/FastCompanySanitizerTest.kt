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

package com.svenjacobs.app.leon.core.domain.sanitizer.fastcompany

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class FastCompanySanitizerTest :
    WordSpec(
        {
            val sanitizer = FastCompanySanitizer()

            "invoke" should {

                "clean fastcompany.com URLs" {
                    sanitizer(
                        "https://www.fastcompany.com/90890683/bronx-dream-school-david-adjaye?u" +
                            "tm_source= newsletters&utm_medium=email&utm_campaign =FC%20%20Top%2010%" +
                            "20Newsletter. Newsletter %20-%20FC%20-%20Top%2010%205-7-23&leadld=77345" +
                            "0&mkt_tok=NjEWLUXFRS04NZIAAAGLIUIVk0W4gRAxyR6Nmx3ZSH_IJS1KWrpFwGORBYZXQ" +
                            "3cnEc99TZ-1QVnrwnx87XLC_QdRSecUnOk0QLPnfUXF2vr9yAQuN7g|OjfbvtY",
                    ) shouldBe
                        "https://www.fastcompany.com/90890683/bronx-dream-school-david-adjaye"
                }
            }

            "matchesDomain" should {

                "match fastcompany.com" {
                    sanitizer.matchesDomain("https://fastcompany.com") shouldBe true
                }
            }
        },
    )
