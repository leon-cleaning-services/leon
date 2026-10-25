/*
 * Léon - The URL Cleaner
 * Copyright (C) 2026 Sven Jacobs
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
package com.svenjacobs.app.leon.ui.model

import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ActionsAfterCleanTest :
    WordSpec(
        {
            val actions =
                ActionsAfterClean(
                    otherApp = ActionAfterClean.OpenShareMenu,
                    browser = ActionAfterClean.OpenUrl,
                    inApp = ActionAfterClean.CopyToClipboard,
                )

            "forSource" should
                {
                    "return the other app action for every source unless per source" {
                        InputSource.entries.forEach {
                            actions.forSource(it) shouldBe ActionAfterClean.OpenShareMenu
                        }
                    }

                    "return the matching action per source" {
                        val perSource = actions.copy(perSource = true)

                        perSource.forSource(InputSource.OtherApp) shouldBe
                            ActionAfterClean.OpenShareMenu
                        perSource.forSource(InputSource.Browser) shouldBe ActionAfterClean.OpenUrl
                        perSource.forSource(InputSource.InApp) shouldBe
                            ActionAfterClean.CopyToClipboard
                    }
                }
        },
    )
