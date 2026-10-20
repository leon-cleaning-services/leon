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
package com.svenjacobs.app.leon.network

import com.svenjacobs.app.leon.core.domain.shortlink.Resolution
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution.Reason
import com.svenjacobs.app.leon.core.domain.shortlink.ShortLinkResolver
import com.svenjacobs.app.leon.core.domain.url.Url
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull

/**
 * The longest resolving all short links of one text may take, in milliseconds.
 *
 * A single short link can take up to [KtorShortLinkResolver.MAX_HOPS] requests of up to
 * [TIMEOUT_MILLIS] each — far longer than anybody watches a progress dialog. Whatever is not
 * resolved by then counts as [Reason.Timeout], which the user can retry.
 */
const val RESOLVE_ALL_BUDGET_MILLIS = 15_000L

/**
 * Resolves every one of [urls] concurrently, within [RESOLVE_ALL_BUDGET_MILLIS] altogether.
 *
 * The result is keyed by the URLs exactly as they were passed in, query and all, which is how
 * `Cleaner.shortLinks` returns them and `Cleaner.clean` expects them as `resolved`. Each URL is
 * resolved once, however often it is listed. A URL which is not resolved within the budget is
 * [Resolution.Failed] with [Reason.Timeout]; the others keep their own result.
 */
suspend fun ShortLinkResolver.resolveAll(urls: List<Url>): Map<Url, Resolution> = coroutineScope {
    // Every URL starts at the same moment, so a timeout of its own is the budget of all of them.
    urls
        .distinct()
        .map { url ->
            async {
                url to
                    (withTimeoutOrNull(RESOLVE_ALL_BUDGET_MILLIS) { resolve(url) }
                        ?: Resolution.Failed(Reason.Timeout))
            }
        }
        .awaitAll()
        .toMap()
}
