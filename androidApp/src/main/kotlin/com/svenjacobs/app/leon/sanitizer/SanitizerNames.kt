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

import android.content.Context
import com.svenjacobs.app.leon.R
import com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer
import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizerId

/**
 * The sanitizers whose name is a description rather than a brand, and therefore translated.
 *
 * `Sanitizer.name` carries the English text for everything else — a brand is the same in every
 * language, and keeping it in the catalog is what lets `core-domain` do without Android resources.
 */
private val TRANSLATED_NAMES =
    mapOf(
        SanitizerId("amazon") to R.string.sanitizer_amazon_product_name,
        SanitizerId("aol_search") to R.string.sanitizer_aol_search_name,
        SanitizerId("bluesky_redirect") to R.string.sanitizer_bluesky_redirect_name,
        SanitizerId("empty_parameters") to R.string.sanitizer_empty_parameters_name,
        SanitizerId("google_search") to R.string.sanitizer_google_search_name,
        SanitizerId("mydealz_parameters") to R.string.sanitizer_mydealz_parameters_name,
        SanitizerId("mydealz_redirects") to R.string.sanitizer_mydealz_redirects_name,
        SanitizerId("yahoo_search") to R.string.sanitizer_yahoo_search_name,
        SanitizerId("youtube_redirect") to R.string.sanitizer_youtube_redirect_name,
    )

/** The name to show for this sanitizer, translated where it is not a brand name. */
fun Sanitizer.displayName(context: Context): String =
    TRANSLATED_NAMES[id]?.let(context::getString) ?: name
