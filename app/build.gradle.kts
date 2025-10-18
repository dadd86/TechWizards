plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.diegodiaz.techwizards"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.diegodiaz.techwizards"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui.graphics)
    androidTestImplementation(platform(libs.compose.bom))

    // Compose UI
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)

    // Navegación Compose
    implementation(libs.navigation.compose)

    // Lifecycle / Coroutines
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.coroutines.android)

    // Room + KSP
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    debugImplementation(libs.androidx.ui.test.manifest)
    ksp(libs.room.compiler)

    // WorkManager / Security / JSON
    implementation(libs.work.runtime.ktx)
    implementation(libs.security.crypto)
    implementation(libs.serialization.json)

    // Tus dependencias previas (si las necesitas)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
        // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

