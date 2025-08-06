package com.example.recycling_app.Account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.recycling_app.R;
import com.example.recycling_app.StartscreenActivity;
import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.UserSignupRequest;
import com.example.recycling_app.ui.dialog.SelectionDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdditionalInfoActivity extends AppCompatActivity implements SelectionDialogFragment.OnItemSelectedListener {

    private static final String TAG = "AdditionalInfoActivity";

    private TextView textViewAge;
    private TextView textViewGender;
    private TextView textViewRegion;
    private Button buttonSignup;
    private ApiService apiService;
    private FirebaseFirestore db;
    private String userEmail;
    private String userPassword;
    private String userName;
    private String userPhoneNumber;

    private FirebaseAuth mAuth; // Firebase Authentication 인스턴스
    private DatabaseReference mDatabase; // Firebase Realtime Database 인스턴스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        mAuth = FirebaseAuth.getInstance(); // Authentication 초기화
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Realtime Database 초기화

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

        // EdgeToEdge 관련 코드: 시스템 바(상단바, 하단바)의 인셋을 고려하여 뷰의 패딩을 조정
        // 이 코드는 레이아웃이 시스템 바 아래로 확장될 때 콘텐츠가 시스템 바에 가려지지 않도록 함
        // 시스템 바의 인셋만큼 뷰의 좌, 상, 우, 하 패딩을 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 상단바 아이콘과 글씨 색상을 어둡게 설정 (Light Mode)
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
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

        // Firebase Authentication에 사용자 등록
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Realtime Database에 사용자 정보 저장
                        if (user != null) {
                            writeNewUser(user.getUid(), userEmail, userName, userPhoneNumber, age, gender, region);
                        }

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AdditionalInfoActivity.this, "회원가입 실패: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Realtime Database에 사용자 정보 저장하는 메서드
    private void writeNewUser(String userId, String userEmail, String userName, String userPhoneNumber, int age, String gender, String region) {
        UserSignupRequest user = new UserSignupRequest(
                userEmail,      // email
                userName,       // name
                userPhoneNumber, // phoneNumber
                age,        // age
                gender,     // gender
                region,     // region
                false       // isGoogleUser
        );

        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdditionalInfoActivity.this, "회원가입 완료!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdditionalInfoActivity.this, StartscreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Realtime Database 저장 실패", e);
                    Toast.makeText(AdditionalInfoActivity.this, "데이터베이스 저장 실패.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdditionalInfoActivity.this, StartscreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
    }
}
