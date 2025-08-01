package com.example.recycling_app.Profile.customerservice;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycling_app.R;
import com.example.recycling_app.dto.InquiryDTO;
import com.example.recycling_app.service.ProfileApiService;
import com.example.recycling_app.Network.RetrofitClient;

// Firebase 관련 import 제거
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseUser;
// import com.google.android.gms.tasks.OnCompleteListener;
// import com.google.android.gms.tasks.Task;

import com.example.recycling_app.util.AuthManager; // AuthManager import 추가

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitInquiryActivity extends AppCompatActivity {

    private ImageView backArrowIcon;
    private TextView submitInquiryTitle;
    private EditText etInquiryTitle;
    private EditText etInquiryContent;
    private Button btnCancelInquiry;
    private Button btnSubmitInquiry;

    // Firebase 인증 객체 대신 AuthManager 사용
    private AuthManager authManager;
    private String currentUid;
    // private String jwtToken; // RetrofitClient의 Interceptor가 자동으로 처리하므로 직접 관리할 필요 없음

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_inquiry);

        // AuthManager 초기화
        authManager = RetrofitClient.getAuthManager();

        // 사용자 로그인 상태 확인 및 UID 가져오기
        currentUid = authManager.getUserId();
        // jwtToken = authManager.getAccessToken(); // Interceptor가 사용하므로 여기서 직접 가져올 필요 없음

        if (currentUid == null) {
            Toast.makeText(this, "로그인이 필요합니다. (사용자 ID 없음)", Toast.LENGTH_LONG).show();
            finish(); // 로그인 화면으로 리디렉션 또는 액티비티 종료
            return; // 이후 코드 실행 방지
        }

        backArrowIcon = findViewById(R.id.back_arrow_icon);
        submitInquiryTitle = findViewById(R.id.submit_inquiry_title);
        etInquiryTitle = findViewById(R.id.et_inquiry_title);
        etInquiryContent = findViewById(R.id.et_inquiry_content);
        btnCancelInquiry = findViewById(R.id.btn_cancel_inquiry);
        btnSubmitInquiry = findViewById(R.id.btn_submit_inquiry);

        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onBackPressed();
            }
        });

        btnCancelInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SubmitInquiryActivity.this, "문의 작성이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnSubmitInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitInquiry();
            }
        });
    }

    private void submitInquiry() {
        String title = etInquiryTitle.getText().toString().trim();
        String content = etInquiryContent.getText().toString().trim();

        if (currentUid == null) { // 토큰은 인터셉터가 처리하므로 UID만 확인
            Toast.makeText(this, "사용자 인증 정보가 없습니다. 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();

        InquiryDTO inquiryDTO = new InquiryDTO(currentUid, title, content, timestamp, null);

        ProfileApiService apiService = RetrofitClient.getProfileApiService();
        // String authHeader = "Bearer " + jwtToken; // 이 라인 제거
        apiService.submitInquiry(inquiryDTO).enqueue(new Callback<String>() { // authHeader 파라미터 제거
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(SubmitInquiryActivity.this, "문의가 성공적으로 제출되었습니다: " + response.body(), Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    if (response.code() == 401 || response.code() == 403) {
                        Toast.makeText(SubmitInquiryActivity.this, "인증 실패: 다시 로그인해주세요.", Toast.LENGTH_LONG).show();
                        authManager.clearAuthData();
                        finish();
                    } else {
                        String errorMessage = "문의 제출 실패";
                        try {
                            if (response.errorBody() != null) {
                                errorMessage += ": " + response.errorBody().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(SubmitInquiryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(SubmitInquiryActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}