package com.example.recycling_app.dto;

import com.google.gson.annotations.SerializedName;

// 백엔드의 UserSignupRequest DTO와 필드 이름 및 타입이 일치해야 합니다.
public class UserSignupRequest {
    @SerializedName("email") // JSON 키와 필드 이름이 다를 경우 사용
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
    @SerializedName("phoneNumber") // 백엔드 필드명 확인
    private String phoneNumber;
    @SerializedName("age")
    private int age;
    @SerializedName("gender")
    private String gender;
    @SerializedName("region")
    private String region;

    // 모든 필드를 포함하는 생성자
    // 백엔드 DTO의 일반적인 순서 (email, name, password)에 맞춰 조정
    public UserSignupRequest(String email, String name, String password, String phoneNumber, int age, String gender, String region) {
        this.email = email;
        this.name = name; // 이름
        this.password = password; // 비밀번호
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.region = region;
    }

    // Getter
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getRegion() {
        return region;
    }
}
