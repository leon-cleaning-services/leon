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
package com.svenjacobs.app.leon.core.domain.sanitizer

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/** The part of a URL a [Rule] reads a value from. */
sealed interface Source {

    data object Host : Source

    data object Path : Source

    data object Fragment : Source

    /** The value of the first parameter whose name is matched completely by [name]. */
    data class Parameter(val name: String) : Source
}

/**
 * One step of turning a raw value into the URL it hides.
 *
 * Redirect wrappers bury their target behind any number of these — a percent-encoded capture out of
 * a path, or a base64 encoded JSON payload — and the steps are applied in order, each one working
 * on what the previous one produced.
 */
sealed interface Decode {

    /**
     * Finds [pattern] in the value and expands [replacement] from that match, which may reference
     * its groups as `$1`, `$2` …
     *
     * The default replacement is the first group, which is the usual "the target sits in here
     * somewhere" case. A replacement which is more than a group reference builds a URL out of the
     * capture instead, as `youtu.be/<id>` → `www.youtube.com/watch?v=<id>` does.
     */
    data class Capture(val pattern: String, val replacement: String = "$1") : Decode

    /** Percent-decodes the value, as a URL carried inside another URL is encoded. */
    data object PercentDecode : Decode

    /** Base64-decodes the value. */
    data object Base64Decode : Decode

    /** Reads the string field [key] out of a JSON object. */
    data class JsonField(val key: String) : Decode
}

/**
 * Describes what a [Sanitizer] does to a URL it applies to.
 *
 * Pure data, like everything a sanitizer is made of: what a rule *means* is decided by `Cleaner`,
 * which turns it into the changes it would perform. Regular expressions are held as strings, not as
 * [Regex], so that the catalog could be read from a configuration file.
 */
sealed interface Rule {

    /**
     * Removes every parameter whose name is matched completely by [name], or every parameter whose
     * name is *not* matched when [negate] is set.
     */
    data class RemoveParameters(val name: String, val negate: Boolean = false) : Rule

    /**
     * Removes every parameter which has an empty value, such as `?a=`.
     *
     * A parameter without a value at all, such as `?flag`, is kept — it carries meaning by being
     * present.
     */
    data object RemoveEmptyParameters : Rule

    /**
     * Removes the fragment, either unconditionally or only when [pattern] matches it completely.
     */
    data class RemoveFragment(val pattern: String? = null) : Rule

    /**
     * Rewrites the host when [pattern] matches [from] completely.
     *
     * [replacement] may reference groups of [pattern] as `$1`, `$2` … [from] is the host itself
     * unless another part of the URL is named: `open.substack.com/pub/<publication>` puts the host
     * of the article in its *path*, and this is what reads it out of there.
     */
    data class RewriteHost(
        val pattern: String,
        val replacement: String,
        val from: Source = Source.Host,
    ) : Rule

    /**
     * Rewrites the path when [pattern] matches [from] completely.
     *
     * [replacement] may reference groups of [pattern] as `$1`, `$2` … [from] is the path itself
     * unless another part of the URL is named.
     */
    data class RewritePath(
        val pattern: String,
        val replacement: String,
        val from: Source = Source.Path,
    ) : Rule

    /**
     * Replaces the URL with the one hidden in [from], after running it through [steps].
     *
     * This is how every redirect wrapper is expressed, however deeply it buries its target: the `q`
     * parameter of a search engine, the `RU=` capture in a click-tracking path, or a base64 encoded
     * JSON payload. When any step finds nothing, nothing happens.
     *
     * @param dropParameters Discards the query of the resulting URL, for wrappers which append
     *   their own tracking to the target rather than to themselves.
     */
    data class Follow(
        val from: Source,
        val steps: ImmutableList<Decode> = persistentListOf(),
        val dropParameters: Boolean = false,
    ) : Rule
}
