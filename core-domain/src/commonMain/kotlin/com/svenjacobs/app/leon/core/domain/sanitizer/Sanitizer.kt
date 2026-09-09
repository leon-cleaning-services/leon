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

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Removes tracking from the URLs of one site or one tracking product.
 *
 * A sanitizer is data, not code: [match] says which URLs it applies to and [rules] say what it
 * would do to them. Interpreting either of those is `Cleaner`'s job, which is what lets it propose
 * the changes to the user before any of them is applied.
 *
 * @param name Displayed in the settings. For a brand this is the brand's name and is not
 *   translated; a descriptive name is translated by the app, which looks up [id].
 * @param match The URLs this sanitizer applies to. A URL matching *any* entry is sanitized; the
 *   default matches every URL, for sanitizers which remove a tracking product's parameters from
 *   wherever they appear.
 */
data class Sanitizer(
    val id: SanitizerId,
    val name: String,
    val rules: ImmutableList<Rule>,
    val match: ImmutableList<Match> = persistentListOf(Match()),
)
