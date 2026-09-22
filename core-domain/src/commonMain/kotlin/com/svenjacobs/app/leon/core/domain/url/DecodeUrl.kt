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

import kotlinx.collections.immutable.toImmutableList

/**
 * Decodes every percent-escape of [encoded], and `+` as a space, the way a URL query is encoded.
 *
 * This is the decoding a sanitizer performs on a value it is about to interpret — the target of a
 * redirect, which only becomes a URL once its `%3A%2F%2F` has become `://`. To decode a URL for
 * *display*, use [decoded], which keeps the escapes that carry the URL's structure.
 *
 * Written by hand rather than with `java.net.URLDecoder` so that this module stays plain Kotlin. A
 * `%` which is not followed by two hexadecimal digits is kept as it is instead of throwing, because
 * a URL somebody shares is not necessarily well formed and is better returned unchanged than not at
 * all.
 */
fun decodeUrl(encoded: String): String = decode(encoded, reserved = null)

/**
 * The same URL with its percent-escapes decoded as far as that only changes how it reads and not
 * what it addresses — `%C3%BC` becomes `ü`, while an escape which would turn into a delimiter of
 * the component holding it keeps its original spelling.
 *
 * Decoding has to happen per component, because which characters are structural depends on where
 * they sit: a `%2F` in the path would split a path segment in two, while the same escape inside a
 * parameter value is just a slash in that value. Decoding the serialized URL as one string, as this
 * used to, can only apply the strictest of those rules everywhere, which leaves an embedded URL —
 * the payload of every redirect wrapper — displayed entirely unchanged.
 *
 * The host and the user information are left alone: an escape there is exotic enough that decoding
 * it is more likely to change which server is addressed than to help anybody read the URL.
 */
fun Url.decoded(): Url =
    copy(
        path = decode(path, reserved = PATH_RESERVED),
        parameters =
            parameters
                .map { parameter ->
                    Url.Parameter(
                        name = decode(parameter.name, reserved = NAME_RESERVED),
                        value = parameter.value?.let { decode(it, reserved = VALUE_RESERVED) },
                    )
                }
                .toImmutableList(),
        fragment = fragment?.let { decode(it, reserved = FRAGMENT_RESERVED) },
    )

/** A decoded `/`, `?` or `#` would end the path or a segment of it early. */
private const val PATH_RESERVED = "/?#"

/**
 * A decoded `&` or `=` would move the boundaries of the parameter and `#` would end the query. `+`
 * is reserved for the same reason as in a value.
 */
private const val NAME_RESERVED = "&=#+"

/**
 * A decoded `&` would split the value into another parameter and `#` would turn its remainder into
 * the fragment. `+` is reserved because it reads as a space, so `%2B` must not become one. An `=`
 * is safe: only the first one separates name from value.
 */
private const val VALUE_RESERVED = "&#+"

/** Only the `#` which already introduced the fragment delimits it. */
private const val FRAGMENT_RESERVED = "#"

/**
 * Decodes [encoded], keeping the escape of every character in [reserved] — plus `%` and everything
 * below `0x21`, which no component may hold literally — in its original spelling. A `null`
 * [reserved] decodes everything, and is the only mode in which `+` becomes a space.
 */
private fun decode(encoded: String, reserved: String?): String {
    if ('%' !in encoded && '+' !in encoded) return encoded

    val bytes = ArrayList<Byte>(encoded.length)
    var i = 0

    while (i < encoded.length) {
        when (val char = encoded[i]) {
            '+' -> {
                bytes += if (reserved == null) ' '.code.toByte() else '+'.code.toByte()
                i++
            }
            '%' -> {
                val byte = encoded.hexByteAt(i + 1)
                if (byte == null) {
                    bytes += char.code.toByte()
                    i++
                } else if (reserved != null && byte.isReservedIn(reserved)) {
                    // Original spelling, not the decoded byte, so `%2f` does not become `%2F`.
                    encoded.substring(i, i + 3).encodeToByteArray().forEach { bytes += it }
                    i += 3
                } else {
                    bytes += byte
                    i += 3
                }
            }
            else -> {
                char.toString().encodeToByteArray().forEach { bytes += it }
                i++
            }
        }
    }

    return bytes.toByteArray().decodeToString()
}

/** The byte written as two hexadecimal digits at [index], or `null` when there are none. */
private fun String.hexByteAt(index: Int): Byte? {
    if (index + 1 >= length) return null
    val high = this[index].digitToIntOrNull(radix = 16) ?: return null
    val low = this[index + 1].digitToIntOrNull(radix = 16) ?: return null
    return ((high shl 4) or low).toByte()
}

/**
 * Whether decoding this byte would change what the URL addresses rather than just how it reads: a
 * space or control character cannot appear in a URL at all, a `%` would start an escape of its own,
 * and [reserved] names the delimiters of the component this byte sits in.
 */
private fun Byte.isReservedIn(reserved: String): Boolean {
    val code = toInt() and 0xFF
    return code < 0x21 || code == '%'.code || code.toChar() in reserved
}
