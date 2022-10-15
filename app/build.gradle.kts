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

import java.util.*

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
	buildToolsVersion = Android.buildToolsVersion

	defaultConfig {
		applicationId = "com.svenjacobs.app.leon"
		minSdk = Android.minSdk
		targetSdk = Android.targetSdk
		versionCode = 237
		versionName = "1.2.0"

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
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
			signingConfig = signingConfigs.getByName("release")
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}

	kotlinOptions {
		jvmTarget = "11"
		freeCompilerArgs = listOf(
			"-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
			"-opt-in=kotlin.RequiresOptIn",
		)
	}

	buildFeatures {
		compose = true
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
	implementation(project(":core-common"))
	implementation(project(":core-domain"))

	implementation(libs.kotlin.stdlib.jdk8)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.activity.ktx)

	//region Compose
	debugImplementation(libs.androidx.compose.ui.tooling)
	implementation(libs.bundles.androidx.compose)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.mikepenz.aboutlibraries.compose)
	implementation(libs.google.accompanist.systemuicontroller)
	//endregion

	implementation(libs.androidx.startup.runtime)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.runtime.compose)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.androidx.browser)
	implementation(libs.jakewharton.timber)

	debugImplementation(libs.facebook.stetho)

	testImplementation(libs.kotest.runner.junit5)
	testImplementation(libs.kotest.assertions.core)
	testImplementation(libs.mockk)
}
