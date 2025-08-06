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

    @SerializedName("isGoogleUser")
    private boolean isGoogleUser;

    public UserSignupRequest(String userEmail, String userName, String userPhoneNumber, int age, String gender, String region, boolean b) {
        this.email = userEmail;
        this.name = userName;
        this.phoneNumber = userPhoneNumber;
        this.age = age;
        this.gender = gender;
        this.region = region;
        this.isGoogleUser = isGoogleUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isGoogleUser() {
        return isGoogleUser;
    }
}