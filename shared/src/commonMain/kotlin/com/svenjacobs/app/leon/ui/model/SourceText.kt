/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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
package com.svenjacobs.app.leon.ui.model

import androidx.compose.runtime.Immutable

/**
 * Text received via an incoming [android.content.Intent], identified by a stable [id].
 *
 * The [id] stays the same across activity recreation (e.g. configuration changes) but changes for
 * every new incoming intent, even if [text] is identical to the previous one. This allows
 * distinguishing "the same intent redelivered" from "the same text shared again".
 */
@Immutable data class SourceText(val id: String, val text: String?)
