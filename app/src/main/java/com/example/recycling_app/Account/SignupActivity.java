package com.example.recycling_app.Account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recycling_app.Account.AdditionalInfoActivity;
import com.example.recycling_app.R;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private EditText editTextEmail, editTextPassword, editTextPasswordConfirm, editTextNickname, editTextPhone;
    private CheckBox checkboxLocationConsent, checkboxPrivacyConsent;
    private Button buttonNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextEmail = findViewById(R.id.edit_text_id);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextPasswordConfirm = findViewById(R.id.edit_text_password_confirm);
        editTextNickname = findViewById(R.id.edit_text_nickname); // 이름 입력 필드
        editTextPhone = findViewById(R.id.edit_text_phone); // 전화번호 입력 필드

        checkboxLocationConsent = findViewById(R.id.checkbox_location_consent);
        checkboxPrivacyConsent = findViewById(R.id.checkbox_privacy_consent);
        buttonNextPage = findViewById(R.id.button_signup);

        buttonNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptGoToNextPage();
            }
        });
    }

    private void attemptGoToNextPage() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextPasswordConfirm.getText().toString().trim();
        String name = editTextNickname.getText().toString().trim(); // 닉네임 -> 이름으로 사용
        String phoneNumber = editTextPhone.getText().toString().trim(); // 전화번호

        boolean isLocationConsent = checkboxLocationConsent.isChecked();
        boolean isPrivacyConsent = checkboxPrivacyConsent.isChecked();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(passwordConfirm) || TextUtils.isEmpty(name) || // 이름 필드 유효성 검사
                TextUtils.isEmpty(phoneNumber)) { // 전화번호 필드 유효성 검사
            Toast.makeText(this, "모든 필수 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "유효한 이메일 주소를 아이디로 입력해주세요.", Toast.LENGTH_SHORT).show();
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

        if (!phoneNumber.matches("^(010|011|016|017|018|019)-?[0-9]{3,4}-?[0-9]{4}$")) {
            Toast.makeText(this, "유효한 전화번호 형식이 아닙니다 (예: 010-1234-5678 또는 01012345678)", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isLocationConsent) {
            Toast.makeText(this, "위치 정보 수집 동의가 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPrivacyConsent) {
            Toast.makeText(this, "개인정보 수집 동의가 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // AdditionalInfoActivity로 데이터 전달
        Intent intent = new Intent(SignupActivity.this, AdditionalInfoActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("name", name);
        intent.putExtra("phoneNumber", phoneNumber);

        startActivity(intent);
    }
}