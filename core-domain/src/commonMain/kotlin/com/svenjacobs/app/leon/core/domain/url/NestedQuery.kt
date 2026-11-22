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
package com.svenjacobs.app.leon.core.domain.url

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * A query string which is itself the value of a [Url.Parameter], such as the `referrer` of
 * `?id=app&referrer=utm_source=x%26utm_medium=y`.
 *
 * It is read from the *raw* value, which is still percent-encoded: `%26` separates its parameters
 * and `=` or `%3D` separates a name from its value. Every segment keeps the spelling it had, so
 * that [without] changes nothing but the parameter it removes.
 *
 * @param segments The raw parameters, in order.
 * @param separators The raw separators between [segments], so there is one fewer of them.
 */
internal class NestedQuery
private constructor(
    private val segments: ImmutableList<String>,
    private val separators: ImmutableList<String>,
) {

    /** The parameters of this query, split on the first `=` or `%3D` of each segment. */
    val parameters: List<Url.Parameter> = segments.map(::parseSegment)

    /** Whether [parameter] is one of [parameters]. */
    operator fun contains(parameter: Url.Parameter) = parameter in parameters

    /**
     * The raw value without the first segment equal to [parameter], and without the separator which
     * joined it to its neighbour. The remaining segments and separators are kept as they were, and
     * the value is returned unchanged when [parameter] is not part of this query.
     */
    fun without(parameter: Url.Parameter): String {
        val index = parameters.indexOf(parameter)
        if (index == -1) return rebuild(segments, separators)

        val remainingSegments = segments.filterIndexed { i, _ -> i != index }
        // The separator after the removed segment goes with it, or the one before it for the last.
        val dropped = if (index < separators.size) index else index - 1
        val remainingSeparators = separators.filterIndexed { i, _ -> i != dropped }
        return rebuild(remainingSegments, remainingSeparators)
    }

    private fun rebuild(segments: List<String>, separators: List<String>) = buildString {
        segments.forEachIndexed { i, segment ->
            if (i > 0) append(separators[i - 1])
            append(segment)
        }
    }

    companion object {

        private val SEPARATOR = Regex("%26", RegexOption.IGNORE_CASE)
        private val ASSIGNMENT = Regex("=|%3D", RegexOption.IGNORE_CASE)
        private val EMBEDDED_URL = Regex("(?::|%3A)(?:/|%2F){2}", RegexOption.IGNORE_CASE)

        /**
         * Reads [raw] as a nested query, or returns `null` when it is none: when no segment has a
         * name and a value, when a segment has no name, or when it holds a whole URL, which is the
         * business of a `Rule.Follow` and not a list of parameters.
         */
        fun parse(raw: String): NestedQuery? {
            if (!ASSIGNMENT.containsMatchIn(raw) || EMBEDDED_URL.containsMatchIn(raw)) return null

            val segments = raw.split(SEPARATOR)
            if (segments.any { ASSIGNMENT.find(it)?.range?.first == 0 || it.isEmpty() }) return null

            return NestedQuery(
                segments = segments.toImmutableList(),
                separators = SEPARATOR.findAll(raw).map { it.value }.toList().toImmutableList(),
            )
        }

        private fun parseSegment(segment: String): Url.Parameter {
            val assignment =
                ASSIGNMENT.find(segment) ?: return Url.Parameter(name = segment, value = null)
            return Url.Parameter(
                name = segment.take(assignment.range.first),
                value = segment.substring(assignment.range.last + 1),
            )
        }
    }
}

/**
 * The query nested in the value of this parameter, or `null` when its value is not one, see
 * [NestedQuery.parse].
 */
internal fun Url.Parameter.nestedQuery(): NestedQuery? = value?.let(NestedQuery::parse)
