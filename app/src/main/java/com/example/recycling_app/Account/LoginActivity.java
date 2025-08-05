package com.example.recycling_app.Account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.recycling_app.MainscreenActivity;
import com.example.recycling_app.R;
import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.JwtLoginResponse;
import com.example.recycling_app.dto.LoginRequest;
import com.example.recycling_app.Network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 로그인 화면 레이아웃

        apiService = RetrofitClient.getApiService();
        editTextEmail = findViewById(R.id.edit_text_login_email);
        editTextPassword = findViewById(R.id.edit_text_login_password);
        buttonLogin = findViewById(R.id.button_login_submit);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // 상단바 아이콘과 글씨 색상을 어둡게 설정 (Light Mode)
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
    }

    private void attemptLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // 입력값 유효성 검사
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "유효한 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // LoginRequest DTO 생성
        LoginRequest loginRequest = new LoginRequest(email, password);

        // API 호출
        Call<JwtLoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<JwtLoginResponse>() {
            @Override
            public void onResponse(Call<JwtLoginResponse> call, Response<JwtLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JwtLoginResponse jwtResponse = response.body();
                    String accessToken = jwtResponse.getAccessToken();
                    String refreshToken = jwtResponse.getRefreshToken();

                    // 로그인 성공 처리: 토큰 저장 (예: SharedPreferences) 및 메인 화면으로 이동
                    // 여기서는 간단히 토스트 메시지를 띄우고 MainActivity로 이동합니다.
                    Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "로그인 성공: Access Token = " + accessToken);

                    Intent intent = new Intent(LoginActivity.this, MainscreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // 로그인 Activity 종료
                } else {
                    // 로그인 실패 처리
                    String errorMessage = "로그인 실패";
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBodyString);
                            // 백엔드에서 에러 메시지를 JSON으로 보낼 경우 파싱 필요
                            // 예: {"message": "Invalid credentials"}
                            // errorMessage += " (" + errorBodyString + ")";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "로그인 응답 실패: HTTP " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JwtLoginResponse> call, Throwable t) {
                // 네트워크 오류 처리
                Toast.makeText(LoginActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "로그인 요청 실패 (네트워크 오류)", t);
            }
        });
    }
}
