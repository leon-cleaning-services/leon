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
import com.svenjacobs.app.leon.core.domain.sanitizer.AlwaysEnabled
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AllSanitizers
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution
import com.svenjacobs.app.leon.core.domain.shortlink.Resolution.Reason
import com.svenjacobs.app.leon.core.domain.shortlink.ShortLinkResolver
import com.svenjacobs.app.leon.core.domain.url.Url
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest

private const val VIDEO =
    "https://www.tiktok.com/@/video/7649011711130766612?_r=1&_d=placeholder&u_code=placeholder" +
        "&share_item_id=7649011711130766612&timestamp=&utm_campaign=client_share" +
        "&utm_source=short_fallback&share_app_id=1233"

private const val SHORT_LINK = "https://vm.tiktok.com/ZS923LxwaARJm-5DUYW/"

@OptIn(ExperimentalCoroutinesApi::class)
class KtorShortLinkResolverTest :
    WordSpec(
        {
            "resolve" should
                {
                    "resolve a short link with a single redirect" {
                        val engine = engine { redirect(VIDEO) }

                        engine.resolve(SHORT_LINK) shouldBe resolved(VIDEO)
                        engine.requestedUrls() shouldBe listOf(SHORT_LINK)
                    }

                    "follow a redirect to another short link, but not the target" {
                        val engine = engine { request ->
                            when (request.url.host) {
                                "www.tt.site" ->
                                    redirect("https://www.tiktok.com/t/ZS923LxwaARJm/?a=1")
                                "www.tiktok.com" -> redirect(VIDEO)
                                else -> error("Unexpected request to ${request.url}")
                            }
                        }

                        engine.resolve("https://www.tt.site/t/ZS923LxwaARJm/") shouldBe
                            resolved(VIDEO)
                        engine.requestedUrls() shouldBe
                            listOf(
                                "https://www.tt.site/t/ZS923LxwaARJm/",
                                // The query of the intermediate short link is not sent either.
                                "https://www.tiktok.com/t/ZS923LxwaARJm/",
                            )
                    }

                    "request the short link without its query and fragment" {
                        val engine = engine { redirect(VIDEO) }

                        engine.resolve("$SHORT_LINK?_t=tracking&_r=1#fragment")

                        engine.requestedUrls() shouldBe listOf(SHORT_LINK)
                    }

                    "resolve a Location relative to the host" {
                        val engine = engine {
                            redirect("/r/fossdroid/comments/1659ic4/title/?share_id=placeholder")
                        }

                        engine.resolve("https://www.reddit.com/r/fossdroid/s/Wptky3Ma2I") shouldBe
                            resolved(
                                "https://www.reddit.com/r/fossdroid/comments/1659ic4/title/" +
                                    "?share_id=placeholder",
                            )
                    }

                    "resolve a Location relative to the scheme" {
                        val engine = engine { redirect("//www.amazon.com/dp/B09FDSW9CS/ref=cm_sw") }

                        engine.resolve("https://a.co/d/4X8EKfk") shouldBe
                            resolved("https://www.amazon.com/dp/B09FDSW9CS/ref=cm_sw")
                    }

                    "resolve a Location relative to the path" {
                        val engine = engine { request ->
                            when (request.url.encodedPath) {
                                "/t/AbCd12345" -> redirect("ZS923LxwaARJm")
                                else -> redirect(VIDEO)
                            }
                        }

                        engine.resolve("https://www.tiktok.com/t/AbCd12345") shouldBe
                            resolved(VIDEO)
                        engine.requestedUrls() shouldBe
                            listOf(
                                "https://www.tiktok.com/t/AbCd12345",
                                "https://www.tiktok.com/t/ZS923LxwaARJm",
                            )
                    }

                    "give up after ${KtorShortLinkResolver.MAX_HOPS} requests" {
                        var hop = 0
                        val engine = engine { redirect("https://vm.tiktok.com/Code${++hop}/") }

                        engine.resolve(SHORT_LINK) shouldBe Resolution.Failed(Reason.Unknown)
                        engine.requestHistory.size shouldBe KtorShortLinkResolver.MAX_HOPS
                    }

                    "report a redirect to the start page as expired" {
                        engine { redirect("https://www.tiktok.com/?_r=1") }
                            .resolve(SHORT_LINK) shouldBe Resolution.Failed(Reason.Expired)

                        engine { redirect("http://www.xiaohongshu.com/") }
                            .resolve("https://xhslink.com/a/f8ANF4cQ0nWeb") shouldBe
                            Resolution.Failed(Reason.Expired)

                        engine { redirect("https://www.amazon.com") }
                            .resolve("https://a.co/d/4X8EKfk") shouldBe
                            Resolution.Failed(Reason.Expired)
                    }

                    "report an HTTP error status" {
                        for (status in listOf(403, 404, 500, 503)) {
                            engine { respond("", HttpStatusCode.fromValue(status)) }
                                .resolve(SHORT_LINK) shouldBe
                                Resolution.Failed(Reason.HttpError(status))
                        }
                    }

                    "report an answer without a redirect" {
                        engine { respond("<html>", HttpStatusCode.OK) }.resolve(SHORT_LINK) shouldBe
                            Resolution.Failed(Reason.NoRedirect)

                        engine { respond("", HttpStatusCode.Found) }.resolve(SHORT_LINK) shouldBe
                            Resolution.Failed(Reason.NoRedirect)
                    }

                    "report a Location which is not an HTTP URL as unknown" {
                        engine { redirect("snssdk1233://aweme/detail/7649011711130766612") }
                            .resolve(SHORT_LINK) shouldBe Resolution.Failed(Reason.Unknown)
                    }

                    "report an unreachable server as offline" {
                        for (exception in
                            listOf(
                                UnknownHostException("vm.tiktok.com"),
                                UnresolvedAddressException(),
                                ConnectException("Failed to connect"),
                            )) {
                            engine { throw exception }.resolve(SHORT_LINK) shouldBe
                                Resolution.Failed(Reason.Offline)
                        }
                    }

                    "report a server which does not answer in time as timeout" {
                        for (exception in
                            listOf(
                                HttpRequestTimeoutException(SHORT_LINK, TIMEOUT_MILLIS),
                                ConnectTimeoutException("Connect timeout"),
                                SocketTimeoutException("Read timed out"),
                            )) {
                            engine { throw exception }.resolve(SHORT_LINK) shouldBe
                                Resolution.Failed(Reason.Timeout)
                        }
                    }

                    "report any other exception as unknown" {
                        engine { error("Boom") }.resolve(SHORT_LINK) shouldBe
                            Resolution.Failed(Reason.Unknown)
                    }

                    "propagate cancellation instead of reporting a failure" {
                        val requested = CompletableDeferred<Unit>()
                        val engine = engine {
                            requested.complete(Unit)
                            awaitCancellation()
                        }

                        coroutineScope {
                            val resolution = async { engine.resolve(SHORT_LINK) }
                            requested.await()
                            resolution.cancel()

                            shouldThrow<CancellationException> { resolution.await() }
                        }
                    }

                    "not create the HTTP client until a short link is resolved" {
                        var created = false
                        val resolver =
                            KtorShortLinkResolver(
                                httpClient =
                                    lazy {
                                        created = true
                                        HttpClient(engine { redirect(VIDEO) }) {
                                            configureDefaults()
                                        }
                                    },
                                cleaner = Cleaner(AllSanitizers, AlwaysEnabled),
                            )

                        created shouldBe false
                        resolver.resolve(Url.parse(SHORT_LINK)!!)
                        created shouldBe true
                    }
                }

            "resolveAll" should
                {
                    "key each resolution by the URL as it was passed in, query included" {
                        val engine = engine { request ->
                            when (request.url.host) {
                                "vm.tiktok.com" -> redirect(VIDEO)
                                else -> respond("", HttpStatusCode.NotFound)
                            }
                        }
                        val tiktok = Url.parse("$SHORT_LINK?_t=tracking")!!
                        val amazon = Url.parse("https://a.co/d/4X8EKfk")!!

                        engine.resolver().resolveAll(listOf(tiktok, amazon, tiktok)) shouldBe
                            mapOf(
                                tiktok to resolved(VIDEO),
                                amazon to Resolution.Failed(Reason.HttpError(404)),
                            )
                        // A URL listed twice is resolved once.
                        engine.requestHistory.size shouldBe 2
                    }

                    "return nothing for no URLs" {
                        engine { error("Unexpected request") }
                            .resolver()
                            .resolveAll(emptyList()) shouldBe emptyMap()
                    }

                    "count a URL not resolved within the budget as a timeout, keeping the rest" {
                        runTest {
                            val fast = Url.parse(SHORT_LINK)!!
                            val slow = Url.parse("https://a.co/d/4X8EKfk")!!
                            val resolver = ShortLinkResolver { url ->
                                if (url == slow) awaitCancellation()
                                resolved(VIDEO)
                            }

                            resolver.resolveAll(listOf(fast, slow)) shouldBe
                                mapOf(
                                    fast to resolved(VIDEO),
                                    slow to Resolution.Failed(Reason.Timeout),
                                )
                            currentTime shouldBe RESOLVE_ALL_BUDGET_MILLIS
                        }
                    }
                }
        },
    )

private fun engine(
    handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
) = MockEngine(handler)

private fun MockRequestHandleScope.redirect(location: String) =
    respond("", HttpStatusCode.Found, headersOf(HttpHeaders.Location, location))

private fun MockEngine.resolver() =
    KtorShortLinkResolver(
        httpClient = lazy { HttpClient(this) { configureDefaults() } },
        cleaner = Cleaner(AllSanitizers, AlwaysEnabled),
    )

private suspend fun MockEngine.resolve(url: String) = resolver().resolve(Url.parse(url)!!)

private fun MockEngine.requestedUrls() = requestHistory.map { it.url.toString() }

private fun resolved(url: String) = Resolution.Resolved(Url.parse(url)!!)
