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
import java.awt.Desktop
import java.net.URI

private object DesktopUrlActions : UrlActions {

    override val canShare = false
    override val isDefaultBrowser = false

    // No share sheet on desktop; the Share affordance is hidden wherever `canShare` is `false`.
    override fun share(text: String, chooserTitle: String) {}

    override fun open(url: String, customTabs: Boolean, chooserTitle: String) {
        if (!Desktop.isDesktopSupported()) return
        val desktop = Desktop.getDesktop()
        if (!desktop.isSupported(Desktop.Action.BROWSE)) return
        runCatching { desktop.browse(URI(url)) }
    }
}

@Composable actual fun rememberUrlActions(): UrlActions = DesktopUrlActions
