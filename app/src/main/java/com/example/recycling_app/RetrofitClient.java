package com.example.recycling_app;

import android.content.Context;
import okhttp3.Interceptor; // OkHttp Interceptor import
import okhttp3.OkHttpClient; // OkHttpClient import
import okhttp3.Request; // OkHttp Request import
import okhttp3.Response; // OkHttp Response import
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.recycling_app.service.ProfileApiService;
import com.example.recycling_app.util.AuthManager;

import java.io.IOException; // IOException import

// Retrofit 클라이언트를 관리하는 싱글톤 클래스
public class RetrofitClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/"; // 에뮬레이터에서 로컬 호스트 접속용 IP
    private static Retrofit retrofit = null;
    private static ProfileApiService profileApiService = null;
    private static AuthManager authManager = null; // AuthManager 인스턴스

    // Context를 받아 RetrofitClient를 초기화하는 메서드
    // 이 메서드는 애플리케이션 시작 시 (예: Application 클래스 또는 첫 액티비티) 한 번만 호출되어야 함.
    public static void init(Context context) {
        // AuthManager가 먼저 초기화되어야 Interceptor에서 사용할 수 있음.
        if (authManager == null) {
            authManager = new AuthManager(context.getApplicationContext());
        }

        if (retrofit == null) {
            // OkHttpClient 빌더를 사용하여 Interceptor를 추가함.
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            Request.Builder requestBuilder = originalRequest.newBuilder();

                            // AuthManager를 통해 동기적으로 ID 토큰을 가져옴.
                            // Interceptor는 네트워크 요청 스레드에서 실행되므로 안전햠.
                            String idToken = authManager.getIdTokenSync();

                            // 토큰이 존재하면 Authorization 헤더에 추가함.
                            if (idToken != null && !idToken.isEmpty()) {
                                requestBuilder.header("Authorization", "Bearer " + idToken);
                            }

                            Request newRequest = requestBuilder.build();
                            return chain.proceed(newRequest);
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Interceptor가 추가된 OkHttpClient 인스턴스를 Retrofit에 설정
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (profileApiService == null) {
            profileApiService = retrofit.create(ProfileApiService.class);
        }
    }

    // ProfileApiService의 싱글톤 인스턴스를 반환
    public static ProfileApiService getProfileApiService() {
        if (profileApiService == null) {
            throw new IllegalStateException("RetrofitClient.init(Context) must be called first!");
        }
        return profileApiService;
    }

    // AuthManager의 싱글톤 인스턴스를 반환
    public static AuthManager getAuthManager() {
        if (authManager == null) {
            throw new IllegalStateException("RetrofitClient.init(Context) must be called first to initialize AuthManager!");
        }
        return authManager;
    }

    // Retrofit 인스턴스 자체를 반환 (필요시 사용)
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            throw new IllegalStateException("RetrofitClient.init(Context) must be called first!");
        }
        return retrofit;
    }
}