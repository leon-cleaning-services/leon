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
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout

/**
 * Creates the one [HttpClient] of the app, which is only ever used to resolve short links.
 *
 * Each platform builds it on OkHttp, named explicitly rather than discovered through a
 * `ServiceLoader`, which R8 could not see.
 */
expect fun createHttpClient(): HttpClient

/** The time a single request may take, separately for connecting, reading and in total. */
internal const val TIMEOUT_MILLIS = 5_000L

/**
 * The configuration every platform's [createHttpClient] shares, and which tests apply to a mock
 * engine so that they exercise the same client.
 *
 * - Redirects are not followed: [KtorShortLinkResolver] reads each `Location` itself and stops
 *   before contacting the site a short link leads to. Following them would request that site, with
 *   its tracking parameters, and download the whole page.
 * - A non-2xx status is an answer to look at, not an exception.
 * - There are no cookies, no logging and no default headers. The engine sends Ktor's default
 *   `User-Agent: Ktor client`, which every short link host in the catalog answers with the same
 *   redirect as a browser does (checked with curl against TikTok, Amazon, Reddit, Google Maps,
 *   Lazada and Xiaohongshu), so there is no reason to pretend to be a browser.
 */
internal fun HttpClientConfig<*>.configureDefaults() {
    followRedirects = false
    expectSuccess = false

    install(HttpTimeout) {
        requestTimeoutMillis = TIMEOUT_MILLIS
        connectTimeoutMillis = TIMEOUT_MILLIS
        socketTimeoutMillis = TIMEOUT_MILLIS
    }
}

/**
 * Maps the platform's own exceptions for "the server could not be reached" and "the server did not
 * answer in time" to their [Resolution.Reason], or returns `null` for any other exception.
 *
 * Ktor's multiplatform exceptions are mapped in common code; this covers the `java.*` ones the
 * engine lets through, which common code cannot name.
 */
internal expect fun platformFailureReason(throwable: Throwable): Resolution.Reason?
