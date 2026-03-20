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

package com.svenjacobs.app.leon.core.domain

import com.svenjacobs.app.leon.core.common.url.decodeUrl
import com.svenjacobs.app.leon.core.domain.inject.DomainContainer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.inject.DomainContainer.Sanitizers
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizersCollection
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Performs cleaning of a URL taking all enabled [Sanitizers][Sanitizer] into account.
 */
class CleanerService(
    private val sanitizers: SanitizersCollection = Sanitizers,
    private val repository: SanitizerRepository = SanitizerRepository,
) {

    data class Result(
        val originalText: String,
        val cleanedText: String,
        val urls: ImmutableList<String>,
    )

    suspend fun clean(text: String?, decodeUrl: Boolean = false): Result {
        if (text.isNullOrEmpty()) throw IllegalArgumentException()

        val (cleaned, urls) = URL_REGEX
            .findAll(text)
            .fold(Pair(text, emptyList<String>())) { (currentText, urls), match ->
                var prevResult: String
                var result = match.value
                var i = 0

                do {
                    prevResult = result
                    result = cleanUrl(result, sanitizers)
                    i++
                } while (result != prevResult && i < MAX_ITERATION)

                Pair(currentText.replace(match.value, result), urls + result)
            }
            .let { (cleaned, urls) ->
                val decoded = if (decodeUrl) {
                    withContext(Dispatchers.Default) {
                        decodeUrl(cleaned)
                    }
                } else {
                    cleaned
                }

                Pair(decoded, urls)
            }

        return Result(
            originalText = text,
            cleanedText = cleaned,
            urls = urls.toImmutableList(),
        )
    }

    private suspend fun cleanUrl(url: String, sanitizers: SanitizersCollection): String {
        val cleaned = sanitizers
            .filter { repository.isEnabled(it.id) }
            .filter { it.matchesDomain(url) }
            .fold(url) { currentUrl, sanitizer ->
                sanitizer(currentUrl)
            }

        return if (cleaned.contains('?')) {
            cleaned
        } else {
            cleaned.replaceFirst('&', '?')
        }
    }

    private companion object {
        private val URL_REGEX = Regex("https?://.\\S*")
        private const val MAX_ITERATION = 5
    }
}
