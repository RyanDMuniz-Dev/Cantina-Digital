plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization) // Necessário para o Supabase
    alias(libs.plugins.hilt)                 // Hilt
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.android)

    id("com.google.gms.google-services") // Necessário para o Firebase
}

android {
    namespace = "com.example.cantinadigital"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.cantinadigital"
        minSdk = 24
        targetSdk = 35 // isso está dando uma cobrinha amarela
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir, providers)

        buildConfigField("String", "SUPABASE_URL", "\"${localProperties["SUPABASE_URL"]}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties["SUPABASE_ANON_KEY"]}\"")

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
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))

    // Bibliotecas do Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.runtime)
    implementation(libs.firebase.database)

    // Corrotinas para Firebase (.await())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    // Ícones estendidos
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.ktor.client.android)

    // Hilt (Injeção de Dependência)
    implementation(libs.hilt.android)
    implementation(libs.core.ktx)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Aplica a BOM do Compose também nos testes para resolver a versão do ui-test-junit4
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}