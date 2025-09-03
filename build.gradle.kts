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

import com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jmailen.gradle.kotlinter.tasks.LintTask

buildscript {
	repositories {
		google()
		mavenCentral()
	}

	dependencies {
		classpath(libs.android.gradle.plugin)
		classpath(libs.kotlin.gradle.plugin)
		classpath(libs.compose.gradle.plugin)
	}
}

plugins {
	alias(libs.plugins.ben.manes.versions)
	alias(libs.plugins.kotlinter)
	alias(libs.plugins.adarshr.test.logger)
	alias(libs.plugins.aboutlibraries) apply false
}

subprojects {
	apply(plugin = "org.jmailen.kotlinter")
	apply(plugin = "com.adarshr.test-logger")

	repositories {
		google()
		mavenCentral()
	}

	testlogger {
		theme = STANDARD
	}

	tasks.withType<LintTask>().configureEach {
		exclude { it.file.path.contains("/build/generated/") }
	}

	tasks.withType<KotlinCompile>().configureEach {
		compilerOptions {
			freeCompilerArgs.addAll(
				"-opt-in=kotlin.RequiresOptIn",
				"-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
				"-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
			)
		}
	}
}

tasks.register<Delete>("clean") {
	delete = setOf(rootProject.layout.buildDirectory)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {

	fun isNonStable(version: String) =
		listOf("alpha", "beta", "rc", "eap", "-m", ".m", "-a", "dev").any {
			version.lowercase().contains(it)
		}

	rejectVersionIf {
		isNonStable(candidate.version) && !isNonStable(currentVersion)
	}
}
