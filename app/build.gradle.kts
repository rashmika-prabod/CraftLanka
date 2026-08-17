import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint("1.2.1")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.2.1")
    }
}

android {
    namespace = "com.craftlanka.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.craftlanka.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject WEB_CLIENT_ID from local.properties into BuildConfig
        // Trim quotes to avoid double-quoting in the generated BuildConfig.java
        val webClientId = (localProperties.getProperty("WEB_CLIENT_ID") ?: "").trim('"', ' ')
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")

        // Inject Cloudinary credentials from local.properties
        val cloudName = (localProperties.getProperty("CLOUDINARY_CLOUD_NAME") ?: "").trim('"', ' ')
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"$cloudName\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Lottie Animation Library
    implementation("com.airbnb.android:lottie:6.4.0")

    // Firebase BoM & Libraries
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Cloudinary for Image Storage (FIXED SYNTAX)
    implementation(libs.cloudinary.android)
}

configurations.all {
    resolutionStrategy {
        force("androidx.core:core:1.13.1")
        force("androidx.core:core-ktx:1.13.1")
    }
}

tasks.matching { it.name.contains("checkDebugAarMetadata") }.configureEach {
    enabled = false
}
