plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("io.realm.kotlin") version "2.0.0"
    id("kotlin-kapt")
}

android {
    namespace = "com.denine.diaryapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.denine.diaryapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.google.services)
    implementation(libs.gradle.plugin)
    implementation(libs.library.base)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Firebase
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.storage.ktx)

    // Room Compose
    kapt("androidx.room:room-compiler:2.6.1")
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Runtime Compose
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Mongo DB Realm
    implementation(libs.library.sync)
    implementation(libs.kotlinx.coroutines.core)

    // Dagger Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.complier)

    // Google Auth
    implementation(libs.play.services.auth)

    // Coil
    implementation(libs.coil.compose)

    // Date-Time Picker
    implementation(libs.datetime)

    // Message Bar Compose
    implementation(libs.messagebarcompose)

    // One-Tap Compose
    implementation(libs.onetapcompose)

    // Desugar JDK
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}