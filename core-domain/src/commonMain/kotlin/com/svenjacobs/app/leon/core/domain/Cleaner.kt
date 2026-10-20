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
package com.svenjacobs.app.leon.core.domain

import com.svenjacobs.app.leon.core.domain.change.Change
import com.svenjacobs.app.leon.core.domain.change.apply
import com.svenjacobs.app.leon.core.domain.sanitizer.AlwaysEnabled
import com.svenjacobs.app.leon.core.domain.sanitizer.Decode
import com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch
import com.svenjacobs.app.leon.core.domain.sanitizer.Match
import com.svenjacobs.app.leon.core.domain.sanitizer.Rule
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerRepository
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizersCollection
import com.svenjacobs.app.leon.core.domain.sanitizer.Source
import com.svenjacobs.app.leon.core.domain.sanitizer.catalog.AllSanitizers
import com.svenjacobs.app.leon.core.domain.sanitizer.resolveRules
import com.svenjacobs.app.leon.core.domain.shortlink.ShortLinkResolver
import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.core.domain.url.decodeUrl
import com.svenjacobs.app.leon.core.domain.url.decoded
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
    private val sanitizers: SanitizersCollection = AllSanitizers,
    private val repository: SanitizerRepository = AlwaysEnabled,
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
     * @param display [cleaned] as it is shown and shared — decoded when the caller asked for that,
     *   and the same object otherwise. [cleaned] stays encoded, because it is what the change list
     *   is built from and what a change is applied to on the next pass.
     */
    data class CleanedUrl(
        val original: Url,
        val cleaned: Url,
        val display: Url,
        val available: ImmutableList<Change>,
        val applied: ImmutableList<Change>,
    )

    /**
     * Removes tracking from every URL found in [text].
     *
     * @param declined Changes the user does not want applied. A change is recognized across calls
     *   because cleaning the same URL again proposes equal changes.
     * @param additional Changes the user asked for themselves, applied after the sanitizers.
     * @param resolved The targets of the [short links][shortLinks] in [text] which the caller
     *   resolved beforehand, keyed by the short link as it was parsed out of [text]. A short link
     *   with a target is replaced by it — a change the user can decline like any other — and one
     *   without is cleaned as if it was an ordinary URL. Cleaning itself never performs a request.
     */
    suspend fun clean(
        text: String?,
        decodeUrl: Boolean = false,
        declined: Set<Change> = emptySet(),
        additional: Set<Change> = emptySet(),
        resolved: Map<Url, Url> = emptyMap(),
    ): Result {
        val input = requireNotNull(text?.takeIf(String::isNotEmpty))

        val urls = mutableListOf<CleanedUrl>()
        var cleanedText = input

        for (match in URL_REGEX.findAll(input)) {
            // A URL which cannot be parsed is left alone rather than mangled.
            val original = Url.parse(match.value) ?: continue
            val url = cleanUrl(original, decodeUrl, declined, additional, resolved)
            urls += url

            // Serializing an unchanged URL could normalize it, so only touch the text when
            // cleaning or decoding actually changed something.
            if (url.applied.isNotEmpty() || url.display != url.cleaned) {
                cleanedText = cleanedText.replace(match.value, url.display.toString())
            }
        }

        return Result(
            originalText = input,
            cleanedText = cleanedText,
            urls = urls.toImmutableList(),
        )
    }

    /**
     * The URLs in [text] which are short links: an enabled sanitizer applies to them and declares
     * them short links with a [Rule.Resolve]. Each one is listed once, in the order it first
     * appears.
     *
     * These are the only URLs a caller should ask a [ShortLinkResolver] about, and their targets
     * are what [clean] expects as `resolved`. An empty [text] has none.
     */
    suspend fun shortLinks(text: String?): List<Url> =
        URL_REGEX.findAll(text.orEmpty())
            .mapNotNull { Url.parse(it.value) }
            .distinct()
            .toList()
            .filter { isShortLink(it) }

    /**
     * Whether an enabled sanitizer declares [url] a short link.
     *
     * A resolver uses this to decide whether a redirect leads to yet another short link, which it
     * may follow, or to the site the short link stands for, which it must not contact.
     */
    suspend fun isShortLink(url: Url): Boolean = sanitizers.any { sanitizer ->
        repository.isEnabled(sanitizer.id) &&
            sanitizer.matches(url) &&
            sanitizer.resolveRules.any { it.appliesTo(url) }
    }

    /**
     * Repeatedly asks every enabled sanitizer what it would change until nothing is left to apply.
     *
     * Cleaning is iterative because a change can reveal further work: following a redirect yields a
     * URL of a different site, which its own sanitizers then clean. Settling is the normal exit —
     * [MAX_ITERATION] only bounds a catalog which never settles, such as a rewrite that appends to
     * the path on every pass, or two sanitizers rewriting a host back and forth.
     */
    private suspend fun cleanUrl(
        original: Url,
        decodeUrl: Boolean,
        declined: Set<Change>,
        additional: Set<Change>,
        resolved: Map<Url, Url>,
    ): CleanedUrl {
        // A set, so a change proposed again on a later pass is listed once, where it first
        // appeared.
        val available = mutableSetOf<Change>()
        val applied = mutableListOf<Change>()
        var current = original

        // `repeat` is deliberately not used here: its `return@repeat` would continue rather than
        // stop, and settling early is the whole point of the check below.
        for (pass in 1..MAX_ITERATION) {
            val proposed = propose(current, resolved)
            available += proposed

            val apply = proposed.filterNot { it in declined }
            if (apply.isEmpty()) break

            // Every change of a pass was proposed for the same URL, so a replacement goes last:
            // its target is a different URL, which the other changes were never meant for. It is
            // cleaned on the next pass instead.
            current =
                current.apply(apply.map { it.action }.sortedBy { it is Change.Action.Replace })
            applied += apply
        }

        val extra = additional.filterNot { it in applied }
        current = current.apply(extra.map { it.action })
        applied += extra

        return CleanedUrl(
            original = original,
            cleaned = current,
            display =
                if (decodeUrl) withContext(Dispatchers.Default) { current.decoded() } else current,
            available = available.toImmutableList(),
            applied = applied.toImmutableList(),
        )
    }

    /** What every enabled sanitizer which applies to [url] would change about it. */
    private suspend fun propose(url: Url, resolved: Map<Url, Url>): List<Change> =
        sanitizers
            .filter { repository.isEnabled(it.id) }
            .filter { it.matches(url) }
            .flatMap { sanitizer ->
                sanitizer.sanitize(url, resolved).map { Change(sanitizer.id, it) }
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

/**
 * The changes [sanitizer] proposes for [url], the rules taken in order.
 *
 * @param resolved The known targets of short links, see [Cleaner.clean].
 */
internal fun Sanitizer.sanitize(
    url: Url,
    resolved: Map<Url, Url> = emptyMap(),
): List<Change.Action> = rules.flatMap { it.sanitize(url, resolved) }

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
 *
 * @param resolved The known targets of short links, keyed by the short link. Only [Rule.Resolve]
 *   reads it; it is how that rule gets by without a request of its own.
 */
internal fun Rule.sanitize(url: Url, resolved: Map<Url, Url> = emptyMap()): List<Change.Action> =
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

        is Rule.RemoveFragment ->
            url.fragment
                ?.takeIf { pattern == null || Regex(pattern).matches(it) }
                ?.let { setComponent(Change.Component.FRAGMENT, from = it, to = null) }
                .orEmpty()

        is Rule.RewriteHost ->
            rewrite(pattern, replacement, url.read(from), current = url.host)
                ?.let { setComponent(Change.Component.HOST, from = url.host, to = it) }
                .orEmpty()

        is Rule.RewritePath ->
            rewrite(pattern, replacement, url.read(from), current = url.path)
                ?.let { setComponent(Change.Component.PATH, from = url.path, to = it) }
                .orEmpty()

        is Rule.Follow ->
            steps
                .fold(url.read(from)) { value, step -> value?.let(step::apply) }
                ?.let(Url::parse)
                ?.let { if (dropParameters) it.copy(parameters = persistentListOf()) else it }
                ?.takeUnless { it == url }
                ?.let { listOf(Change.Action.Replace(from = url, to = it)) }
                .orEmpty()

        is Rule.Resolve ->
            resolved[url]
                ?.takeIf { appliesTo(url) && it != url }
                ?.let { listOf(Change.Action.Replace(from = url, to = it)) }
                .orEmpty()
    }

/** Whether this rule declares [url] a short link, which it does when its pattern fits the path. */
internal fun Rule.Resolve.appliesTo(url: Url): Boolean = Regex(pattern).matches(url.path)

private fun setComponent(component: Change.Component, from: String?, to: String?) =
    listOf(Change.Action.SetComponent(component, from = from, to = to))

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
            with(Regex(pattern)) {
                // Expanded from the match alone, so that "$1" refers to that match's groups.
                find(value)?.let { replaceFirst(it.value, replacement) }
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
