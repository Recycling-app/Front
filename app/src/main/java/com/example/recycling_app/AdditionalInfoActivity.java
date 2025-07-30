package com.example.recycling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.UserSignupRequest;
import com.example.recycling_app.network.RetrofitClient;
import com.example.recycling_app.ui.dialog.SelectionDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdditionalInfoActivity extends AppCompatActivity implements SelectionDialogFragment.OnItemSelectedListener {

    private static final String TAG = "AdditionalInfoActivity";

    private TextView textViewAge;
    private TextView textViewGender;
    private TextView textViewRegion;
    private Button buttonSignup;

    private ApiService apiService;

    private String userEmail;
    private String userPassword;
    private String userName;
    private String userPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        apiService = RetrofitClient.getApiService();

        userEmail = getIntent().getStringExtra("email");
        userPassword = getIntent().getStringExtra("password");
        userName = getIntent().getStringExtra("name");
        userPhoneNumber = getIntent().getStringExtra("phoneNumber");

        textViewAge = findViewById(R.id.text_view_age_selection);
        textViewGender = findViewById(R.id.text_view_gender_selection);
        textViewRegion = findViewById(R.id.text_view_region_selection);
        buttonSignup = findViewById(R.id.button_signup_complete);

        textViewAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> ages = new ArrayList<>(generateAgeList());
                SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("나이 선택", "age", ages);
                dialogFragment.setOnItemSelectedListener(AdditionalInfoActivity.this);
                dialogFragment.show(getSupportFragmentManager(), "age_selection_dialog");
            }
        });

        textViewGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> genders = new ArrayList<>(Arrays.asList("남성", "여성", "선택 안 함"));
                SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("성별 선택", "gender", genders);
                dialogFragment.setOnItemSelectedListener(AdditionalInfoActivity.this);
                dialogFragment.show(getSupportFragmentManager(), "gender_selection_dialog");
            }
        });

        textViewRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> regions = new ArrayList<>(Arrays.asList("서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원특별자치도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"));
                SelectionDialogFragment dialogFragment = SelectionDialogFragment.newInstance("지역 선택", "region", regions);
                dialogFragment.setOnItemSelectedListener(AdditionalInfoActivity.this);
                dialogFragment.show(getSupportFragmentManager(), "region_selection_dialog");
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });
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
        String ageStr = textViewAge.getText().toString().trim();
        String gender = textViewGender.getText().toString().trim();
        String region = textViewRegion.getText().toString().trim();

        if (TextUtils.isEmpty(ageStr) || ageStr.equals("나이를 선택하세요") ||
                TextUtils.isEmpty(gender) || gender.equals("성별을 선택하세요") ||
                TextUtils.isEmpty(region) || region.equals("지역을 선택하세요")) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);

        // UserSignupRequest DTO 생성: 백엔드 DTO의 순서에 맞춰 매개변수 순서 조정
        // 백엔드 DTO: email, name, password, phoneNumber, age, gender, region
        UserSignupRequest signupRequest = new UserSignupRequest(
                userEmail,       // email
                userName,        // name
                userPassword,    // password
                userPhoneNumber, // phoneNumber
                age,             // age
                gender,          // gender
                region           // region
        );

        Call<String> call = apiService.signupUser(signupRequest);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String successMessage = response.body();
                    Toast.makeText(AdditionalInfoActivity.this, successMessage != null ? successMessage : "회원가입 성공!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "회원가입 성공: " + signupRequest.getEmail());

                    Intent intent = new Intent(AdditionalInfoActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
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
                    Toast.makeText(AdditionalInfoActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "회원가입 응답 실패: HTTP " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(AdditionalInfoActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "회원가입 요청 실패 (네트워크 오류)", t);
            }
        });
    }
}

