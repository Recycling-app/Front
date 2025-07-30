package com.example.recycling_app.Profile.accountmanagement;

import androidx.annotation.NonNull; // null이 아님을 명시하는 어노테이션
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log; // 로그 메시지 출력
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycling_app.R;
import com.example.recycling_app.dto.ProfileDTO; // 사용자 프로필 데이터 전송 객체 (DTO) 임포트
import com.example.recycling_app.service.ProfileApiService; // 프로필 관련 API 서비스 인터페이스 임포트
import com.example.recycling_app.Network.RetrofitClient; // Retrofit 클라이언트 (API 서비스 인스턴스 생성) util 패키지 확인

import com.google.firebase.auth.FirebaseAuth; // Firebase 인증 import
import com.google.firebase.auth.FirebaseUser; // Firebase 사용자 import
import com.google.android.gms.tasks.OnCompleteListener; // OnCompleteListener import
import com.google.android.gms.tasks.Task; // Task import

import java.util.HashMap; // 해시 맵 (키-값 쌍 저장)
import java.util.Map; // 맵 인터페이스

import retrofit2.Call; // Retrofit의 비동기 네트워크 요청 객체
import retrofit2.Callback; // Retrofit의 비동기 응답 처리 콜백
import retrofit2.Response; // Retrofit의 HTTP 응답 객체

// 개인 정보 수정 기능을 담당하는 액티비티
// 사용자의 이름, 이메일, 전화번호, 주소, 나이, 성별 등 개인 정보를 조회하고 수정할 수 있도록 함
public class PersonalInfoEditActivity extends AppCompatActivity {

    // UI 요소들을 위한 변수 선언
    private ImageView backArrowIcon; // 뒤로가기 화살표 아이콘
    private TextView personalInfoEditTitle; // 화면 제목 텍스트
    private EditText etName; // 이름 입력 필드
    private EditText etEmail; // 이메일 입력 필드
    private EditText etPhone; // 전화번호 입력 필드
    private EditText etAddress; // 주소 입력 필드
    private EditText etAge; // 나이 입력 필드
    private Spinner spinnerGender; // 성별 선택을 위한 스피너
    private ImageView ivGenderDropdownArrow; // 성별 스피너 드롭다운을 트리거하는 화살표 이미지 뷰

    private Button btnCancel; // 취소 버튼
    private Button btnConfirm; // 확인 (개인 정보 저장) 버튼

    // 현재 로드된 프로필 데이터를 저장하여, 부분 업데이트 시 기존 값을 유지하는 데 사용
    private ProfileDTO currentProfileData;

    // Firebase 인증 객체 선언
    private FirebaseAuth mAuth;
    private String currentUid; // 실제 Firebase UID
    private String firebaseIdToken; // Firebase ID 토큰

    private static final String TAG = "PersonalInfoEditAct"; // 로그 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_edit); // 이 액티비티에 연결될 레이아웃 파일 지정

        // UI 요소 초기화 (XML 레이아웃의 ID와 연결)
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        personalInfoEditTitle = findViewById(R.id.personal_info_edit_title);
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etAge = findViewById(R.id.et_age);
        spinnerGender = findViewById(R.id.spinner_gender);
        ivGenderDropdownArrow = findViewById(R.id.iv_gender_dropdown_arrow);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);

        // 성별 스피너 설정
        String[] genders = {"남자", "여자"}; // 스피너에 표시될 성별 옵션
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        // 뒤로가기 아이콘 클릭 리스너 설정
        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onBackPressed(); // 이전 화면으로 돌아가기
            }
        });

        // 화살표 ImageView 클릭 리스너
        ivGenderDropdownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerGender.performClick();
            }
        });

        // 성별 Spinner 선택 리스너 (선택 항목 변경 시 로직 필요X)
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 취소 버튼 클릭 리스너
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PersonalInfoEditActivity.this, "개인 정보 수정이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish(); // 현재 액티비티 종료
            }
        });

        // 확인 버튼 클릭 리스너 (개인 정보 변경 사항을 백엔드에 저장)
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePersonalInfoChanges(); // 개인 정보 변경 사항 저장 메서드 호출
            }
        });

        // Firebase 인증 객체 초기화
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 사용자 로그인 상태 확인 및 UID, ID 토큰 가져오기
        if (currentUser != null) {
            currentUid = currentUser.getUid();
            Log.d(TAG, "Current UID: " + currentUid);
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                firebaseIdToken = task.getResult().getToken();
                                Log.d(TAG, "Firebase ID Token acquired: " + (firebaseIdToken != null ? firebaseIdToken.substring(0, 30) + "..." : "null"));
                                // 토큰 획득 성공. 이제 프로필 데이터를 로드함.
                                loadPersonalInfo();
                            } else {
                                Log.e(TAG, "Firebase ID 토큰 가져오기 실패", task.getException());
                                Toast.makeText(PersonalInfoEditActivity.this, "인증 정보 가져오기 실패", Toast.LENGTH_SHORT).show();
                                finish(); // 오류 시 액티비티 종료
                            }
                        }
                    });
        } else {
            Log.w(TAG, "사용자가 로그인되지 않았습니다. 개인 정보를 로드할 수 없습니다.");
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish(); // 로그인되지 않은 경우 액티비티 종료
        }
    }

    // 백엔드에서 현재 로그인된 사용자의 개인 정보 데이터를 로드하는 메서드
    private void loadPersonalInfo() {
        // UID 또는 토큰이 없는 경우 처리 (방어적 코드)
        if (currentUid == null || firebaseIdToken == null) {
            Log.w(TAG, "loadPersonalInfo: UID 또는 Firebase ID 토큰이 null입니다. API 호출을 건너뜜.");
            Toast.makeText(this, "인증 정보가 없어 개인 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProfileApiService apiService = RetrofitClient.getProfileApiService();
        String authHeader = "Bearer " + firebaseIdToken; // Authorization 헤더 생성

        // getProfile API 호출 시 Authorization 헤더 전달
        apiService.getProfile(currentUid).enqueue(new Callback<ProfileDTO>() {
            @Override
            public void onResponse(@NonNull Call<ProfileDTO> call, @NonNull Response<ProfileDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProfileData = response.body();
                    etName.setText(currentProfileData.getName());
                    etEmail.setText(currentProfileData.getEmail());
                    etPhone.setText(currentProfileData.getPhoneNumber());
                    etAddress.setText(currentProfileData.getAddress());
                    etAge.setText(String.valueOf(currentProfileData.getAge()));

                    String gender = currentProfileData.getGender();
                    if (gender != null) {
                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGender.getAdapter();
                        int spinnerPosition = adapter.getPosition(gender);
                        if (spinnerPosition != -1) { // 스피너에 해당 성별 옵션이 있는지 확인
                            spinnerGender.setSelection(spinnerPosition);
                        } else {
                            Log.w(TAG, "로드된 성별 '" + gender + "'이/가 스피너 옵션에 없습니다.");
                        }
                    }
                    Toast.makeText(PersonalInfoEditActivity.this, "개인 정보 로드 성공", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "개인 정보 로드 성공.");
                } else {
                    String errorMessage = "개인 정보 로드 실패";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        }
                    }
                    Log.e(TAG, "개인 정보 로드 실패: " + response.code() + " " + errorMessage);
                    Toast.makeText(PersonalInfoEditActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileDTO> call, @NonNull Throwable t) {
                Log.e(TAG, "네트워크 오류: " + t.getMessage(), t);
                Toast.makeText(PersonalInfoEditActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 개인 정보 변경 사항을 백엔드에 저장하는 메서드 (PATCH 요청 사용)
    private void savePersonalInfoChanges() {
        // UID 또는 토큰이 없는 경우 처리 (방어적 코드)
        if (currentUid == null || firebaseIdToken == null) {
            Log.w(TAG, "savePersonalInfoChanges: UID 또는 Firebase ID 토큰이 null입니다. API 호출을 건너뜜.");
            Toast.makeText(this, "인증 정보가 없어 개인 정보를 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        int age = 0;
        try {
            String ageText = etAge.getText().toString().trim();
            if (!ageText.isEmpty()) {
                age = Integer.parseInt(ageText);
            } else {
                Toast.makeText(this, "나이를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "나이를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String gender = spinnerGender.getSelectedItem().toString();

        if (currentProfileData != null) {
            // 변경된 필드만 Map에 추가
            if (!name.equals(currentProfileData.getName())) updates.put("name", name);
            if (!email.equals(currentProfileData.getEmail())) updates.put("email", email);
            if (!phone.equals(currentProfileData.getPhoneNumber())) updates.put("phoneNumber", phone);
            if (!address.equals(currentProfileData.getAddress())) updates.put("address", address);
            if (age != currentProfileData.getAge()) updates.put("age", age);
            if (!gender.equals(currentProfileData.getGender())) updates.put("gender", gender);
        } else {
            // currentProfileData가 null인 경우 모든 필드를 추가
            updates.put("name", name);
            updates.put("email", email);
            updates.put("phoneNumber", phone);
            updates.put("address", address);
            updates.put("age", age);
            updates.put("gender", gender);
            Log.w(TAG, "currentProfileData가 null이므로 모든 필드를 업데이트 Map에 추가합니다.");
        }

        if (updates.isEmpty()) {
            Toast.makeText(this, "변경된 내용이 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ProfileApiService apiService = RetrofitClient.getProfileApiService();
        String authHeader = "Bearer " + firebaseIdToken; // Authorization 헤더 생성

        // updateProfileFields API 호출 시 Authorization 헤더 전달
        apiService.updateProfileFields(currentUid, updates).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PersonalInfoEditActivity.this, "개인 정보가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "개인 정보 업데이트 성공.");
                    finish();
                } else {
                    String errorMessage = "개인 정보 업데이트 실패";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += ": " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        }
                    }
                    Log.e(TAG, "개인 정보 업데이트 실패: " + response.code() + " " + errorMessage);
                    Toast.makeText(PersonalInfoEditActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e(TAG, "네트워크 오류: " + t.getMessage(), t);
                Toast.makeText(PersonalInfoEditActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}