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
package com.svenjacobs.app.leon.sanitizer

import androidx.compose.runtime.Composable
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.sanitizer_amazon_product_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_aol_search_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_bluesky_redirect_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_empty_parameters_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_google_search_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_mydealz_parameters_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_mydealz_redirects_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_short_link_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_yahoo_search_name
import com.svenjacobs.app.leon.shared.resources.sanitizer_youtube_redirect_name
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/** A translated name: [resource], formatted with [args] — the brand, where the name has one. */
private class TranslatedName(val resource: StringResource, vararg val args: Any)

/** The short link sanitizer of [brand], whose name is "<brand> Short Link" in English. */
private fun shortLink(brand: String) = TranslatedName(Res.string.sanitizer_short_link_name, brand)

/**
 * The sanitizers whose name is a description rather than a brand, and therefore translated.
 *
 * `Sanitizer.name` carries the English text for everything else — a brand is the same in every
 * language, and keeping it in the catalog is what lets `core-domain` do without Android resources.
 */
private val TRANSLATED_NAMES: Map<SanitizerId, TranslatedName> =
    mapOf(
        SanitizerId("amazon") to TranslatedName(Res.string.sanitizer_amazon_product_name),
        SanitizerId("amazon_short_link") to shortLink("Amazon"),
        SanitizerId("aol_search") to TranslatedName(Res.string.sanitizer_aol_search_name),
        SanitizerId("bluesky_redirect") to
            TranslatedName(Res.string.sanitizer_bluesky_redirect_name),
        SanitizerId("empty_parameters") to
            TranslatedName(Res.string.sanitizer_empty_parameters_name),
        SanitizerId("google_maps_short_link") to shortLink("Google Maps"),
        SanitizerId("google_search") to TranslatedName(Res.string.sanitizer_google_search_name),
        SanitizerId("lazada_short_link") to shortLink("Lazada"),
        SanitizerId("mydealz_parameters") to
            TranslatedName(Res.string.sanitizer_mydealz_parameters_name),
        SanitizerId("mydealz_redirects") to
            TranslatedName(Res.string.sanitizer_mydealz_redirects_name),
        SanitizerId("reddit_short_link") to shortLink("Reddit"),
        SanitizerId("tiktok_short_link") to shortLink("TikTok"),
        SanitizerId("xiaohongshu_short_link") to shortLink("Xiaohongshu"),
        SanitizerId("yahoo_search") to TranslatedName(Res.string.sanitizer_yahoo_search_name),
        SanitizerId("youtube_redirect") to
            TranslatedName(Res.string.sanitizer_youtube_redirect_name),
    )

// Kotlin does not allow `suspend fun displayName()` and `fun displayName()` to overload each other
// even though their JVM signatures differ (the compiler reports "conflicting overloads" /
// "overload resolution ambiguity") - the plan's identically-named pair does not compile, hence
// `resolveDisplayName` below for the suspend variant.

/** The name to show for this sanitizer, translated where it is not a brand name. */
suspend fun Sanitizer.resolveDisplayName(): String =
    TRANSLATED_NAMES[id]?.let { getString(it.resource, *it.args) } ?: name

/** The name to show for this sanitizer, translated where it is not a brand name. */
@Composable
fun Sanitizer.displayName(): String =
    TRANSLATED_NAMES[id]?.let { stringResource(it.resource, *it.args) } ?: name
