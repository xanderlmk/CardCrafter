@file:Suppress("PropertyName")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.parcelize)
}

android {
    namespace = "com.belmontCrest.cardCrafter"
    // For sdk 34 just change compileSdk and targetSdk.
    compileSdk = 35

    defaultConfig {
        applicationId = "com.belmontCrest.cardCrafter"
        minSdk = 26
        targetSdk = 35
        versionCode = 12
        versionName = "1.0.5"
        ndk { this.debugSymbolLevel = "SYMBOL_TABLE" }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
            "String",
            "SUPABASE_URL", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SUPABASE_URL", "") + "\""
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SUPABASE_KEY", "") + "\""
        )
        buildConfigField(
            "String",
            "SB_DECK_TN", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SB_DECK_TN", "") + "\""
        )
        buildConfigField(
            "String",
            "SB_CARD_TN", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SB_CARD_TN", "") + "\""
        )
        buildConfigField(
            "String",
            "SB_OWNER_TN", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SB_OWNER_TN", "") + "\""
        )
        buildConfigField(
            "String",
            "SB_CTD_TN", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SB_CTD_TN", "") + "\""
        )
        buildConfigField(
            "String",
            "SB_DACO_TN", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SB_DACO_TN", "") + "\""
        )
        buildConfigField(
            "String",
            "SYNCED_SB_URL", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SYNCED_SB_URL", "") + "\""
        )
        buildConfigField(
            "String",
            "SYNCED_SB_KEY", "\"" + gradleLocalProperties(rootDir, providers)
                .getProperty("SYNCED_SB_KEY", "") + "\""
        )

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }
    sourceSets {
        this["androidTest"].assets.srcDir("$projectDir/schemas")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk { this.debugSymbolLevel = "SYMBOL_TABLE" }
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }
    flavorDimensions += "version"
    productFlavors {
        create("normal") {
            dimension = "version"
            versionNameSuffix = "-normal"
        }
        create("demo") {
            applicationIdSuffix = ".demo"
            dimension = "version"
            versionNameSuffix = "-demo"
        }
        create("full") {
            applicationIdSuffix = ".full"
            dimension = "version"
            versionNameSuffix = "-full"
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Delete this kotlin section to run on Koala version
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }


}


dependencies {

    /** Possibly to use in the future.
    implementation (libs.hilt.android)
    annotationProcessor (libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
     */

    implementation(libs.androidx.foundation.android)

    implementation(libs.accompanist.swiperefresh)

    implementation(libs.slf4j.nop)
    // Jetpack Compose Integration
    implementation(libs.androidx.navigation.compose)

    // to use https.
    implementation(libs.converter.gson)

    // Views/Fragments Integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    implementation(libs.material)
    implementation(libs.androidx.activity)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)


    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.bcrypt)

    // Room database
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    androidTestImplementation(libs.androidx.room.testing)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    implementation(libs.kotlin.stdlib.jdk7)

    implementation(libs.androidx.constraintlayout)

    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)

    androidTestImplementation(libs.androidx.arch.core.testing)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Network Image
    implementation(libs.coil.compose)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.realtime.kt)
    implementation(libs.postgrest.kt)
    implementation(libs.gotrue.kt)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.cio)

    // Google
    implementation(libs.androidx.credentials)
    implementation(libs.googleid)
    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation(libs.androidx.credentials.play.services.auth)

}
