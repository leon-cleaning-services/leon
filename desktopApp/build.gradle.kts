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

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    alias(libs.plugins.compose.multiplatform)
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.metro)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
    implementation(libs.androidx.sqlite.bundled)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.androidx.room3.runtime)
    implementation(libs.metro.viewmodel.compose)
}

// Keeps the desktop package version in lockstep with the Android app's versionName so the two
// never drift; this file is read, never written to.
val versionName =
    Regex("""versionName = "(\d+)"""")
        .find(rootProject.file("androidApp/build.gradle.kts").readText())!!
        .groupValues[1]

compose.desktop {
    application {
        mainClass = "com.svenjacobs.app.leon.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.Msi, TargetFormat.Dmg)
            packageName = "leon"
            packageVersion = "$versionName.0.0"
            description = "Léon – The URL Cleaner"
            vendor = "Sven Jacobs"
            licenseFile.set(rootProject.file("LICENSE"))
            copyright = "© 2026 Sven Jacobs. Licensed under GPL-3.0-or-later."
            // jdeps' module auto-detection misses this: AndroidX DataStore's protobuf-lite uses
            // sun.misc.Unsafe reflectively, so without it the packaged app crashes on startup with
            // NoClassDefFoundError: sun/misc/Unsafe as soon as it touches the preferences DataStore.
            modules("jdk.unsupported")

            linux {
                iconFile.set(project.file("packaging/icons/leon.png"))
                debMaintainer = "github@svenjacobs.com"
                // Without this the rpm's License tag reads "Unknown"; licenseFile only ships the
                // text. SPDX identifier, as Fedora's packaging guidelines expect.
                rpmLicenseType = "GPL-3.0-or-later"
                appCategory = "Utility"
                menuGroup = "Utility"
                shortcut = true
            }

            windows {
                iconFile.set(project.file("packaging/icons/leon.ico"))
                packageVersion = "$versionName.0.0"
                menuGroup = "Léon"
                shortcut = true
                dirChooser = true
                // Generated once with `uuidgen`. NEVER change this: it is what makes an MSI upgrade
                // replace the installed copy instead of installing a second one beside it.
                upgradeUuid = "1c0f6d56-1fb8-4af9-867f-f518beb75083"
            }

            macOS {
                iconFile.set(project.file("packaging/icons/leon.icns"))
                bundleID = "com.svenjacobs.app.leon"
                packageName = "Leon" // → Leon.app / Leon-65.0.0.dmg; the global `leon` stays the Linux binary
                dockName = "Léon"
            }
        }

        buildTypes.release.proguard {
            // ponytail: enable once Room/Metro keep rules are proven on desktop.
            isEnabled.set(false)
        }
    }
}
