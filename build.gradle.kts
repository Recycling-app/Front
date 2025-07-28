plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Firebase Google Services 플러그인 버전 정의
    id("com.google.gms.google-services") version "4.4.2" apply false
}