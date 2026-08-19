/*
 * Léon - The URL Cleaner
 * Copyright (C) 2025 Sven Jacobs
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
package com.svenjacobs.app.leon.core.domain.sanitizer.substack

import android.content.Context
import com.svenjacobs.app.leon.core.domain.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

/**
 * Reduces Substack URLs to the reference of the article (or note, comment …) they point to.
 *
 * Substack appends a lot of tracking to links inside newsletter emails, most notably `r` (the
 * reader who received the mail) and `token` (a JWT identifying that reader), next to the usual
 * `utm_*` campaign parameters. Removing all parameters is not enough though:
 * - `substack.com/app-link/…` URLs, which are used for the links inside newsletter mails, carry the
 *   article reference in the `publication_id` and `post_id` parameters. Dropping all parameters
 *   leaves a URL that no longer points to any article at all, so those two are kept.
 * - `open.substack.com/pub/<publication>/p/<slug>` URLs are an indirection for opening the article
 *   in the Substack app. They are rewritten to the canonical `<publication>.substack.com/p/<slug>`.
 * - `substack.com/redirect/<uuid>?j=<blob>` URLs are left untouched. It is not publicly documented
 *   what the `j` blob carries, and removing it would leave a dead link.
 *
 * For all other Substack URLs the path already is the article reference, so only the parameters are
 * removed. The fragment is always kept since it references a section or comment of the article.
 */
class SubstackSanitizer : Sanitizer {

    override val id = SanitizerId("substack")

    override fun getMetadata(context: Context) =
        Sanitizer.Metadata(name = context.getString(R.string.sanitizer_substack))

    override fun matchesDomain(input: String) = URL_REGEX.matches(input)

    override fun invoke(input: String): String {
        val groupValues = URL_REGEX.matchEntire(input)?.groupValues ?: return input
        val scheme = groupValues[1]
        val host = groupValues[2]
        val path = groupValues[3]
        val query = groupValues[4]
        val fragment = groupValues[5]

        if (REDIRECT_PATH_REGEX.matches(path)) return input

        val (cleanedHost, cleanedPath) = canonicalize(host, path)
        val cleanedQuery =
            if (APP_LINK_PATH_REGEX.matches(cleanedPath)) {
                keepParameters(query, APP_LINK_REFERENCE_PARAMETERS)
            } else {
                ""
            }

        return "$scheme$cleanedHost$cleanedPath$cleanedQuery$fragment"
    }

    /**
     * Rewrites the `open.substack.com/pub/<publication>` indirection to the canonical
     * `<publication>.substack.com` host, keeping the remaining path.
     */
    private fun canonicalize(host: String, path: String): Pair<String, String> {
        if (!host.equals(OPEN_HOST, ignoreCase = true)) return Pair(host, path)
        val groupValues =
            OPEN_PUB_PATH_REGEX.matchEntire(path)?.groupValues ?: return Pair(host, path)
        return Pair("${groupValues[1]}.substack.com", groupValues[2])
    }

    /**
     * Removes all parameters of [query] except those named in [parameters], preserving order.
     *
     * `RegexFactory.exceptParameter` is the obvious helper for this but cannot be used here: its
     * `[^&]+` swallows the fragment along with the last parameter, and the fragment is exactly what
     * this sanitizer preserves.
     */
    private fun keepParameters(query: String, parameters: Set<String>): String {
        val kept =
            query.removePrefix("?").split('&').filter { it.substringBefore('=') in parameters }

        return if (kept.isEmpty()) "" else "?${kept.joinToString("&")}"
    }

    private companion object {

        private const val OPEN_HOST = "open.substack.com"

        /**
         * Matches Substack URLs, capturing scheme, host, path, parameters and fragment.
         *
         * The path has to start with `/` so that the host ends at `substack.com` and a URL like
         * `https://substack.com.example.com/p/foo` does not match. An optional port is part of the
         * host group so that it survives into the result.
         */
        private val URL_REGEX =
            Regex(
                "(https?://)?((?:[A-Za-z0-9-]+\\.)*substack\\.com(?::\\d+)?)((?:/[^?#]*)?)(\\?[^#]*)?(#.*)?",
                RegexOption.IGNORE_CASE,
            )

        private val APP_LINK_PATH_REGEX = Regex("/app-link(/.*)?", RegexOption.IGNORE_CASE)

        private val REDIRECT_PATH_REGEX = Regex("/redirect(/.*)?", RegexOption.IGNORE_CASE)

        private val OPEN_PUB_PATH_REGEX =
            Regex("/pub/([A-Za-z0-9-]+)(/.*)?", RegexOption.IGNORE_CASE)

        /** Parameters of `app-link` URLs which reference the actual content. */
        private val APP_LINK_REFERENCE_PARAMETERS = setOf("publication_id", "post_id", "note_id")
    }
}
