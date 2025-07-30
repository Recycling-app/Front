package com.example.recycling_app.service;

import com.example.recycling_app.dto.RecycleData; // 재활용 데이터 DTO
import com.example.recycling_app.dto.User;       // 사용자 정보 DTO

import retrofit2.Call;       // Retrofit 비동기 통신 객체
import retrofit2.http.GET;   // HTTP GET 요청 정의 어노테이션
import retrofit2.http.POST;  // HTTP POST 요청 정의 어노테이션
import retrofit2.http.Body;  // HTTP 요청 본문(body)에 객체 포함 어노테이션

// 백엔드 API와의 통신을 위한 메서드 정의 인터페이스
// Retrofit 라이브러리가 이 인터페이스를 구현하여 실제 네트워크 요청 처리
public interface ApiService {

    // --- 사용자 정보 관련 API ---

    // GET 요청: "/user/info" 경로로 사용자 정보 가져오기
    // 백엔드에서 User DTO 형식으로 응답 예상
    @GET("/user/info")
    Call<User> getUserInfo(); // User 객체를 담은 Call 객체 반환

    // --- 재활용 데이터 관련 API ---

    // POST 요청: "/recycle/submit" 경로로 재활용 데이터 전송
    // @Body 어노테이션 사용, RecycleData 객체가 HTTP 요청 본문에 JSON 형태로 포함
    // 서버로부터 특별한 응답 데이터 없이 성공 여부만 받으므로 Call<Void> 사용
    @POST("/recycle/submit")
    Call<Void> submitRecycleData(@Body RecycleData data); // RecycleData 객체를 본문에 담아 전송
}