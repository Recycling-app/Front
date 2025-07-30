package com.example.recycling_app.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory; // ScalarsConverterFactory import
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import com.example.recycling_app.api.ApiService;

public class RetrofitClient {
    // 에뮬레이터에서 로컬 스프링 부트 서버에 접속하려면 10.0.2.2를 사용합니다.
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // 실제 서버 URL로 변경해야 합니다.

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // HTTP 요청/응답 로깅 인터셉터 설정
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // 요청 및 응답 본문 로깅

            // OkHttpClient에 인터셉터 추가
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create()) // String 응답을 먼저 처리하도록 추가
                    .addConverterFactory(GsonConverterFactory.create()) // JSON 응답 처리
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
