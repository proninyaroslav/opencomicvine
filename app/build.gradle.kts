import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "org.proninyaroslav.opencomicvine"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.proninyaroslav.opencomicvine"
        minSdk = 21
        targetSdk = 35
        versionCode = 5
        versionName = "1.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keyProperties = File("${rootProject.projectDir}/key.properties").let {
                if (it.exists) {
                    it.loadProperties()
                } else {
                    null
                }
            }

            storeFile = keyProperties?.getProperty("storeFile")?.let { file(it) }
            keyPassword = keyProperties?.getProperty("keyPassword")
            storePassword = keyProperties?.getProperty("storePassword")
            keyAlias = keyProperties?.getProperty("keyAlias")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

dependencies {
    val navVersion: String by project
    val roomVersion: String by project
    val retrofitVersion: String by project
    val okhttpVersion: String by project
    val moshiVersion: String by project
    val moshiSealedVersion: String by project
    val accompanistVersion: String by project
    val acraVersion: String by project
    val sandwichVersion: String by project
    val hiltVersion: String by project

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.room:room-testing:$roomVersion")
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.8")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // AndroidX Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Android Material View
    implementation("com.google.android.material:material:1.12.0")

    // Compose Core
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-util")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Fallback import for components that aren"t in Material 3
    implementation("androidx.compose.material:material")

    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Paging 3.0
    implementation("androidx.paging:paging-compose:3.3.6")
    implementation("androidx.room:room-paging:$roomVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.2")

    // Accompanist
    implementation("com.google.accompanist:accompanist-webview:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")

    // Moshi
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // moshi-sealed
    // TODO: https://github.com/ZacSweers/MoshiX/issues/530
    //noinspection GradleDependency
    implementation("dev.zacsweers.moshix:moshi-sealed-runtime:$moshiSealedVersion")
    //noinspection GradleDependency
    ksp("dev.zacsweers.moshix:moshi-sealed-codegen:$moshiSealedVersion")

    // ACRA
    implementation("ch.acra:acra-mail:$acraVersion")
    implementation("ch.acra:acra-dialog:$acraVersion")

    // Other
    implementation("com.github.skydoves:sandwich:$sandwichVersion")
    implementation("com.github.skydoves:sandwich-retrofit:$sandwichVersion")
    implementation("com.github.SmartToolFactory:Compose-Image:1.2.2")
    implementation("com.github.alorma:compose-settings-ui-m3:1.0.3")
    implementation("io.github.fornewid:placeholder-material3:1.0.1")
}