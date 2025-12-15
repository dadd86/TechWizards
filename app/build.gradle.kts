plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.parcelize")
    alias(libs.plugins.google.services)
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

        val apiBaseUrl = providers.environmentVariable("API_BASE_URL")
            .orElse("https://httpbin.org/anything/")
            .get()
        val apiSerializer = providers.environmentVariable("API_SERIALIZER")
            .orElse("moshi")
            .get()
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "API_SERIALIZER", "\"$apiSerializer\"")

        val googleWebClientId = providers.environmentVariable("GOOGLE_WEB_CLIENT_ID")
            .orElse("CHANGE_ME")
            .get()
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")

        buildConfigField("String", "API_BASE_URL", "\"https://api.techwizards.dev/\"")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.7.3"
    }
    kotlinOptions { jvmTarget = "17" }

    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
}

dependencies {
    // Compose BOM
    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.ui.graphics)
    androidTestImplementation(platform(libs.compose.bom))

    // Compose UI
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
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
    implementation(libs.datastore.preferences)
    implementation(libs.play.services.location)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.auth)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Tus dependencias previas (si las necesitas)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // --- RxJava + Room ---
    implementation(libs.room.rxjava3)
    implementation(libs.rxjava3)
    implementation(libs.rxandroid)
    implementation(libs.coroutines.rx3)

    // --- Networking (Retrofit + Moshi/Gson + OkHttp) ---
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.moshi.kotlin)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)
    testImplementation(libs.okhttp.mockwebserver)

    // Test utils
    testImplementation("io.mockk:mockk:1.13.12")
    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Firebase Auth
    implementation(libs.firebase.auth)

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")



}


