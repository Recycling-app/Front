package com.example.recycling_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.GoogleLoginRequest;
import com.example.recycling_app.dto.JwtLoginResponse;
import com.example.recycling_app.network.RetrofitClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001; // Google Sign-In 요청 코드

    private GoogleSignInClient mGoogleSignInClient;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService();

        // Google Sign-In 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI 요소들을 XML ID와 연결
        Button loginButton = findViewById(R.id.login_button);
        Button googleLoginButton = findViewById(R.id.google_login_button);
        TextView signupTextView = findViewById(R.id.signup_text);
        TextView findInfoTextView = findViewById(R.id.find_info_text);

        // "Login" 버튼 클릭 리스너 (LoginActivity로 이동)
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // "Sign in with Google" 버튼 클릭 리스너 (Google 로그인 시작)
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        // "회원가입" 텍스트뷰 클릭 리스너
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // "아이디 및 비밀번호 찾기" 텍스트뷰 클릭 리스너 (FindInfoActivity로 이동)
        findInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    // Google 로그인 인텐트 시작
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Google 로그인 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Google 로그인 결과 핸들링
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken(); // 백엔드로 보낼 ID 토큰
            String email = account.getEmail(); // Google 계정 이메일

            if (idToken != null) {
                sendIdTokenToBackend(idToken, email); // 이메일도 함께 전달
            } else {
                Toast.makeText(this, "Google ID Token을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google ID Token is null.");
            }
        } catch (ApiException e) {
            Log.w(TAG, "Google 로그인 실패: statusCode=" + e.getStatusCode() + ", message=" + e.getMessage());
            Toast.makeText(this, "Google 로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 백엔드로 Google ID 토큰 전송 (이메일 매개변수 추가)
    private void sendIdTokenToBackend(String idToken, String email) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        Call<JwtLoginResponse> call = apiService.googleLogin(request);

        call.enqueue(new Callback<JwtLoginResponse>() {
            @Override
            public void onResponse(Call<JwtLoginResponse> call, Response<JwtLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Google 로그인 성공 (이미 등록된 사용자)
                    JwtLoginResponse jwtResponse = response.body();
                    String accessToken = jwtResponse.getAccessToken();
                    String refreshToken = jwtResponse.getRefreshToken();

                    Toast.makeText(MainActivity.this, "Google 로그인 성공!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Google 로그인 성공: Access Token = " + accessToken);

                    // 로그인 성공 시 앱의 메인 콘텐츠 화면으로 이동
                    Intent intent = new Intent(MainActivity.this, MainActivity.class); // 예시: MainActivity로 이동
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 401 || response.code() == 404) { // 백엔드에서 '등록된 사용자 아님' 응답 (UNAUTHORIZED 또는 NOT_FOUND)
                    String errorBodyString = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBodyString = response.errorBody().string();
                            Log.e(TAG, "Google Login Error Body (Signup Required): " + errorBodyString);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Google login error body", e);
                    }

                    // 백엔드에서 "등록된 사용자가 아닙니다. 먼저 회원가입하세요." 메시지를 보낸다고 가정
                    if (errorBodyString.contains("등록된 사용자가 아닙니다")) {
                        Toast.makeText(MainActivity.this, "Google 계정으로 회원가입이 필요합니다.", Toast.LENGTH_LONG).show();
                        // GoogleAdditionalInfoActivity로 이동하여 추가 정보 입력받기
                        Intent intent = new Intent(MainActivity.this, GoogleAdditionalInfoActivity.class);
                        intent.putExtra("idToken", idToken); // ID 토큰 전달
                        intent.putExtra("email", email); // Google 계정 이메일 전달
                        startActivity(intent);
                    } else {
                        // 다른 401/404 오류 처리
                        Toast.makeText(MainActivity.this, "Google 로그인 실패: " + errorBodyString, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Google 로그인 응답 실패 (다른 401/404): HTTP " + response.code() + " " + response.message());
                    }
                } else {
                    // 기타 HTTP 오류 처리
                    String errorMessage = "Google 로그인 실패";
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            Log.e(TAG, "Google Login Error Body: " + errorBodyString);
                            errorMessage += ": " + errorBodyString;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing Google login error body", e);
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Google 로그인 응답 실패: HTTP " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JwtLoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Google 로그인 요청 실패 (네트워크 오류)", t);
            }
        });
    }
}