package com.example.recycling_app.Profile.accountmanagement;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycling_app.StartscreenActivity;
import com.example.recycling_app.R;
import com.example.recycling_app.Network.RetrofitClient;
import com.example.recycling_app.service.ProfileApiService;
import com.google.firebase.auth.FirebaseAuth; // Firebase 인증 import
import com.google.firebase.auth.FirebaseUser; // Firebase 사용자 import
import com.google.android.gms.tasks.OnCompleteListener; // OnCompleteListener import
import com.google.android.gms.tasks.Task; // Task import

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 회원 탈퇴 기능을 담당하는 액티비티
public class AccountDeletionActivity extends AppCompatActivity {

    private ImageView backArrowIcon;
    private TextView accountDeletionTitle;
    private EditText etCurrentPassword;
    private EditText etConfirmPassword;
    private Button btnCancel;
    private Button btnConfirm;

    //Firebase 인증 객체 선언
    private FirebaseAuth mAuth;
    private String currentUid; // 실제 Firebase UID로 교체 (로그인 후 받아온 실제 사용자 UID 사용)
    private String firebaseIdToken; // Firebase ID 토큰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_deletion);

        // Firebase 인증 객체 초기화
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 사용자 로그인 상태 확인 및 UID, ID 토큰 가져오기
        if (currentUser != null) {
            currentUid = currentUser.getUid();
            currentUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.GetTokenResult>() {
                        @Override
                        public void onComplete(Task<com.google.firebase.auth.GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                firebaseIdToken = task.getResult().getToken();
                                // 토큰 획득 성공. 이제 버튼 활성화 등의 후속 작업을 할 수 있음.
                                // (현재는 별도 로직 없으므로 다음 단계로 넘어감)
                            } else {
                                Toast.makeText(AccountDeletionActivity.this, "Firebase ID 토큰 가져오기 실패", Toast.LENGTH_SHORT).show();
                                finish(); // 오류 시 액티비티 종료
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish(); // 로그인되지 않은 경우 액티비티 종료
        }

        // UI 요소 초기화
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        accountDeletionTitle = findViewById(R.id.account_deletion_title);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);

        backArrowIcon.setOnClickListener(v -> onBackPressed());

        btnCancel.setOnClickListener(v -> {
            Toast.makeText(AccountDeletionActivity.this, "회원 탈퇴가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnConfirm.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (currentPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(AccountDeletionActivity.this, "모든 비밀번호 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
            } else if (!currentPassword.equals(confirmPassword)) {
                Toast.makeText(AccountDeletionActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원 탈퇴 확인");
        builder.setMessage("정말로 회원 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.");

        builder.setPositiveButton("예", (dialog, which) -> performAccountDeletion()); // 람다식 사용
        builder.setNegativeButton("아니오", (dialog, which) -> { // 람다식 사용
            Toast.makeText(AccountDeletionActivity.this, "회원 탈퇴가 취소되었습니다.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.show();
    }

    private void performAccountDeletion() {
        // UID 또는 토큰이 없는 경우 처리
        if (currentUid == null || firebaseIdToken == null) {
            Toast.makeText(this, "사용자 인증 정보가 없어 계정을 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProfileApiService apiService = RetrofitClient.getProfileApiService();
        String authHeader = "Bearer " + firebaseIdToken; // Authorization 헤더 생성

        // deleteUserAccount API 호출 시 Authorization 헤더 전달
        apiService.deleteUserAccount(currentUid).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AccountDeletionActivity.this, "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_LONG).show();

                    // Firebase 인증 세션에서도 로그아웃 처리
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(AccountDeletionActivity.this, StartscreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AccountDeletionActivity.this, "회원 탈퇴 실패: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(AccountDeletionActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}