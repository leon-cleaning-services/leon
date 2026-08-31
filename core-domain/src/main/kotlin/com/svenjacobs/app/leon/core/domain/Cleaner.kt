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

import com.svenjacobs.app.leon.core.domain.change.Change
import com.svenjacobs.app.leon.core.domain.change.apply
import com.svenjacobs.app.leon.core.domain.inject.DomainContainer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.inject.DomainContainer.Sanitizers
import com.svenjacobs.app.leon.core.domain.sanitizer.Decode
import com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch
import com.svenjacobs.app.leon.core.domain.sanitizer.Match
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizersCollection
import com.svenjacobs.app.leon.core.domain.sanitizer.Source
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.core.domain.url.decodeUrl
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Performs cleaning of a URL taking all enabled [Sanitizers][Sanitizer] into account. */
class Cleaner(
    private val sanitizers: SanitizersCollection = Sanitizers,
    private val repository: SanitizerRepository = SanitizerRepository,
) {

    data class Result(
        val originalText: String,
        val cleanedText: String,
        val urls: ImmutableList<CleanedUrl>,
    )

    /**
     * A single URL of the cleaned text, together with the changes the sanitizers proposed for it.
     *
     * @param available Every change which was proposed, whether it was applied or not.
     * @param applied The changes which produced [cleaned], in the order they were applied.
     */
    data class CleanedUrl(
        val original: Url,
        val cleaned: Url,
        val available: ImmutableList<Change>,
        val applied: ImmutableList<Change>,
    )

    /**
     * Removes tracking from every URL found in [text].
     *
     * @param declined Changes the user does not want applied. A change is recognized across calls
     *   because cleaning the same URL again proposes equal changes.
     * @param additional Changes the user asked for themselves, applied after the sanitizers.
     */
    suspend fun clean(
        text: String?,
        decodeUrl: Boolean = false,
        declined: Set<Change> = emptySet(),
        additional: Set<Change> = emptySet(),
    ): Result {
        if (text.isNullOrEmpty()) throw IllegalArgumentException()

        val originalText: String = text
        val urls = mutableListOf<CleanedUrl>()
        var cleanedText = originalText

        for (match in URL_REGEX.findAll(originalText)) {
            // A URL which cannot be parsed is left alone rather than mangled.
            val original = Url.parse(match.value) ?: continue
            val url = cleanUrl(original, declined, additional)
            urls += url

            // Serializing an unchanged URL could normalize it, so only touch the text when a change
            // was actually applied.
            if (url.applied.isNotEmpty()) {
                cleanedText = cleanedText.replace(match.value, url.cleaned.toString())
            }
        }

        val result =
            if (decodeUrl) {
                withContext(Dispatchers.Default) { decodeUrl(cleanedText) }
            } else {
                cleanedText
            }

        return Result(
            originalText = originalText,
            cleanedText = result,
            urls = urls.toImmutableList(),
        )
    }

    /**
     * Repeatedly asks every enabled sanitizer what it would change until nothing new is applied.
     *
     * Cleaning is iterative because a change can reveal further work: following a redirect yields a
     * URL of a different site, which its own sanitizers then clean.
     */
    private suspend fun cleanUrl(
        original: Url,
        declined: Set<Change>,
        additional: Set<Change>,
    ): CleanedUrl {
        val available = mutableListOf<Change>()
        val applied = mutableListOf<Change>()
        var current = original

        for (iteration in 0 until MAX_ITERATION) {
            val proposed =
                sanitizers
                    .filter { repository.isEnabled(it.id) }
                    .filter { it.matches(current) }
                    .flatMap { sanitizer ->
                        sanitizer.sanitize(current).map { Change(sanitizer.id, it) }
                    }

            available += proposed.filterNot { it in available }

            val apply = proposed.filterNot { it in declined }
            if (apply.isEmpty()) break

            current = current.apply(apply.map { it.action })
            applied += apply
        }

        val extra = additional.toList().filterNot { it in applied }
        if (extra.isNotEmpty()) {
            current = current.apply(extra.map { it.action })
            applied += extra
        }

        return CleanedUrl(
            original = original,
            cleaned = current,
            available = available.toImmutableList(),
            applied = applied.toImmutableList(),
        )
    }

    private companion object {
        private val URL_REGEX = Regex("https?://.\\S*")
        private const val MAX_ITERATION = 5
    }
}

/**
 * Whether [sanitizer] applies to [url], which it does when *any* of its matches does.
 *
 * Internal rather than private so that a test can assert on matching directly; nothing outside this
 * module interprets a sanitizer.
 */
internal fun Sanitizer.matches(url: Url): Boolean = match.any { it.matches(url) }

/** The changes [sanitizer] proposes for [url], the rules taken in order. */
internal fun Sanitizer.sanitize(url: Url): List<Change.Action> = rules.flatMap { it.sanitize(url) }

internal fun Match.matches(url: Url): Boolean =
    host.matches(url.host) && (pathPrefix == null || url.path.startsWith(pathPrefix))

internal fun HostMatch.matches(host: String): Boolean =
    when (this) {
        is HostMatch.Any -> true
        is HostMatch.Domain -> host.withoutWww().equals(name, ignoreCase = true)
        is HostMatch.Subdomains ->
            host.equals(name, ignoreCase = true) || host.endsWith(".$name", ignoreCase = true)
        // Anchored, so that a lookalike host cannot pass as the one the pattern describes.
        is HostMatch.Pattern -> Regex(regex, RegexOption.IGNORE_CASE).matches(host.withoutWww())
    }

/**
 * Turns a single rule into the changes it would perform, or into nothing at all when it does not
 * apply to this URL — which is not an error, most rules only fit a subset of the URLs their
 * sanitizer matches.
 *
 * The regular expressions are compiled here on every call. Cleaning happens once per shared URL and
 * touches a handful of patterns, so caching them would buy microseconds and cost the catalog its
 * plainness.
 */
internal fun Rule.sanitize(url: Url): List<Change.Action> =
    when (this) {
        is Rule.RemoveParameters -> {
            val regex = Regex(name)
            url.parameters
                .filter { regex.matches(it.name) != negate }
                .map { Change.Action.RemoveParameter(it) }
        }

        is Rule.RemoveEmptyParameters ->
            url.parameters
                .filter { it.value?.isEmpty() == true }
                .map { Change.Action.RemoveParameter(it) }

        is Rule.RemoveFragment -> {
            val fragment = url.fragment
            when {
                fragment == null -> emptyList()
                pattern != null && !Regex(pattern).matches(fragment) -> emptyList()
                else ->
                    listOf(
                        Change.Action.SetComponent(
                            Change.Component.FRAGMENT,
                            from = fragment,
                            to = null,
                        )
                    )
            }
        }

        is Rule.RewriteHost ->
            rewrite(pattern, replacement, url.read(from), current = url.host)?.let {
                listOf(Change.Action.SetComponent(Change.Component.HOST, from = url.host, to = it))
            } ?: emptyList()

        is Rule.RewritePath ->
            rewrite(pattern, replacement, url.read(from), current = url.path)?.let {
                listOf(Change.Action.SetComponent(Change.Component.PATH, from = url.path, to = it))
            } ?: emptyList()

        is Rule.Follow -> {
            val target =
                steps
                    .fold(url.read(from)) { value, step -> value?.let(step::apply) }
                    ?.let(Url::parse)
                    ?.let { if (dropParameters) it.copy(parameters = persistentListOf()) else it }

            if (target == null || target == url) {
                emptyList()
            } else {
                listOf(Change.Action.Replace(from = url, to = target))
            }
        }
    }

/** The value this [Source] names, or `null` when the URL does not have it. */
private fun Url.read(source: Source): String? =
    when (source) {
        is Source.Host -> host
        is Source.Path -> path
        is Source.Fragment -> fragment
        is Source.Parameter -> {
            val regex = Regex(source.name)
            parameters.firstOrNull { regex.matches(it.name) }?.value
        }
    }

/** Applies one decoding step, or returns `null` when it finds nothing to work on. */
@OptIn(ExperimentalEncodingApi::class)
private fun Decode.apply(value: String): String? =
    when (this) {
        is Decode.Capture ->
            Regex(pattern).find(value)?.let { match ->
                // Expand the replacement from this match alone, so that "$1" refers to its groups.
                Regex(pattern).replaceFirst(match.value, replacement)
            }

        is Decode.PercentDecode -> decodeUrl(value)

        is Decode.Base64Decode ->
            runCatching { Base64.Default.decode(value).decodeToString() }.getOrNull()

        is Decode.JsonField ->
            runCatching { Json.parseToJsonElement(value).jsonObject[key]?.jsonPrimitive?.content }
                .getOrNull()
    }

/**
 * Returns [input] with the single match of [pattern] replaced by [replacement], or `null` when
 * [input] is absent, [pattern] does not match the complete [input], or the result is what the
 * rewritten component already holds.
 *
 * Requiring a complete match keeps a pattern such as `.*` from being applied twice, which is what
 * [Regex.replace] would do — it also matches the empty string at the end of the input. The result
 * is compared against [current] rather than against [input], because a rule may read one part of
 * the URL to rewrite another.
 */
private fun rewrite(
    pattern: String,
    replacement: String,
    input: String?,
    current: String,
): String? {
    if (input == null) return null
    val regex = Regex(pattern)
    if (regex.matchEntire(input) == null) return null
    return regex.replaceFirst(input, replacement).takeUnless { it == current }
}

private fun String.withoutWww(): String =
    if (startsWith("www.", ignoreCase = true)) substring(4) else this
