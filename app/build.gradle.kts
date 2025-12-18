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



        val apiSerializer = providers.environmentVariable("API_SERIALIZER")
            .orElse("moshi")
            .get()

        buildConfigField("String", "API_SERIALIZER", "\"$apiSerializer\"")

        val googleWebClientId = providers.environmentVariable("GOOGLE_WEB_CLIENT_ID")
            .orElse("CHANGE_ME")
            .get()
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
    }

    flavorDimensions += "target"

    productFlavors {
        create("emulator") {
            dimension = "target"
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"http://10.0.2.2:5002/techwizards-dado/us-central1/api/\""
            )
        }

        create("device") {
            dimension = "target"
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"http://192.168.178.23:5002/techwizards-dado/us-central1/api/\""
            )
        }
    }

    buildTypes {
        debug {
            // opcional: nada aquí
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true // o false si no quieres ofuscar
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
    androidTestImplementation(platform(libs.compose.bom))

    // Firebase BOM
    implementation(platform(libs.firebase.bom))

    // Compose UI
    implementation(libs.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navegación Compose
    implementation(libs.navigation.compose)

    // Lifecycle / Coroutines
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.coroutines.android)

    // Room + KSP
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // WorkManager / Security / JSON / DataStore / Location
    implementation(libs.work.runtime.ktx)
    implementation(libs.security.crypto)
    implementation(libs.serialization.json)
    implementation(libs.datastore.preferences)
    implementation(libs.play.services.location)

    // Base Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // --- RxJava + Room ---
    implementation(libs.room.rxjava3)
    implementation(libs.rxjava3)
    implementation(libs.rxandroid)
    implementation(libs.coroutines.rx3)

    // --- Networking (Retrofit + Moshi/Gson + OkHttp) ---
    // ⚠️ Ojo: en tu catalog puede que retrofit y retrofit.core sean el mismo.
    // Si al sync te da "duplicate class" o similar, deja SOLO uno.
    implementation(libs.retrofit)
    implementation(libs.retrofit.core)

    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin) // ✅ necesario para KotlinJsonAdapterFactory

    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)

    implementation(libs.okhttp.logging)
    testImplementation(libs.okhttp.mockwebserver)

    // Google Sign-In (solo una vez)
    implementation(libs.play.services.auth)

    // Firebase Auth (solo una vez)
    implementation(libs.firebase.auth)
    // Firebase Firestore para sincronización en tiempo real
    implementation(libs.firebase.firestore)

    // Test utils
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.2")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}



