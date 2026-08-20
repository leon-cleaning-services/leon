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
package com.svenjacobs.app.leon.core.common.domain

private val SCHEME = Regex("^https?://")

private val HOST_DELIMITERS = charArrayOf(':', '/', '?', '#')

/**
 * Returns whether this URL is matched by the regular expression [domain].
 *
 * An optional scheme and an optional leading `www.` are matched implicitly, so `example\.com`
 * matches `example.com`, `http://example.com` and `https://www.example.com` alike.
 *
 * [domain] must cover the complete host of the URL. A pattern which merely is a prefix of the host
 * does *not* match, which keeps lookalike hosts such as `example.com.evil.com` or
 * `example.computer` from being treated as `example.com`. Whatever follows the host — a port, a
 * path, a query or a fragment — is not restricted, and [domain] may reach into the path itself, for
 * example `google\.com/maps`.
 *
 * @param domain Regular expression of the domain, without scheme and without leading `www.`
 */
fun String.matchesDomainRegex(domain: String): Boolean {
    val match = Regex("^(?:https?://)?(?:www\\.)?(?:$domain)").find(this) ?: return false
    return match.range.last + 1 >= hostEndIndex()
}

/**
 * Returns whether this URL is hosted at exactly [domain].
 *
 * @param domain Literal domain, without scheme and without leading `www.`
 */
fun String.matchesDomain(domain: String): Boolean = matchesDomainRegex(domain.quoteDots())

/**
 * Returns whether this URL is hosted at [domain] or at any of its subdomains.
 *
 * @param domain Literal domain, without scheme and without leading `www.`
 */
fun String.matchesSubdomains(domain: String): Boolean =
    matchesDomainRegex("(?:[^./?#:]+\\.)*${domain.quoteDots()}")

private fun String.quoteDots(): String = replace(".", "\\.")

/**
 * Returns the index at which the host of this URL ends, which is the first delimiter after an
 * optional scheme, or the length of the string when there is none.
 */
private fun String.hostEndIndex(): Int {
    val start = SCHEME.find(this)?.value?.length ?: 0
    val index = indexOfAny(HOST_DELIMITERS, startIndex = start)
    return if (index == -1) length else index
}
