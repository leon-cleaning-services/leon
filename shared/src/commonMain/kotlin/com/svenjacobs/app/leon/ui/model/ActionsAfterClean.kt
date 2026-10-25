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

import androidx.compose.runtime.Immutable
import com.svenjacobs.app.leon.core.domain.action.ActionAfterClean

/**
 * The action after clean for each [InputSource].
 *
 * Unless [perSource] is set, [otherApp] applies to every source and the other values are ignored.
 */
@Immutable
data class ActionsAfterClean(
    val otherApp: ActionAfterClean = ActionAfterClean.DoNothing,
    val browser: ActionAfterClean = ActionAfterClean.DoNothing,
    val inApp: ActionAfterClean = ActionAfterClean.DoNothing,
    val perSource: Boolean = false,
) {

    fun forSource(source: InputSource): ActionAfterClean =
        if (!perSource) {
            otherApp
        } else {
            when (source) {
                InputSource.OtherApp -> otherApp
                InputSource.Browser -> browser
                InputSource.InApp -> inApp
            }
        }
}
