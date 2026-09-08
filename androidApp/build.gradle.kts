/*
 * Léon - The URL Cleaner
 * Copyright (C) 2024 Sven Jacobs
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

@file:Suppress("UnstableApiUsage")

import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.android.compose.screenshot)
    alias(libs.plugins.metro)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

android {
    namespace = "com.svenjacobs.app.leon"
    compileSdk = Android.compileSdk

    defaultConfig {
        applicationId = "com.svenjacobs.app.leon"
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = 286
        versionName = "65"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val signingFile = rootProject.file("signing.properties")
        if (!signingFile.exists()) {
            logger.warn("No signing properties found. Release signing not possible.")
            return@signingConfigs
        }

        create("release") {
            val props = Properties()
            signingFile.inputStream().use { props.load(it) }

            storeFile = rootProject.file("upload-keystore.jks")
            storePassword = props.getProperty("storePassword")
            keyPassword = props.getProperty("keyPassword")
            keyAlias = "upload"
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    lint {
        disable.add("EnsureInitializerMetadata")
    }

    androidResources {
        generateLocaleConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs.addAll(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlin.RequiresOptIn",
        )
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(platform(libs.kotlin.bom))

    implementation(projects.shared)

    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.metro.viewmodel)
    implementation(libs.metro.viewmodel.compose)

    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.browser)
    implementation(libs.jakewharton.timber)
    implementation(libs.androidx.room3.runtime)
    implementation(libs.androidx.sqlite.framework)

    debugImplementation(libs.facebook.stetho)

    screenshotTestImplementation(libs.android.tools.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    lintChecks(libs.slack.compose.lint.checks)
}
