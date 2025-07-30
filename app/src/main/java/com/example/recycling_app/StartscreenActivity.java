package com.example.recycling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recycling_app.Account.FindInfoActivity;
import com.example.recycling_app.Account.SignupActivity;
import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.Network.RetrofitClient;
//import com.example.recycling_app.dto.GoogleLoginRequest;


public class StartscreenActivity extends AppCompatActivity {

    private static final String TAG = "StartscreenActivity";
    private static final int RC_SIGN_IN = 9001; // Google Sign-In 요청 코드

//    private GoogleSignInClient mGoogleSignInClient;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService();

        // Google Sign-In 설정
        // R.string.default_web_client_id는 google-services.json 파일에서 자동으로 생성됩니다.
        // Firebase 프로젝트를 설정하고 Google Sign-In을 활성화해야 합니다.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id)) // 백엔드에서 ID 토큰 검증 시 필요
//                .requestEmail() // 이메일 정보 요청
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI 요소들을 XML ID와 연결
        Button loginButton = findViewById(R.id.login_button);
        Button googleLoginButton = findViewById(R.id.google_login_button);
        TextView signupTextView = findViewById(R.id.signup_text);
        TextView findInfoTextView = findViewById(R.id.find_info_text);

        // "Login" 버튼 클릭 리스너 (LoginActivity로 이동)
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartscreenActivity.this, com.example.recycling_app.Account.LoginActivity.class);
                startActivity(intent);
            }
        });

        // "Sign in with Google" 버튼 클릭 리스너 (Google 로그인 시작)
//        googleLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signInWithGoogle(); // 이 라인 주석 해제하여 기능 활성화
//            }
//        });

        // "회원가입" 텍스트뷰 클릭 리스너
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartscreenActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // "아이디 및 비밀번호 찾기" 텍스트뷰 클릭 리스너 (FindInfoActivity로 이동)
        findInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartscreenActivity.this, FindInfoActivity.class);
                startActivity(intent);
            }
        });
//         TedPermission.with(getApplicationContext())
//                 .setPermissionListener(permissionListener)
//                 .setRationaleMessage("카메라 권한이 필요합니다.")
//                 .setDeniedMessage("거부하셨습니다.")
//                 .setPermissions(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
//                 .check();

        Intent intent = new Intent(StartscreenActivity.this, MainscreenActivity.class);
        startActivity(intent);
        finish();
    };
}

    // Google 로그인 인텐트 시작
//    private void signInWithGoogle() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    // Google 로그인 결과 처리
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }

    // Google 로그인 결과 핸들링
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            String idToken = account.getIdToken(); // 백엔드로 보낼 ID 토큰
//
//            if (idToken != null) {
//                sendIdTokenToBackend(idToken);
//            } else {
//                Toast.makeText(this, "Google ID Token을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Google ID Token is null.");
//            }
//        } catch (ApiException e) {
//            // Google 로그인 실패 (예: 사용자가 취소, 네트워크 오류 등)
//            Log.w(TAG, "Google 로그인 실패: statusCode=" + e.getStatusCode() + ", message=" + e.getMessage());
//            Toast.makeText(this, "Google 로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

    // 백엔드로 Google ID 토큰 전송
//    private void sendIdTokenToBackend(String idToken) {
//        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
//        Call<JwtLoginResponse> call = apiService.googleLogin(request); // 백엔드의 googleLogin 엔드포인트 호출
//
//        call.enqueue(new Callback<JwtLoginResponse>() {
//            @Override
//            public void onResponse(Call<JwtLoginResponse> call, Response<JwtLoginResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    JwtLoginResponse jwtResponse = response.body();
//                    String accessToken = jwtResponse.getAccessToken();
//                    String refreshToken = jwtResponse.getRefreshToken();
//
//                    Toast.makeText(MainActivity.this, "Google 로그인 성공!", Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "Google 로그인 성공: Access Token = " + accessToken);
//
//                    // 성공 시 앱의 메인 콘텐츠 화면으로 이동 (현재는 MainActivity로 다시 이동)
//                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    String errorMessage = "Google 로그인 실패";
//                    try {
//                        if (response.errorBody() != null) {
//                            String errorBodyString = response.errorBody().string();
//                            Log.e(TAG, "Google Login Error Body: " + errorBodyString);
//                            errorMessage += ": " + errorBodyString;
//                        }
//                    } catch (Exception e) {
//                        Log.e(TAG, "Error parsing Google login error body", e);
//                    }
//                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//                    Log.e(TAG, "Google 로그인 응답 실패: HTTP " + response.code() + " " + response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JwtLoginResponse> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
//                Log.e(TAG, "Google 로그인 요청 실패 (네트워크 오류)", t);
//            }
//        });
//    }