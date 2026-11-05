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
package com.svenjacobs.app.leon.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Loaded off the main thread: rendering an adaptive icon into a bitmap takes long enough to drop
// frames when a dialog lists several apps at once.
@Composable
actual fun rememberAppIcon(packageName: String, size: Dp): ImageBitmap? {
    val context = LocalContext.current
    val sizePx = with(LocalDensity.current) { size.roundToPx() }

    val icon by
        produceState<ImageBitmap?>(initialValue = null, context, packageName, sizePx) {
            value =
                withContext(Dispatchers.IO) {
                    runCatching {
                        context.packageManager
                            .getApplicationIcon(packageName)
                            .toBitmap(width = sizePx, height = sizePx)
                            .asImageBitmap()
                    }
                    // Not installed (anymore): the caller shows its placeholder instead.
                    .getOrNull()
                }
        }

    return icon
}
