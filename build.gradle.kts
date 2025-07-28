plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // ⭐ Firebase Google Services 플러그인 버전 정의를 여기에 추가합니다.
    id("com.google.gms.google-services") version "4.4.2" apply false
}