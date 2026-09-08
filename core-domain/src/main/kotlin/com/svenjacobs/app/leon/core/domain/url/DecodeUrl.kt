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
 *
 * @param keepStructure When `true`, an escape whose decoded byte is a delimiter the URL syntax
 *   relies on — `/ ? # & = : + %`, space, or any other byte below `0x21` — is left in its original
 *   spelling (e.g. `%2f` stays `%2f`, not `/`) instead of being decoded, and a literal `+` stays a
 *   literal `+` instead of becoming a space. Decoding those would change what the URL addresses
 *   (`%2F` inside a path segment turning into a `/` that splits it) rather than merely how it is
 *   displayed, which is what this is for: showing a human-readable URL (`%C3%BC` → `ü`) without
 *   rewriting its structure.
 */
fun decodeUrl(encoded: String, keepStructure: Boolean = false): String {
    if ('%' !in encoded && '+' !in encoded) return encoded

    val bytes = ArrayList<Byte>(encoded.length)
    var i = 0

    while (i < encoded.length) {
        when (val char = encoded[i]) {
            '+' -> {
                bytes += if (keepStructure) '+'.code.toByte() else ' '.code.toByte()
                i++
            }
            '%' -> {
                val byte = encoded.hexByteAt(i + 1)
                if (byte == null) {
                    bytes += char.code.toByte()
                    i++
                } else if (keepStructure && byte.isStructural()) {
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

/** The ASCII delimiters and control characters a URL's syntax depends on. */
private val STRUCTURAL_BYTES = "/?#&=:+%".map { it.code.toByte() }.toSet()

/** Whether decoding this byte would change what a URL addresses rather than just how it reads. */
private fun Byte.isStructural(): Boolean = (toInt() and 0xFF) < 0x21 || this in STRUCTURAL_BYTES
