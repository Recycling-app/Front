// app/src/main/java/com/example/recycling_app/network/RetrofitClient.java
package com.example.recycling_app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import com.example.recycling_app.api.ApiService;
import android.os.Build; // Build 클래스 import

public class RetrofitClient {
    // 에뮬레이터에서 로컬 스프링 부트 서버에 접속하려면 10.0.2.2를 사용합니다.
    // 실제 기기에서 접속하려면 호스트 PC의 실제 로컬 IP 주소를 사용해야 합니다.
    private static final String EMULATOR_BASE_URL = "http://10.0.2.2:8080/";
    // 아래 <YOUR_LOCAL_IP_ADDRESS> 부분을 사용자 PC의 실제 로컬 IP 주소로 변경하세요.
    // 예: "http://192.168.0.100:8080/"
    private static final String REAL_DEVICE_BASE_URL = "http://<YOUR_LOCAL_IP_ADDRESS>:8080/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // 실행 환경에 따라 BASE_URL 설정
            String baseUrl;
            if (isEmulator()) {
                baseUrl = EMULATOR_BASE_URL;
            } else {
                // 실제 기기에서 테스트할 경우, 반드시 REAL_DEVICE_BASE_URL을 올바른 IP로 변경해야 합니다.
                baseUrl = REAL_DEVICE_BASE_URL;
            }

            // HTTP 요청/응답 로깅 인터셉터 설정
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // 요청 및 응답 본문 로깅

            // OkHttpClient에 인터셉터 추가
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl) // 동적으로 설정된 URL 사용
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create()) // String 응답을 먼저 처리하도록 추가
                    .addConverterFactory(GsonConverterFactory.create()) // JSON 응답 처리
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    // 현재 앱이 에뮬레이터에서 실행 중인지 확인하는 헬퍼 메서드
    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
