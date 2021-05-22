object Versions {
    const val compose = "1.0.0-beta07"
}

object Libs {

    object Android {
        const val plugin = "com.android.tools.build:gradle:7.0.0-beta02"
    }

    object AndroidX {

        object Core {
            const val ktx = "androidx.core:core-ktx:1.5.0"
        }

        object AppCompat {
            const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        }

        object Activity {
            private const val version = "1.3.0-alpha08"

            const val ktx = "androidx.activity:activity-ktx:$version"
            const val compose = "androidx.activity:activity-compose:$version"
        }

        object Compose {
            const val ui = "androidx.compose.ui:ui:${Versions.compose}"
            const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
            const val material = "androidx.compose.material:material:${Versions.compose}"
        }

        object Lifecycle {
            private const val version = "2.3.1"

            const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val viewModelCompose =
                "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha05"
        }

        object ConstraintLayout {
            const val compose = "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha07"
        }

        object Room {
            private const val version = "2.3.0"

            const val ktx = "androidx.room:room-ktx:$version"
            const val compiler = "androidx.room:room-compiler:$version"
        }

        object Navigation {
            const val compose = "androidx.navigation:navigation-compose:2.4.0-alpha01"
        }

        object Browser {
            const val browser = "androidx.browser:browser:1.3.0"
        }

        object Hilt {
            const val navigation = "androidx.hilt:hilt-navigation:1.0.0"
        }

        object Startup {
            const val runtime = "androidx.startup:startup-runtime:1.0.0"
        }
    }

    object Google {

        object Material {
            const val material = "com.google.android.material:material:1.3.0"
        }

        object Hilt {
            private const val version = "2.35.1"

            const val plugin = "com.google.dagger:hilt-android-gradle-plugin:$version"
            const val android = "com.google.dagger:hilt-android:$version"
            const val androidCompiler = "com.google.dagger:hilt-android-compiler:$version"
        }
    }

    object JetBrains {

        object Kotlin {
            private const val version = "1.4.32"

            const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
            const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        }

        object KotlinX {

            object Coroutines {
                const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0"
            }
        }
    }

    object Square {

        object Moshi {
            const val moshi = "com.squareup.moshi:moshi:1.12.0"
        }
    }

    object JakeWharton {

        object Timber {
            const val timber = "com.jakewharton.timber:timber:4.7.1"
        }
    }

    object MikePenz {

        object AboutLibraries {
            private const val version = "8.8.6"

            const val plugin = "com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:$version"
            const val ui = "com.mikepenz:aboutlibraries:$version"
        }
    }

    object Test {

        object Kotest {
            private const val version = "4.6.0"

            const val runnerJunit5 = "io.kotest:kotest-runner-junit5:$version"
            const val assertionsCore = "io.kotest:kotest-assertions-core:$version"
        }

        object MockK {
            const val mockk = "io.mockk:mockk:1.11.0"
        }
    }
}
