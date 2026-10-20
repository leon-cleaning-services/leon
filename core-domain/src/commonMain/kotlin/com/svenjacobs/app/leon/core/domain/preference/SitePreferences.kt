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
package com.svenjacobs.app.leon.core.domain.preference

import com.svenjacobs.app.leon.core.domain.url.Url
import com.svenjacobs.app.leon.core.domain.withoutWww
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

/**
 * What the user decided about the parameters of one site, remembered across shares.
 *
 * Only deviations from the catalog are held, and only by parameter name: a value changes with every
 * share, a name does not. Because no sanitizer id is involved either, a remembered choice survives
 * the catalog being reworked in a later release.
 *
 * @param remove Parameters removed although no sanitizer proposes it.
 * @param keep Parameters kept although a sanitizer proposes removing them.
 */
data class SitePreference(
    val remove: ImmutableSet<String> = persistentSetOf(),
    val keep: ImmutableSet<String> = persistentSetOf(),
)

/** Every remembered [SitePreference], keyed by [site][Url.site]. */
typealias SitePreferences = ImmutableMap<String, SitePreference>

/**
 * The site a choice is remembered for: the host, lowercased and without a leading `www.` — the same
 * normalization [HostMatch.Domain][com.svenjacobs.app.leon.core.domain.sanitizer.HostMatch] uses. A
 * subdomain is a site of its own.
 */
val Url.site: String
    get() = host.withoutWww().lowercase()
