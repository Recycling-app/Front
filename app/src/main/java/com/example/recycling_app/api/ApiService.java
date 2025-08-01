// app/src/main/java/com/example/recycling_app/api/ApiService.java
package com.example.recycling_app.api;

import com.example.recycling_app.dto.FindEmailRequest;
import com.example.recycling_app.dto.FindEmailResponse;
//import com.example.recycling_app.dto.GoogleLoginRequest;
//import com.example.recycling_app.dto.GoogleSignupRequest;
import com.example.recycling_app.dto.JwtLoginResponse;
import com.example.recycling_app.dto.LoginRequest;
import com.example.recycling_app.dto.ResetPasswordRequest;
import com.example.recycling_app.dto.UserSignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 회원가입 API 엔드포인트
    @POST("api/auth/signup")
    Call<String> signupUser(@Body UserSignupRequest request);

    // 일반 로그인 API 엔드포인트
    @POST("api/auth/login")
    Call<JwtLoginResponse> loginUser(@Body LoginRequest request);

//    // Google 회원가입 API 엔드포인트 (백엔드 AuthController에 정의됨)
//    @POST("api/auth/google/signup")
//    Call<String> googleSignup(@Body GoogleSignupRequest request);
//
//    // Google 로그인 API 엔드포인트 (백엔드 AuthController에 정의됨)
//    @POST("api/auth/google/login")
//    Call<JwtLoginResponse> googleLogin(@Body GoogleLoginRequest request);

    // 아이디(이메일) 찾기 API 엔드포인트
    @POST("api/auth/find-email")
    Call<FindEmailResponse> findEmail(@Body FindEmailRequest request);

    // 비밀번호 재설정 API 엔드포인트
    @POST("api/auth/reset-password")
    Call<String> resetPassword(@Body ResetPasswordRequest request);
}
