plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidLint)
    id("org.jetbrains.kotlin.native.cocoapods")
}

kotlin {

    androidLibrary {
        namespace = "com.example.core_ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreUI"
            isStatic = true
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "Core UI module with Google Maps support"
        homepage = "https://github.com/example/arccosmvp"
        ios.deploymentTarget = "15.4"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "CoreUI"
            isStatic = true
        }

        pod("GoogleMaps") {
            version = "8.4.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.shared)

                implementation(libs.kotlin.stdlib)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Koin for dependency injection
                implementation(libs.koin.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {

                // Google Maps
                implementation(libs.maps.compose)
                implementation(libs.play.services.maps)
                implementation(libs.play.services.location)
                
                // Koin Compose dependencies (Android only)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                // Koin Compose dependencies for iOS (excluding viewmodel which has Android deps)
                implementation(libs.koin.compose)
            }
        }
    }
}