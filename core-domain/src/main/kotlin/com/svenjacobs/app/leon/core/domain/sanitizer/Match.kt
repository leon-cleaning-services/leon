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

/**
 * Describes the URLs a [Sanitizer] applies to.
 *
 * @param host Which host the URL has to be served from.
 * @param pathPrefix When given, the path of the URL additionally has to start with it. Matched
 *   case-sensitively, since paths are.
 */
data class Match(val host: HostMatch = HostMatch.Any, val pathPrefix: String? = null)

/**
 * Describes the host a [Match] applies to.
 *
 * Every variant matches the **complete** host, so a lookalike host such as `example.com.evil.com`
 * cannot pass as `example.com`. A leading `www.` of the URL is ignored throughout, so
 * `Domain("example.com")` matches `www.example.com` as well.
 *
 * Pure data: the matching itself is done by `Cleaner`. [Pattern] holds its regular expression as a
 * string rather than as a [Regex] so that the catalog could be read from a configuration file.
 */
sealed interface HostMatch {

    /** Matches every host. */
    data object Any : HostMatch

    /** Matches exactly [name], but not any of its subdomains. */
    data class Domain(val name: String) : HostMatch

    /** Matches [name] itself and every subdomain of it, however deeply nested. */
    data class Subdomains(val name: String) : HostMatch

    /** Matches hosts for which [regex] matches the complete host. */
    data class Pattern(val regex: String) : HostMatch
}
