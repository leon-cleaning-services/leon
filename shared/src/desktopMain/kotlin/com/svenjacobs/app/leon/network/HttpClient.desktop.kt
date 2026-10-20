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
import io.ktor.client.engine.okhttp.OkHttp
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

actual fun createHttpClient(): HttpClient =
    HttpClient(OkHttp) {
        configureDefaults()

        engine {
            config {
                // Ktor's followRedirects only concerns its own plugin; OkHttp has to be told too.
                followRedirects(false)
                followSslRedirects(false)
            }
        }
    }

internal actual fun platformFailureReason(throwable: Throwable): Resolution.Reason? =
    when (throwable) {
        is UnknownHostException,
        is UnresolvedAddressException,
        is ConnectException,
        is NoRouteToHostException -> Resolution.Reason.Offline
        is SocketTimeoutException -> Resolution.Reason.Timeout
        else -> null
    }
