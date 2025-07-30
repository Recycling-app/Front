package com.example.recycling_app.Profile.profieedit;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycling_app.R;
import com.example.recycling_app.util.AuthManager; // AuthManager 임포트
import com.example.recycling_app.Network.RetrofitClient; // RetrofitClient 임포트

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// 사용자의 비밀번호를 변경하는 액티비티
public class PasswordEditActivity extends AppCompatActivity {

    private ImageView backArrowIcon;
    private TextView passwordEditTitle;
    private EditText etCurrentPassword; // 현재 비밀번호 입력 필드
    private EditText etNewPassword;     // 새 비밀번호 입력 필드
    private EditText etConfirmNewPassword; // 새 비밀번호 확인 입력 필드
    private Button btnCancel;           // 취소 버튼
    private Button btnConfirm;          // 확인 (비밀번호 변경) 버튼

    private AuthManager authManager; // AuthManager 인스턴스
    private FirebaseUser currentUser; // 현재 Firebase 사용자

    private static final String TAG = "PasswordEditActivity"; // 로그 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_edit); // 비밀번호 변경 레이아웃 파일 연결

        // UI 요소 초기화
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        passwordEditTitle = findViewById(R.id.password_edit_title);
        etCurrentPassword = findViewById(R.id.et_current_password); // XML ID 확인 필요
        etNewPassword = findViewById(R.id.et_new_password);         // XML ID 확인 필요
        etConfirmNewPassword = findViewById(R.id.et_confirm_password); // XML ID 확인 필요
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);

        // AuthManager 초기화 및 현재 사용자 정보 가져오기
        authManager = RetrofitClient.getAuthManager();
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // FirebaseUser 직접 가져오기

        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 뒤로가기 버튼 클릭 리스너
        backArrowIcon.setOnClickListener(v -> onBackPressed());

        // 취소 버튼 클릭 리스너
        btnCancel.setOnClickListener(v -> {
            Toast.makeText(this, "비밀번호 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        });

        // 확인 버튼 클릭 리스너
        btnConfirm.setOnClickListener(v -> changePassword());
    }

    // 비밀번호 변경 로직
    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // 입력 값 유효성 검사
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newPassword.length() < 6) { // Firebase 최소 비밀번호 길이
            Toast.makeText(this, "새 비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            // Firebase 재인증 필요: 비밀번호 변경 전에 현재 비밀번호로 사용자 재인증
            currentUser.reauthenticate(com.google.firebase.auth.EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // 재인증 성공, 이제 비밀번호 변경
                            currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(PasswordEditActivity.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "User password updated.");
                                            finish(); // 성공 시 액티비티 종료
                                        } else {
                                            Toast.makeText(PasswordEditActivity.this, "비밀번호 변경 실패: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            Log.e(TAG, "비밀번호 변경 실패", updateTask.getException());
                                        }
                                    });
                        } else {
                            Toast.makeText(PasswordEditActivity.this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "재인증 실패", task.getException());
                        }
                    });
        }
    }
}