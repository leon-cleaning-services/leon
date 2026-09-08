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

import com.svenjacobs.app.leon.core.domain.Cleaner
import com.svenjacobs.app.leon.core.domain.matches
import com.svenjacobs.app.leon.core.domain.url.Url
import io.kotest.core.spec.style.WordSpec
import kotlinx.collections.immutable.persistentListOf

/**
 * Base class for the test of a single sanitizer, which it runs through a [Cleaner] holding nothing
 * but that one sanitizer.
 *
 * Going through the [Cleaner] is what makes these tests meaningful now that a sanitizer is data: it
 * is the [Cleaner] which interprets that data, so testing the two together is testing the thing the
 * user gets.
 *
 * ```
 * class ExampleSanitizerTest :
 *     SanitizerSpec(
 *         ExampleSanitizer,
 *         {
 *             "clean" should {
 *                 "remove example_ parameters" {
 *                     clean("https://example.com/?example_a=1&keep=2") shouldBe
 *                         "https://example.com/?keep=2"
 *                 }
 *             }
 *         },
 *     )
 * ```
 */
abstract class SanitizerSpec(private val sanitizer: Sanitizer, body: SanitizerSpec.() -> Unit) :
    WordSpec() {

    private val cleaner =
        Cleaner(sanitizers = persistentListOf(sanitizer), repository = AlwaysEnabled)

    /** Cleans [url] with this sanitizer alone and returns the result. */
    suspend fun clean(url: String): String = cleaner.clean(url).cleanedText

    /**
     * Whether this sanitizer applies to [url] at all, regardless of whether it would find anything
     * to change in it.
     *
     * A scheme is added when [url] has none, so that the schemeless cases the old string based API
     * accepted keep being expressed the way they were. [Cleaner] only ever hands over URLs which
     * start with a scheme.
     */
    fun matches(url: String): Boolean {
        val absolute = if (url.startsWith("http")) url else "https://$url"
        return Url.parse(absolute)?.let(sanitizer::matches) == true
    }

    init {
        body()
    }
}
