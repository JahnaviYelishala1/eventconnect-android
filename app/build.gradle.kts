plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    // Apply Firebase plugin here
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eventconnect"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.eventconnect"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    // Core + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // 🔥 Firebase Authentication
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")

    // 🔥 ViewModel for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // 🔥 Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// OkHttp (optional but recommended)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.28.0")
    implementation("androidx.compose.foundation:foundation-layout:1.5.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    implementation("androidx.compose.animation:animation:<latest_version>")

}
