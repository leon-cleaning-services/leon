/*
 * Léon - The URL Cleaner
 * Copyright (C) 2023 Sven Jacobs
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

import java.util.Properties

plugins {
	id("com.android.application")
	kotlin("android")
	kotlin("plugin.parcelize")
	id("com.mikepenz.aboutlibraries.plugin")
	alias(libs.plugins.triplet.play)
}

android {
	namespace = "com.svenjacobs.app.leon"
	compileSdk = Android.compileSdk

	defaultConfig {
		applicationId = "com.svenjacobs.app.leon"
		minSdk = Android.minSdk
		targetSdk = Android.targetSdk
		versionCode = 253
		versionName = "32"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	signingConfigs {
		val signingFile = rootProject.file("signing.properties")
		if (!signingFile.exists()) {
			logger.warn("No signing properties found. Release signing not possible.")
			return@signingConfigs
		}

		val release by creating {
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

	kotlinOptions {
		jvmTarget = "17"
		freeCompilerArgs = listOf(
			"-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
			"-opt-in=kotlin.RequiresOptIn",
		)
	}

	buildFeatures {
		compose = true
		buildConfig = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
	}

	testOptions {
		unitTests.all {
			it.useJUnitPlatform()
		}
	}

	lint {
		disable.add("EnsureInitializerMetadata")
	}
}

play {
	serviceAccountCredentials.set(rootProject.file("google-play-service-account.json"))
	defaultToAppBundles.set(true)
}

dependencies {
	val composeBom = platform(libs.androidx.compose.bom)
	implementation(composeBom)
	implementation(platform(libs.kotlin.bom))

	implementation(project(":core-common"))
	implementation(project(":core-domain"))

	implementation(libs.kotlin.stdlib.jdk8)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity.ktx)

	debugImplementation(libs.androidx.compose.ui.tooling)
	implementation(libs.bundles.androidx.compose)
	implementation(libs.androidx.compose.material2) // Required by AboutLibraries
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.material3.window.size)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.mikepenz.aboutlibraries.compose)
	implementation(libs.google.accompanist.systemuicontroller)

	implementation(libs.androidx.startup.runtime)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.jakewharton.timber)

	debugImplementation(libs.facebook.stetho)

	androidTestImplementation(composeBom)

	testImplementation(libs.kotest.runner.junit5)
	testImplementation(libs.kotest.assertions.core)
	testImplementation(libs.mockk)

	lintChecks(libs.slack.compose.lint.checks)
}
