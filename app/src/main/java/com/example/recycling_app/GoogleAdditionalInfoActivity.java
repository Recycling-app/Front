package com.example.recycling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // 이름, 비밀번호, 전화번호는 EditText로 입력받음
import android.widget.TextView; // 나이, 성별, 지역은 TextView로 선택받음
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.GoogleSignupRequest;
import com.example.recycling_app.network.RetrofitClient;
import com.example.recycling_app.ui.dialog.SelectionDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// SelectionDialogFragment.OnItemSelectedListener 인터페이스 구현
public class GoogleAdditionalInfoActivity extends AppCompatActivity implements SelectionDialogFragment.OnItemSelectedListener {

    private static final String TAG = "GoogleAdditionalInfo";

    // UI 요소들
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextPhoneNumber;
    private TextView textViewAge;
    private TextView textViewGender;
    private TextView textViewRegion;
    private Button buttonSignupComplete;

    private ApiService apiService;

    // MainActivity에서 전달받을 데이터
    private String googleIdToken;
    private String googleEmail; // Google 계정의 이메일 (백엔드에서 사용)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_additional_info);

        apiService = RetrofitClient.getApiService();

        // Intent로부터 데이터 수신
        googleIdToken = getIntent().getStringExtra("idToken");
        googleEmail = getIntent().getStringExtra("email"); // Google 계정 이메일

        // UI 요소 연결
        editTextName = findViewById(R.id.edit_text_google_name);
        editTextPassword = findViewById(R.id.edit_text_google_password);
        editTextPhoneNumber = findViewById(R.id.edit_text_google_phone_number);
        textViewAge = findViewById(R.id.text_view_google_age_selection);
        textViewGender = findViewById(R.id.text_view_google_gender_selection);
        textViewRegion = findViewById(R.id.text_view_google_region_selection);
        buttonSignupComplete = findViewById(R.id.button_google_signup_complete);

        // 나이 선택 다이얼로그 설정
        textViewAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> ages = new ArrayList<>(generateAgeList());
                SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("나이 선택", "age", ages);
                dialogFragment.setOnItemSelectedListener(GoogleAdditionalInfoActivity.this);
                dialogFragment.show(getSupportFragmentManager(), "google_age_selection_dialog");
            }
        });

        // 성별 선택 다이얼로그 설정
        textViewGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> genders = new ArrayList<>(Arrays.asList("남성", "여성", "선택 안 함"));
                SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("성별 선택", "gender", genders);
                dialogFragment.setOnItemSelectedListener(GoogleAdditionalInfoActivity.this);
                dialogFragment.show(getSupportFragmentManager(), "google_gender_selection_dialog");
            }
        });

        // 지역 선택 다이얼로그 설정
        textViewRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> regions = new ArrayList<>(Arrays.asList("서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원특별자치도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"));
                SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("지역 선택", "region", regions);
                dialogFragment.setOnItemSelectedListener(GoogleAdditionalInfoActivity.this);
                dialogFragment.show(getSupportFragmentManager(), "google_region_selection_dialog");
            }
        });

        // 회원가입 완료 버튼 클릭 리스너
        buttonSignupComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptGoogleSignup();
            }
        });
    }

    // 나이 목록을 생성하는 헬퍼 메서드 (1세부터 100세까지)
    private List<String> generateAgeList() {
        List<String> ages = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ages.add(String.valueOf(i));
        }
        return ages;
    }

    // SelectionDialogFragment.OnItemSelectedListener 인터페이스 구현
    @Override
    public void onItemSelected(String key, String selectedItem) {
        switch (key) {
            case "age":
                textViewAge.setText(selectedItem);
                break;
            case "gender":
                textViewGender.setText(selectedItem);
                break;
            case "region":
                textViewRegion.setText(selectedItem);
                break;
        }
    }

    // Google 회원가입 시도 로직
    private void attemptGoogleSignup() {
        String name = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String ageStr = textViewAge.getText().toString().trim();
        String gender = textViewGender.getText().toString().trim();
        String region = textViewRegion.getText().toString().trim();

        // 유효성 검사
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(ageStr) || ageStr.equals("나이를 선택하세요") ||
                TextUtils.isEmpty(gender) || gender.equals("성별을 선택하세요") ||
                TextUtils.isEmpty(region) || region.equals("지역을 선택하세요")) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "비밀번호는 최소 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phoneNumber.matches("^(010|011|016|017|018|019)-?[0-9]{3,4}-?[0-9]{4}$")) {
            Toast.makeText(this, "유효한 전화번호 형식이 아닙니다 (예: 010-1234-5678 또는 01012345678)", Toast.LENGTH_LONG).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        // GoogleSignupRequest DTO 생성
        GoogleSignupRequest signupRequest = new GoogleSignupRequest(
                googleIdToken, name, password, phoneNumber, age, gender, region
        );

        // API 호출
        Call<String> call = apiService.googleSignup(signupRequest);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String successMessage = response.body();
                    Toast.makeText(GoogleAdditionalInfoActivity.this, successMessage != null ? successMessage : "Google 회원가입 성공!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Google 회원가입 성공: " + googleEmail);

                    // 회원가입 성공 후 메인 화면 또는 로그인 화면으로 이동
                    Intent intent = new Intent(GoogleAdditionalInfoActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "Google 회원가입 실패: ";
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBodyString);
                            errorMessage += errorBodyString; // 서버에서 받은 오류 메시지 표시
                        } else {
                            errorMessage += "알 수 없는 오류 발생.";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                        errorMessage += "응답 처리 중 오류 발생.";
                    }
                    Toast.makeText(GoogleAdditionalInfoActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Google 회원가입 응답 실패: HTTP " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(GoogleAdditionalInfoActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Google 회원가입 요청 실패 (네트워크 오류)", t);
            }
        });
    }
}
