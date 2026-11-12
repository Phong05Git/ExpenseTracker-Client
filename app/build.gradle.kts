
plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.expensetracker"
    compileSdk = 37

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.expensetracker"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
        }
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"http://10.0.2.2:5080/\""
            )

            buildConfigField(
                "boolean",
                "API_PINNING_ENABLED",
                "false"
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://expensetracker-api-b1xz.onrender.com/\""
            )

            buildConfigField(
                "boolean",
                "API_PINNING_ENABLED",
                "true"
            )

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("securityTest") {
            initWith(getByName("release"))
            isDebuggable = true
            applicationIdSuffix = ".securitytest"
            versionNameSuffix = "-securityTest"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.fragment)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    implementation(libs.hilt.android)
    annotationProcessor(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.rxjava3)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    implementation(libs.tink.android)
    implementation(libs.mpandroidchart)

    testImplementation(libs.junit)

    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.junit)
}
