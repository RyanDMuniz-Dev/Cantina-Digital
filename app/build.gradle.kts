plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization) // Necessário para o Supabase
    alias(libs.plugins.hilt)                 // Hilt
    alias(libs.plugins.ksp)
//  alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services") // Necessário para o Firebase
}

android {
    namespace = "com.example.cantinadigital"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.cantinadigital"
        minSdk = 24
        targetSdk = 35
        // isso está dando uma cobrinha amarela
        versionCode = 1
        versionName = "1.0"

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    buildToolsVersion = "36.0.0"
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM para o app
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Bibliotecas do Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.runtime)
    implementation(libs.firebase.database)

    // Corrotinas para Firebase (.await())
    implementation(libs.kotlinx.coroutines.play.services)

    // Ícones estendidos
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    implementation(libs.ktor.client.android)

    // Hilt (Injeção de Dependência)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

//    Testes
    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

//    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}