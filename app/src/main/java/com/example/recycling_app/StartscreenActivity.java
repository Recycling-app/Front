package com.example.recycling_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.recycling_app.Account.Googleuser_AdditionalInfoActivity;
import com.example.recycling_app.Account.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.example.recycling_app.Account.FindInfoActivity;
import com.example.recycling_app.Account.SignupActivity;
import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.network.RetrofitClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class StartscreenActivity extends AppCompatActivity {

    private static final String TAG = "StartscreenActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> signIn());

        Button loginButton = findViewById(R.id.login_button);
        TextView signupTextView = findViewById(R.id.signup_text);
        TextView findInfoTextView = findViewById(R.id.find_info_text);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartscreenActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signupTextView.setOnClickListener(v -> {
            Intent intent = new Intent(StartscreenActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        findInfoTextView.setOnClickListener(v -> {
            Intent intent = new Intent(StartscreenActivity.this, FindInfoActivity.class);
            startActivity(intent);
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

    private void signIn() {
        // 기존에 로그인되어 있는 계정 정보가 있다면 로그아웃을 먼저 수행
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // signOut()이 완료되면 로그인 인텐트 시작
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google 로그인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.getEmail() != null) {
                            String userEmail = user.getEmail();

                            db.collection("users").document(userEmail)
                                    .get()
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            DocumentSnapshot document = dbTask.getResult();
                                            if (document.exists()) {
                                                // 이미 존재하는 사용자: 로그인 성공
                                                Toast.makeText(StartscreenActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(StartscreenActivity.this, MainscreenActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // 새로운 사용자: Googleuser_AdditionalInfoActivity로 이동
                                                Toast.makeText(StartscreenActivity.this, "회원 정보 없음. 추가 정보 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(StartscreenActivity.this, Googleuser_AdditionalInfoActivity.class);
                                                intent.putExtra("email", userEmail);
                                                intent.putExtra("name", user.getDisplayName());
                                                startActivity(intent);
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting document from Firestore: ", dbTask.getException());
                                            Toast.makeText(StartscreenActivity.this, "Firestore 조회 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(StartscreenActivity.this, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(StartscreenActivity.this, "Firebase 인증 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 이전 코드의 자동 로그인 로직은 제거.
        // 사용자가 명시적으로 로그인/회원가입을 선택하도록 함.
    }
}