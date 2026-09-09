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

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    jvmToolchain(21)

    jvm()

    android {
        namespace = "com.svenjacobs.app.leon.core.domain"
        compileSdk = Android.compileSdk
        minSdk = Android.minSdk
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.collections.immutable)
            api(libs.kotlinx.serialization.json)
            api(libs.kotlinx.coroutines.core)
        }

        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotest.assertions.core)
            implementation(libs.mockk)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
