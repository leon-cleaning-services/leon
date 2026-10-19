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

import com.svenjacobs.app.leon.core.domain.Cleaner
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution.Reason
import com.svenjacobs.app.leon.core.domain.shortlink.ShortLinkResolver
import com.svenjacobs.app.leon.core.domain.url.Url
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.prepareGet
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.collections.immutable.persistentListOf

/**
 * Resolves a short link by asking the service behind it, one redirect at a time.
 *
 * Only the short link service is ever contacted, never the site it leads to: each `Location` is
 * read rather than followed, and resolving stops at the first one which [Cleaner.isShortLink] does
 * not consider a short link — the target. Some services redirect to another short link first, such
 * as `tt.site/t/<code>` to `tiktok.com/t/<code>`; those are followed, up to [MAX_HOPS] requests.
 *
 * A short link is requested without its query and fragment. Its code is in the path, and the query
 * is where the tracking parameters are which Léon is about to remove.
 *
 * The [HttpClient] is injected lazily so that none is created unless a short link is resolved.
 */
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class KtorShortLinkResolver(
    private val httpClient: Lazy<HttpClient>,
    private val cleaner: Cleaner,
) : ShortLinkResolver {

    override suspend fun resolve(url: Url): Resolution =
        try {
            follow(url)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpRequestTimeoutException) {
            Resolution.Failed(Reason.Timeout)
        } catch (e: ConnectTimeoutException) {
            Resolution.Failed(Reason.Timeout)
        } catch (e: SocketTimeoutException) {
            Resolution.Failed(Reason.Timeout)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Resolution.Failed(failureReason(e))
        }

    private suspend fun follow(url: Url): Resolution {
        var current = url.withoutQuery()

        repeat(MAX_HOPS) {
            val step =
                httpClient.value.prepareGet(current.toString()).execute { response ->
                    // The body is never read: execute() discards it when this block returns.
                    val status = response.status.value
                    when (status) {
                        in 300..399 ->
                            response.headers[HttpHeaders.Location]?.let { location ->
                                resolveLocation(current, location)?.let { Step.Next(it) }
                                    ?: Step.Done(Resolution.Failed(Reason.Unknown))
                            } ?: Step.Done(Resolution.Failed(Reason.NoRedirect))
                        in 400..599 -> Step.Done(Resolution.Failed(Reason.HttpError(status)))
                        else -> Step.Done(Resolution.Failed(Reason.NoRedirect))
                    }
                }

            val next =
                when (step) {
                    is Step.Done -> return step.resolution
                    is Step.Next -> step.url
                }

            if (!cleaner.isShortLink(next)) {
                return if (next.path.isEmpty() || next.path == "/") {
                    // An unknown or expired short link redirects to the service's start page.
                    Resolution.Failed(Reason.Expired)
                } else {
                    Resolution.Resolved(next)
                }
            }

            current = next.withoutQuery()
        }

        // Still a short link after MAX_HOPS redirects: most likely a loop.
        return Resolution.Failed(Reason.Unknown)
    }

    private sealed interface Step {
        data class Next(val url: Url) : Step

        data class Done(val resolution: Resolution) : Step
    }

    companion object {

        /** The most requests resolving a single short link makes. */
        const val MAX_HOPS = 5
    }
}

private fun Url.withoutQuery() = copy(parameters = persistentListOf(), fragment = null)

/**
 * Resolves the [location] a server redirected [current] to.
 *
 * An absolute location is taken verbatim, so that its encoding survives exactly as the server sent
 * it. Only a relative one goes through Ktor's URL resolution. A location which is not an HTTP(S)
 * URL, such as an app's custom scheme, yields `null`.
 */
private fun resolveLocation(current: Url, location: String): Url? =
    when {
        SCHEME_REGEX.containsMatchIn(location) -> Url.parse(location)
        else ->
            runCatching { URLBuilder(current.toString()).takeFrom(location).buildString() }
                .getOrNull()
                ?.let(Url::parse)
    }

/** Whether a location is absolute, starting with a scheme such as `https:` or `snssdk1233:`. */
private val SCHEME_REGEX = Regex("^[A-Za-z][A-Za-z0-9+.-]*:")

private fun failureReason(e: Throwable): Reason =
    generateSequence(e) { it.cause }.firstNotNullOfOrNull { platformFailureReason(it) }
        ?: Reason.Unknown
