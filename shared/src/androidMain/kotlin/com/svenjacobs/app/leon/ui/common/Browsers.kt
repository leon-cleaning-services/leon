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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.browser.customtabs.CustomTabsService
import androidx.core.net.toUri
import com.svenjacobs.app.leon.ui.model.Browser

/**
 * A link without a host, which only an app handling every https link resolves: a browser. An app
 * that merely opens its own site's links declares a host and does not match it.
 *
 * Seeing the browsers this resolves to on Android 11 and later requires the matching `<queries>`
 * entry in the manifest.
 */
private fun browserIntent(): Intent =
    Intent(Intent.ACTION_VIEW, "https://".toUri()).addCategory(Intent.CATEGORY_BROWSABLE)

/** Whether Léon is the default browser, so that opening a link with it would just reopen Léon. */
fun Context.isDefaultBrowser(): Boolean {
    val activity = browserIntent().resolveActivity(packageManager)
    return activity?.packageName?.startsWith("com.svenjacobs.app.leon") ?: false
}

/**
 * The installed browsers other than Léon, sorted by their label. This queries the package manager
 * and should not be called on the main thread.
 */
fun Context.installedBrowsers(): List<Browser> {
    val pm = packageManager
    val customTabsPackages = customTabsPackages()

    return queryActivities(browserIntent())
        .map { it.activityInfo.applicationInfo }
        // A browser may handle links with more than one activity, but it is chosen as an app.
        .distinctBy { it.packageName }
        .filter { it.packageName != packageName }
        .map {
            Browser(
                packageName = it.packageName,
                label = it.loadLabel(pm).toString(),
                supportsCustomTabs = it.packageName in customTabsPackages,
            )
        }
        .sortedBy { it.label.lowercase() }
}

/**
 * The packages offering a Custom Tabs service, which is how a browser declares that it supports
 * Custom Tabs. Seeing them on Android 11 and later requires the matching `<queries>` entry in the
 * manifest.
 */
private fun Context.customTabsPackages(): Set<String> {
    val intent = Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION)
    val services: List<ResolveInfo> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentServices(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentServices(intent, PackageManager.MATCH_ALL)
        }

    return services.mapTo(mutableSetOf()) { it.serviceInfo.packageName }
}

/**
 * Léon's own activities among those [intent] resolves to, to be left out of a chooser: picking one
 * of them would just reopen Léon. They exist while Léon is registered as a browser.
 */
internal fun Context.ownActivities(intent: Intent): Array<ComponentName> =
    queryActivities(intent)
        .map { it.activityInfo }
        .filter { it.packageName == packageName }
        .map { ComponentName(it.packageName, it.name) }
        .toTypedArray()

private fun Context.queryActivities(intent: Intent): List<ResolveInfo> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.queryIntentActivities(
            intent,
            PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong()),
        )
    } else {
        @Suppress("DEPRECATION")
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
    }
