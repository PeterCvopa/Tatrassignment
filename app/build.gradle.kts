plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.goodrequest.hiring"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.goodrequest.hiring"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            buildConfigField("String", "BASEURL", "\"https://pokeapi.co/api/v2/\"")

        }

        release {
            buildConfigField("String", "BASEURL", "\"https://pokeapi.co/api/v2/\"")
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
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    //timber
    implementation(libs.timber)

    // android
    implementation(libs.android.activity.ktx)
    implementation(libs.android.material)
    implementation(libs.swiperefreshlayout)

    // images
    implementation(libs.coil)

    // network
    implementation(libs.okhttp)
    implementation(libs.okhttp.interceptor)
    implementation(libs.retrofit.base)
    implementation(libs.retrofit.moshi)

    // lc
    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.savestate)
    ksp(libs.lifecycle.compiler)

    //hilt
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.lifecycle)
    implementation(libs.hilt.compose)

    // dagger
    implementation(libs.dagger.base)
    implementation(libs.dagger.android)
    implementation(libs.dagger.android.support)

    //compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.preview)
    implementation(libs.androidx.compose.material3)
    // pull-to-refresh
    implementation(libs.compose.material3.pullrefresh)

    // coil

    implementation(libs.coil)
}