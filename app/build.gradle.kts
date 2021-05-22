import com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.github.triplet.play") version "3.4.0-agp7.0"
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.adarshr.test-logger") version "3.0.0"
}

// Lower version codes depleted due to testing release process
val appVersionCode = versionCode + 200
val baseVersionName = "0.1.0"
val appVersionName = "$baseVersionName.$appVersionCode"

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.svenjacobs.app.leon"
        minSdk = 21
        targetSdk = 30
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true",
                        "room.expandProjection" to "true"
                    )
                )
            }
        }
    }

    signingConfigs {
        create("release") {
            val props = Properties()
            rootProject.file("signing.properties").inputStream().use { props.load(it) }

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
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

play {
    serviceAccountCredentials.set(rootProject.file("google-play-service-account.json"))
    defaultToAppBundles.set(true)
    releaseName.set(baseVersionName)
}

testlogger {
    theme = STANDARD
}

dependencies {
    implementation(Libs.JetBrains.Kotlin.stdlib)
    implementation(Libs.AndroidX.Core.ktx)
    implementation(Libs.AndroidX.AppCompat.appcompat)
    implementation(Libs.AndroidX.Activity.ktx)
    implementation(Libs.Google.Material.material)

    //region Compose
    implementation(Libs.AndroidX.Activity.compose)
    implementation(Libs.AndroidX.Lifecycle.viewModelCompose)
    implementation(Libs.AndroidX.ConstraintLayout.compose)
    implementation(Libs.AndroidX.Compose.ui)
    implementation(Libs.AndroidX.Compose.uiTooling)
    implementation(Libs.AndroidX.Compose.material)
    implementation(Libs.AndroidX.Navigation.compose)
    //endregion

    implementation(Libs.AndroidX.Startup.runtime)
    implementation(Libs.AndroidX.Lifecycle.runtimeKtx)
    implementation(Libs.AndroidX.Lifecycle.viewModelKtx)
    implementation(Libs.JetBrains.KotlinX.Coroutines.android)
    implementation(Libs.Google.Hilt.android)
    implementation(Libs.AndroidX.Hilt.navigation)
    implementation(Libs.AndroidX.Room.ktx)
    implementation(Libs.AndroidX.Browser.browser)
    implementation(Libs.JakeWharton.Timber.timber)
    implementation(Libs.Square.Moshi.moshi)
    implementation(Libs.MikePenz.AboutLibraries.ui)

    kapt(Libs.Google.Hilt.androidCompiler)
    kapt(Libs.AndroidX.Room.compiler)

    testImplementation(Libs.Test.Kotest.runnerJunit5)
    testImplementation(Libs.Test.Kotest.assertionsCore)
    testImplementation(Libs.Test.MockK.mockk)
}
