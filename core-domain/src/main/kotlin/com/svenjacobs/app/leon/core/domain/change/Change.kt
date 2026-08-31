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
package com.svenjacobs.app.leon.core.domain.change

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.core.domain.url.Url
import kotlinx.collections.immutable.toImmutableList

/**
 * A single modification of a URL which a sanitizer proposes.
 *
 * Sanitizers describe what they would do instead of doing it, so that the user interface can list
 * every proposed change and the user can decline it. A change is its own identity: cleaning the
 * same URL again produces equal changes, so a declined change can be recognized on the next run.
 *
 * @param sanitizerId The sanitizer which proposed this change, or `null` when the user asked for it
 *   themselves in the user interface.
 */
data class Change(val sanitizerId: SanitizerId?, val action: Action) {

    sealed interface Action {

        /** Removes the first remaining parameter equal to [parameter]. */
        data class RemoveParameter(val parameter: Url.Parameter) : Action

        /**
         * Sets [component] to [to].
         *
         * [from] is not used when applying the change; it is carried so that the user interface can
         * show what the component looked like before.
         */
        data class SetComponent(val component: Component, val from: String?, val to: String?) :
            Action

        /** Replaces the complete URL, for example when following a redirect. */
        data class Replace(val from: Url, val to: Url) : Action
    }

    enum class Component {
        HOST,
        PATH,
        FRAGMENT,
    }
}

/** Applies [actions] in order and returns the resulting URL. */
fun Url.apply(actions: List<Change.Action>): Url =
    actions.fold(this) { url, action ->
        when (action) {
            is Change.Action.RemoveParameter -> url.removeParameter(action.parameter)
            is Change.Action.Replace -> action.to
            is Change.Action.SetComponent ->
                when (action.component) {
                    Change.Component.HOST -> url.copy(host = action.to.orEmpty())
                    Change.Component.PATH -> url.copy(path = action.to.orEmpty())
                    Change.Component.FRAGMENT -> url.copy(fragment = action.to)
                }
        }
    }

/**
 * Removes the first parameter equal to [parameter], so that a URL carrying the same parameter twice
 * needs two changes to lose both of them.
 */
private fun Url.removeParameter(parameter: Url.Parameter): Url {
    val index = parameters.indexOf(parameter)
    if (index == -1) return this
    return copy(parameters = parameters.filterIndexed { i, _ -> i != index }.toImmutableList())
}
