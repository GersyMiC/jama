// app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.gms.google-services") // Este no suele tener alias si la versión está en el id

}

android {
    namespace = "com.example.jama_fv"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.jama_fv"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ViewModel y LiveData (Android Architecture Components)
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // <-- Usa alias
    implementation(libs.androidx.lifecycle.livedata.ktx)   // <-- Usa alias
    // Fragmentos (para usar Fragmentos en tus actividades)
    implementation(libs.androidx.fragment.ktx) // <-- Usa alias
    // Coroutines (para manejar operaciones asíncronas de manera eficiente)
    implementation(libs.kotlinx.coroutines.core) // <-- Usa alias
    implementation(libs.kotlinx.coroutines.android) // <-- Usa alias
    // Firebase y mapas
    implementation(platform("com.google.firebase:firebase-bom:33.16.0")) // Esta línea está bien
    implementation(libs.play.services.maps) // <-- Usa alias
    implementation(libs.play.services.location) // <-- Usa alias
    implementation("com.google.firebase:firebase-firestore-ktx") // Estos no tienen alias en tu toml
    implementation("com.google.firebase:firebase-auth-ktx") // Estos no tienen alias en tu toml
    implementation("com.google.firebase:firebase-storage-ktx") // Estos no tienen alias en tu toml



    // Glide (para cargar imágenes fácilmente)
    implementation(libs.glide) // <-- Usa alias
    kapt(libs.glide.compiler)





}