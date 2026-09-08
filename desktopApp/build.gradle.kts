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
        }

        buildTypes.release.proguard {
            // ponytail: enable once Room/Metro keep rules are proven on desktop.
            isEnabled.set(false)
        }
    }
}
