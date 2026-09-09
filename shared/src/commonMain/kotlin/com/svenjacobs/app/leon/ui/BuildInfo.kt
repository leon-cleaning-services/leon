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
package com.svenjacobs.app.leon.ui

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Stands in for the generated per-module build config, which does not exist in a KMP library
 * module. Every root composable (`MainActivity`, the desktop `main`) provides its platform's real
 * values.
 */
data class BuildInfo(val isDebug: Boolean, val versionName: String)

val LocalBuildInfo = staticCompositionLocalOf { BuildInfo(isDebug = false, versionName = "") }
