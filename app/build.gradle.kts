import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id ("com.google.gms.google-services")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(property("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = property("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = property("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = property("MYAPP_RELEASE_KEY_PASSWORD") as String
        }
    }

    namespace = "com.example.recycling_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.recycling_app"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties["GEMINI_API_KEY"]}\"")
    }

    buildTypes {
        getByName("release") {
            // 생성한 release 서명 설정을 release 빌드 타입에 적용
            signingConfig = signingConfigs.getByName("release")
            // ...
        }
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(libs.androidx.room.compiler)
    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.2")
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.pytorch:pytorch_android_lite:2.1.0")
    implementation("org.pytorch:pytorch_android_torchvision_lite:2.1.0")
    implementation("com.kakao.maps.open:android:2.12.8")
    implementation("com.kakao.sdk:v2-common:2.19.0")
    implementation("io.github.ParkSangGwon:tedpermission:2.3.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.exifinterface)
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.media3.common)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}