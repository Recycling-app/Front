// build.gradle.kts (:app 모듈, app 폴더 안)
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Firebase Google Services 플러그인 적용
    id("com.google.gms.google-services")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {

    namespace = "com.example.recycling_app"
    compileSdk = 36 // 현재 설정 유지

    signingConfigs {
        create("release") {
            storeFile = file(property("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = property("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = property("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = property("MYAPP_RELEASE_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            // 여기에 작동할 기기의 IP 주소 입력하기
            buildConfigField("String", "BASE_URL", "\"http://192.168.123.114:8080/\"")
        }
        getByName("release") {
            buildConfigField("String", "BASE_URL", "\"http://192.168.123.114:8080/\"")
        }
    }

    defaultConfig {
        applicationId = "com.example.recycling_app"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties["GEMINI_API_KEY"]}\"")
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
        viewBinding = true
    }
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

dependencies {
    implementation(libs.androidx.room.compiler)
    implementation(libs.firebase.database)
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
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("androidx.fragment:fragment:1.6.0")
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
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
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

    // Firebase 추가
    implementation(platform("com.google.firebase:firebase-bom:33.10.0")) // Firebase BoM 사용 권장
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth") // firebase-auth 포함
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.firebaseui:firebase-ui-storage:8.0.2")

    // Google Services
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Retrofit 및 GSON 의존성
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp 로깅 인터셉터 (네트워크 요청 로깅용)
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // CircleImageView 라이브러리
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Glide 라이브러리 (이미지 로딩 및 표시용)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
}

//.apk 자동 삭제 코드
afterEvaluate {
    tasks.matching { it.name.startsWith("assemble") }.configureEach {
        doLast {
            val apkDir = file("$buildDir/outputs/apk")
            if (apkDir.exists()) {
                apkDir.deleteRecursively()
                println("APK 파일이 자동으로 삭제되었습니다: $apkDir")
            }
        }
    }
}