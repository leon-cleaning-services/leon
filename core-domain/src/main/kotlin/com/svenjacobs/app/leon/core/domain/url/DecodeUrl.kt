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

/**
 * Decodes the percent-encoding of [encoded], and `+` as a space, the way a URL query is encoded.
 *
 * Written by hand rather than with `java.net.URLDecoder` so that this module stays plain Kotlin. A
 * `%` which is not followed by two hexadecimal digits is kept as it is instead of throwing, because
 * a URL somebody shares is not necessarily well formed and is better returned unchanged than not at
 * all.
 */
fun decodeUrl(encoded: String): String {
    if ('%' !in encoded && '+' !in encoded) return encoded

    val bytes = ArrayList<Byte>(encoded.length)
    var i = 0

    while (i < encoded.length) {
        when (val char = encoded[i]) {
            '+' -> {
                bytes += ' '.code.toByte()
                i++
            }
            '%' -> {
                val byte = encoded.hexByteAt(i + 1)
                if (byte == null) {
                    bytes += char.code.toByte()
                    i++
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
