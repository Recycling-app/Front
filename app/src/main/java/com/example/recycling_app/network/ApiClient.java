package com.example.recycling_app.Network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit API 클라이언트를 설정하고 제공하는 클래스
// 이 클래스를 통해 백엔드 서버와 통신할 Retrofit 인스턴스를 얻음.
public class ApiClient {
    // 백엔드 서버의 기본 URL
    // Android 에뮬레이터에서 '10.0.2.2'는 에뮬레이터가 실행되는 호스트 PC의 로컬호스트를 가리킴.
    private static final String BASE_URL = "http://192.168.123.109:8080";
    // Retrofit 객체 인스턴스. 앱 전체에서 단일 인스턴스를 재사용하기 위해 static으로 선언
    private static Retrofit retrofit;

    // Retrofit 클라이언트 인스턴스를 반환하는 메서드 (싱글톤 패턴)
    // 앱에서 API 요청을 보낼 때 이 메서드를 호출해서 Retrofit 객체를 얻음.
    public static Retrofit getClient() {
        // retrofit 인스턴스가 아직 생성되지 않았다면 새로 생성
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 기본 URL 설정
                    .addConverterFactory(GsonConverterFactory.create()) // JSON 데이터를 Java 객체로 변환하기 위한 Gson 컨버터 추가
                    .build(); // Retrofit 인스턴스 빌드
        }
        // 이미 생성된 retrofit 인스턴스를 반환
        return retrofit;
    }
}