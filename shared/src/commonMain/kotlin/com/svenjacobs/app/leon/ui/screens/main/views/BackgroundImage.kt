/*
 * Léon - The URL Cleaner
 * Copyright (C) 2022 Sven Jacobs
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
package com.svenjacobs.app.leon.ui.screens.main.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.svenjacobs.app.leon.shared.resources.Res
import com.svenjacobs.app.leon.shared.resources.background_broom
import com.svenjacobs.app.leon.shared.resources.background_bug
import com.svenjacobs.app.leon.ui.LocalBuildInfo
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun BackgroundImage(modifier: Modifier = Modifier) {
    val isDebug = LocalBuildInfo.current.isDebug

    Image(
        modifier = modifier.fillMaxSize(),
        painter =
            painterResource(
                if (isDebug) Res.drawable.background_bug else Res.drawable.background_broom
            ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        // The drawable is black; without a tint it disappears against a dark surface. Its own
        // 5% fillAlpha survives the SrcIn tint, so this only swaps the hue, not the subtlety.
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
    )
}
