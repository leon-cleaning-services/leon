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
package com.svenjacobs.app.leon

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AndroidPlatform(private val context: Context) : Platform {

    override val supportsBrowserRegistration = true
    override val supportsCustomTabs = true
    override val supportsProtectScreen = true

    override fun isRegisteredAsBrowser(): Boolean =
        packageManager.getComponentEnabledSetting(componentName) ==
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED

    override fun setRegisteredAsBrowser(enabled: Boolean) {
        packageManager.setComponentEnabledSetting(
            componentName,
            if (enabled) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            },
            PackageManager.DONT_KILL_APP,
        )
    }

    private val packageManager: PackageManager
        get() = context.packageManager

    private val componentName: ComponentName
        get() = ComponentName(context.packageName, "${context.packageName}.$COMPONENT_NAME_CLASS")

    private companion object {
        private const val COMPONENT_NAME_CLASS = "MainBrowserActivity"
    }
}
