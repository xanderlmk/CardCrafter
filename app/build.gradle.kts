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
    namespace = "com.example.flashcards"
    // For sdk 34 just change compileSdk and targetSdk.
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.flashcards"
        minSdk = 25
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
            "String",
            "SUPABASE_URL", "\"" + "${gradleLocalProperties(rootDir, providers)
                .getProperty("SUPABASE_URL", "")}" + "\""
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY", "\"" + "${gradleLocalProperties(rootDir, providers)
                .getProperty("SUPABASE_KEY", "")}" + "\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Delete this kotlin section to run on Koala version
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17)) // or 1.8 if using an older JDK
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


    // Jetpack Compose Integration
    implementation(libs.androidx.navigation.compose)

    // to use https.
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // Views/Fragments Integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Feature module support for Fragments
    implementation(libs.androidx.navigation.dynamic.features.fragment)

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
