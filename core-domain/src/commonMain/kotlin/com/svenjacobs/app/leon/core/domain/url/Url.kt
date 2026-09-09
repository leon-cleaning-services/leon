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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * The parts of an HTTP(S) URL, as they appear in the URL.
 *
 * Nothing is normalized: percent-encoding, the case of every component and the order of the
 * parameters are preserved verbatim, so that [toString] returns the input of [parse] unchanged.
 * `java.net.URI` cannot be used for this — it normalizes, and it rejects URLs which Léon has to
 * handle.
 *
 * Three inputs do not survive the round trip, each of them producing a URL that is equivalent to
 * the input: a query section which is present but empty (`https://example.com/?`), empty parameters
 * (`?a=1&&b=2`) and an HTML escaped separator (`?a=1&amp;b=2`). `Cleaner` avoids even those by
 * emitting the original text whenever cleaning did not change anything.
 */
data class Url(
    val scheme: String,
    val host: String,
    val userInfo: String? = null,
    val port: Int? = null,
    val path: String = "",
    val parameters: ImmutableList<Parameter> = persistentListOf(),
    val fragment: String? = null,
) {

    /** A single query parameter. [value] is `null` for a parameter without `=`, such as `?flag`. */
    data class Parameter(val name: String, val value: String?) {

        override fun toString() = if (value == null) name else "$name=$value"
    }

    override fun toString() = buildString {
        append(scheme)
        append("://")
        if (userInfo != null) {
            append(userInfo)
            append('@')
        }
        append(host)
        if (port != null) {
            append(':')
            append(port)
        }
        append(path)
        if (parameters.isNotEmpty()) {
            append('?')
            parameters.joinTo(this, separator = "&")
        }
        if (fragment != null) {
            append('#')
            append(fragment)
        }
    }

    companion object {

        /**
         * Parses [input] into its parts, or returns `null` when it is not an HTTP(S) URL with a
         * host.
         */
        fun parse(input: String): Url? {
            val groups = URL_REGEX.matchEntire(input)?.groups ?: return null
            val authority = parseAuthority(groups[2]!!.value) ?: return null

            return Url(
                scheme = groups[1]!!.value,
                host = authority.host,
                userInfo = authority.userInfo,
                port = authority.port,
                path = groups[3]!!.value,
                parameters = parseParameters(groups[4]?.value.orEmpty()),
                fragment = groups[5]?.value,
            )
        }

        private data class Authority(val host: String, val userInfo: String?, val port: Int?)

        /**
         * Splits `user:password@host:port` into its parts and returns `null` when the host is empty
         * or the port is not a number.
         *
         * The user information has to be separated out so that [host] is the host the URL actually
         * points to: `https://example.com@evil.com` is a request to `evil.com`.
         */
        private fun parseAuthority(authority: String): Authority? {
            val at = authority.lastIndexOf('@')
            val userInfo = if (at == -1) null else authority.take(at)
            val hostAndPort = authority.substring(at + 1)

            val colon = hostAndPort.indexOf(':', startIndex = hostAndPort.lastIndexOf(']') + 1)
            val host = (if (colon == -1) hostAndPort else hostAndPort.take(colon)).ifEmpty { null }
            if (host == null) return null

            val port =
                if (colon == -1) {
                    null
                } else {
                    hostAndPort.substring(colon + 1).toIntOrNull() ?: return null
                }

            return Authority(host = host, userInfo = userInfo, port = port)
        }

        private fun parseParameters(query: String): ImmutableList<Parameter> =
            query
                .split('&')
                .filter { it.isNotEmpty() }
                .map { parameter ->
                    // A URL which was copied out of HTML carries "&amp;" as its separator, which
                    // leaves the entity glued to the front of the following parameter name.
                    val cleaned = parameter.removePrefix("amp;")
                    val index = cleaned.indexOf('=')
                    if (index == -1) {
                        Parameter(name = cleaned, value = null)
                    } else {
                        Parameter(name = cleaned.take(index), value = cleaned.substring(index + 1))
                    }
                }
                .toImmutableList()

        /** Captures scheme, authority, path, query and fragment. */
        private val URL_REGEX =
            Regex("(https?)://([^/?#]*)([^?#]*)(?:\\?([^#]*))?(?:#(.*))?", RegexOption.IGNORE_CASE)
    }
}
