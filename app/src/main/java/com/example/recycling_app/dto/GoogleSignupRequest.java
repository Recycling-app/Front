package com.example.recycling_app.dto;

import com.google.gson.annotations.SerializedName;

public class GoogleSignupRequest {
    @SerializedName("idToken")
    private String idToken;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password; // Google 회원가입 시 비밀번호 받음
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("age")
    private int age;
    @SerializedName("gender")
    private String gender;
    @SerializedName("region")
    private String region;

    // 모든 필드를 포함하는 생성자
    public GoogleSignupRequest(String idToken, String name, String password, String phoneNumber, int age, String gender, String region) {
        this.idToken = idToken;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.gender = gender;
        this.region = region;
    }

    public String getIdToken() {
        return idToken;
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
