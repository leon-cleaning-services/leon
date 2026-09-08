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

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.metro)
    alias(libs.plugins.room3)
    alias(libs.plugins.aboutlibraries)
}

kotlin {
    jvmToolchain(21)

    jvm("desktop")

    android {
        namespace = "com.svenjacobs.app.leon.shared"
        compileSdk = Android.compileSdk
        minSdk = Android.minSdk

        // CMP-9547: without this, Compose resources are silently missing from the APK.
        androidResources { enable = true }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.coreDomain)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.jetbrains.compose.ui.tooling.preview)

            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.material3.window.size)
            implementation(libs.jetbrains.compose.material3.adaptive)
            implementation(libs.jetbrains.compose.material3.adaptive.layout)
            implementation(libs.jetbrains.compose.material3.adaptive.navigation)
            implementation(libs.jetbrains.compose.material3.adaptive.navigation.suite)
            implementation(libs.jetbrains.compose.material3.adaptive.navigation3)

            implementation(libs.jetbrains.compose.material.icons.core)
            implementation(libs.jetbrains.compose.material.icons.extended)

            implementation(libs.jetbrains.navigation3.ui)

            implementation(libs.jetbrains.lifecycle.viewmodel.compose)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.jetbrains.lifecycle.viewmodel.navigation3)

            implementation(libs.metro.viewmodel)
            implementation(libs.metro.viewmodel.compose)

            implementation(libs.androidx.room3.runtime)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.mikepenz.aboutlibraries.compose.m3)
        }

        androidMain.dependencies {
            implementation(libs.androidx.sqlite.framework)
            implementation(libs.androidx.datastore.preferences)
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.androidx.sqlite.bundled)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5)
                implementation(libs.kotest.assertions.core)
                implementation(libs.mockk)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

compose.resources {
    packageOfResClass = "com.svenjacobs.app.leon.shared.resources"
    publicResClass = false
}

room3 { schemaDirectory("$projectDir/schemas") }

dependencies {
    add("kspAndroid", libs.androidx.room3.compiler)
    add("kspDesktop", libs.androidx.room3.compiler)

    // The KMP library plugin has no build variants (no debugImplementation); androidRuntimeClasspath
    // is a real DependencyHandler-generated accessor for the android target's tooling-only config,
    // but only resolves from the top-level `dependencies { }` block, not from inside `kotlin { }`.
    androidRuntimeClasspath(platform(libs.androidx.compose.bom))
    androidRuntimeClasspath(libs.androidx.compose.ui.tooling)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// aboutlibraries-plugin 15.2.0 has no `android { registerAndroidTasks = ... }` DSL (plan assumed a
// newer/different API); export{}'s outputFile is a RegularFileProperty, set via .set(), not `=`.
aboutLibraries {
    export {
        outputFile.set(file("src/commonMain/composeResources/files/aboutlibraries.json"))
    }
}
