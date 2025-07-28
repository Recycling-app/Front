package com.example.recycling_app.dto;

import com.google.gson.annotations.SerializedName; // Gson 라이브러리 사용 시, JSON 필드 이름 매핑을 위한 어노테이션

// 사용자 프로필 데이터를 담는 DTO
// 백엔드 서버의 프로필 데이터 구조와 동일하게 정의
// Retrofit 등을 통해 서버와 사용자 프로필 정보를 주고받을 때 사용
public class ProfileDTO {
    // JSON 필드 'name'과 매핑되는 사용자 이름
    @SerializedName("name")
    private String name;
    // JSON 필드 'email'과 매핑되는 사용자 이메일 주소
    @SerializedName("email")
    private String email;
    // JSON 필드 'gender'와 매핑되는 사용자 성별
    @SerializedName("gender")
    private String gender;
    // JSON 필드 'age'와 매핑되는 사용자 나이
    @SerializedName("age")
    private int age;
    // JSON 필드 'address'와 매핑되는 사용자 주소
    @SerializedName("address")
    private String address;
    // JSON 필드 'phoneNumber'와 매핑되는 사용자 전화번호
    @SerializedName("phoneNumber")
    private String phoneNumber;
    // JSON 필드 'profileImageUrl'과 매핑되는 프로필 사진 URL
    @SerializedName("profileImageUrl")
    private String profileImageUrl;
    // JSON 필드 'nickname'과 매핑되는 사용자 닉네임
    @SerializedName("nickname")
    private String nickname;
    // JSON 필드 'isProfilePublic'과 매핑되는 프로필 공개 여부 (true: 공개, false: 비공개)
    @SerializedName("isProfilePublic")
    private boolean isProfilePublic;

    // 기본 생성자:
    // GSON 라이브러리가 JSON을 객체로 변환(역직렬화)할 때 내부적으로 사용
    public ProfileDTO() {
    }

    // 모든 필드를 포함하는 생성자 (객체 생성 시 모든 데이터 설정 가능)
    public ProfileDTO(String name, String email, String gender, int age, String address, String phoneNumber, String profileImageUrl, String nickname, boolean isProfilePublic) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.nickname = nickname;
        this.isProfilePublic = isProfilePublic;
    }

    // --- Getter 메서드 ---

    // 사용자 이름 반환
    public String getName() { return name; }
    // 사용자 이메일 반환
    public String getEmail() { return email; }
    // 사용자 성별 반환
    public String getGender() { return gender; }
    // 사용자 나이 반환
    public int getAge() { return age; }
    // 사용자 주소 반환
    public String getAddress() { return address; }
    // 사용자 전화번호 반환
    public String getPhoneNumber() { return phoneNumber; }
    // 프로필 사진 URL 반환
    public String getProfileImageUrl() { return profileImageUrl; }
    // 사용자 닉네임 반환
    public String getNickname() { return nickname; }
    // 프로필 공개 여부 반환 (boolean 필드는 'is'로 시작하는 것이 관례)
    public boolean isProfilePublic() { return isProfilePublic; }

    // --- Setter 메서드 ---

    // 사용자 이름 설정
    public void setName(String name) { this.name = name; }
    // 사용자 이메일 설정
    public void setEmail(String email) { this.email = email; }
    // 사용자 성별 설정
    public void setGender(String gender) { this.gender = gender; }
    // 사용자 나이 설정
    public void setAge(int age) { this.age = age; }
    // 사용자 주소 설정
    public void setAddress(String address) { this.address = address; }
    // 사용자 전화번호 설정
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    // 프로필 사진 URL 설정
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    // 사용자 닉네임 설정
    public void setNickname(String nickname) { this.nickname = nickname; }
    // 프로필 공개 여부 설정
    public void setProfilePublic(boolean profilePublic) { isProfilePublic = profilePublic; }
}