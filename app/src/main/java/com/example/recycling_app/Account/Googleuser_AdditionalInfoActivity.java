package com.example.recycling_app.Account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.recycling_app.Network.RetrofitClient;
import com.example.recycling_app.R;
import com.example.recycling_app.StartscreenActivity;
import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.UserSignupRequest;
import com.example.recycling_app.ui.dialog.SelectionDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Googleuser_AdditionalInfoActivity extends AppCompatActivity implements SelectionDialogFragment.OnItemSelectedListener {

    private static final String TAG = "GoogleAdditionalInfo";

    private EditText editTextPassword, editTextPasswordconfirm, editTextPhone;
    private TextView textViewAge, textViewGender, textViewRegion;
    private Button buttonSignup;

    private String userEmail;
    private String userName;

    private ApiService apiService;
    private FirebaseFirestore db;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googleuser_additional_info);

        apiService = RetrofitClient.getApiService();
        db = FirebaseFirestore.getInstance();

        userEmail = getIntent().getStringExtra("email");
        userName = getIntent().getStringExtra("name");

        editTextPassword = findViewById(R.id.google_edit_text_password);
        editTextPasswordconfirm = findViewById(R.id.google_edit_text_password_confirm);
        editTextPhone = findViewById(R.id.google_edit_text_phone);
        textViewAge = findViewById(R.id.text_view_age_selection);
        textViewGender = findViewById(R.id.text_view_gender_selection);
        textViewRegion = findViewById(R.id.text_view_region_selection);
        buttonSignup = findViewById(R.id.button_signup_complete);

        // 상단바 아이콘과 글씨 색상을 어둡게 설정 (Light Mode)
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }

        textViewAge.setOnClickListener(v -> {
            ArrayList<String> ages = new ArrayList<>(generateAgeList());
            SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("나이 선택", "age", ages);
            dialogFragment.setOnItemSelectedListener(Googleuser_AdditionalInfoActivity.this);
            dialogFragment.show(getSupportFragmentManager(), "age_selection_dialog");
        });

        textViewGender.setOnClickListener(v -> {
            ArrayList<String> genders = new ArrayList<>(Arrays.asList("남성", "여성", "선택 안 함"));
            SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("성별 선택", "gender", genders);
            dialogFragment.setOnItemSelectedListener(Googleuser_AdditionalInfoActivity.this);
            dialogFragment.show(getSupportFragmentManager(), "gender_selection_dialog");
        });

        textViewRegion.setOnClickListener(v -> {
            ArrayList<String> regions = new ArrayList<>(Arrays.asList("서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원특별자치도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"));
            SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("지역 선택", "region", regions);
            dialogFragment.setOnItemSelectedListener(Googleuser_AdditionalInfoActivity.this);
            dialogFragment.show(getSupportFragmentManager(), "region_selection_dialog");
        });

        buttonSignup.setOnClickListener(v -> attemptSignup());
    }

    private List<String> generateAgeList() {
        List<String> ages = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ages.add(String.valueOf(i));
        }
        return ages;
    }

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

    private void attemptSignup() {
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextPasswordconfirm.getText().toString().trim();
        String phoneNumber = editTextPhone.getText().toString().trim();
        String ageStr = textViewAge.getText().toString().trim();
        String gender = textViewGender.getText().toString().trim();
        String region = textViewRegion.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, "비밀번호는 최소 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phoneNumber.matches("^(010|011|016|017|018|019)-?[0-9]{3,4}-?[0-9]{4}$")) {
            Toast.makeText(this, "유효한 전화번호 형식이 아닙니다.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(ageStr) || ageStr.equals("나이를 선택하세요") ||
                TextUtils.isEmpty(gender) || gender.equals("성별을 선택하세요") ||
                TextUtils.isEmpty(region) || region.equals("지역을 선택하세요")) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        UserSignupRequest signupRequest = new UserSignupRequest(
                userEmail,
                userName,
                password,
                phoneNumber,
                age,
                gender,
                region,
                true
        );

        Call<String> call = apiService.signupUser(signupRequest);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String successMessage = response.body();
                    Toast.makeText(Googleuser_AdditionalInfoActivity.this, successMessage != null ? successMessage : "회원가입 성공!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "회원가입 성공: " + signupRequest.getEmail());

                    // 백엔드 회원가입 성공 후, 동일한 데이터를 Firestore에도 저장
                    saveUserToFirestore(signupRequest);
                } else {
                    String errorMessage = "회원가입 실패: ";
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBodyString);
                            errorMessage += errorBodyString;
                        } else {
                            errorMessage += "알 수 없는 오류 발생.";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                        errorMessage += "응답 처리 중 오류 발생.";
                    }
                    Toast.makeText(Googleuser_AdditionalInfoActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "회원가입 응답 실패: HTTP " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Googleuser_AdditionalInfoActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "회원가입 요청 실패 (네트워크 오류)", t);
            }
        });
    }

    private void saveUserToFirestore(UserSignupRequest user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("name", user.getName());
        userMap.put("age", user.getAge());
        userMap.put("gender", user.getGender());
        userMap.put("region", user.getRegion());
        userMap.put("isGoogleUser", true); // 이 액티비티에서는 항상 true

        db.collection("users").document(user.getEmail())
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Firestore에 사용자 정보 저장 성공");
                    Toast.makeText(Googleuser_AdditionalInfoActivity.this, "회원가입 완료!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Googleuser_AdditionalInfoActivity.this, StartscreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore에 사용자 정보 저장 실패", e);
                    Toast.makeText(Googleuser_AdditionalInfoActivity.this, "Firestore 저장 실패. 백엔드에는 회원가입 완료되었습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Googleuser_AdditionalInfoActivity.this, StartscreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}