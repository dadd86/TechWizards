plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
    // RxJava3 (tipos: Flowable, Single, BehaviorSubject, CompositeDisposable…)
    implementation("io.reactivex.rxjava3:rxjava:3.1.8")
    // AndroidSchedulers.mainThread() (necesario para pasar al hilo principal)
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    // (Opcional, cuando uses Room/SQLite con RxJava3)
    // implementation("androidx.room:room-rxjava3:2.6.1")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}