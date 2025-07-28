// build.gradle.kts (:app 모듈, app 폴더 안)

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // ⭐ Firebase Google Services 플러그인 적용을 여기에 추가합니다.
    // 이 플러그인은 google-services.json 파일을 읽고 Firebase 설정을 빌드 프로세스에 주입합니다.
    id("com.google.gms.google-services") // 버전 명시 없이 ID만 사용
}

android {
    namespace = "com.example.recycling_app"
    compileSdk = 36 // 현재 설정 유지

    defaultConfig {
        applicationId = "com.example.recycling_app"
        minSdk = 33
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("io.github.ParkSangGwon:tedpermission:2.3.0")
    implementation("com.google.android.material:material:1.12.0")
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

    // ⭐ Firebase BoM (Bill of Materials) 사용 권장
    // BOM을 사용하면 모든 Firebase 라이브러리의 버전을 한 곳에서 관리할 수 있어 편리합니다.
    // libs.versions.toml에 정의된 경우 platform(libs.firebase.bom)으로 사용 가능
    implementation(platform("com.google.firebase:firebase-bom:32.0.0")) // 최신 BOM 버전을 확인하고 사용하세요 (예: 32.0.0)

    // ⭐ Firebase 인증 (FirebaseAuth) 라이브러리
    // libs.firebase.auth와 중복되므로, libs.versions.toml을 사용하고 있다면 아래 직접 선언은 제거하세요.
    // 만약 libs.versions.toml에 22.0.0으로 정의되어 있다면 그대로 두거나, BOM을 따르도록 변경할 수 있습니다.
    implementation("com.google.firebase:firebase-auth") // BOM 사용 시 버전 명시 불필요

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

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