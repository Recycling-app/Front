package com.example.recycling_app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recycling_app.Account.LoginActivity;
import com.example.recycling_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ResetPasswordFragment extends Fragment {

    private EditText emailEditText;
    private Button resetPasswordSubmitButton;
    private LinearLayout initialInputGroup;
    private LinearLayout resultDisplayGroup;
    private EditText resetEmailDisplayEditText;
    private Button loginFromResetButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_reset_password.xml 레이아웃을 인플레이트합니다.
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        // UI 요소들을 초기화합니다.
        emailEditText = view.findViewById(R.id.edit_text_reset_password_email);
        resetPasswordSubmitButton = view.findViewById(R.id.button_reset_password_submit);
        initialInputGroup = view.findViewById(R.id.initial_input_group_reset_password);
        resultDisplayGroup = view.findViewById(R.id.result_display_group_reset_password);
        resetEmailDisplayEditText = view.findViewById(R.id.edit_text_reset_email_display);
        loginFromResetButton = view.findViewById(R.id.button_login_from_reset);

        // 초기 상태 설정
        initialInputGroup.setVisibility(View.VISIBLE);
        resultDisplayGroup.setVisibility(View.GONE);

        // '임시 비밀번호 발송' 버튼 클릭 리스너 설정
        resetPasswordSubmitButton.setOnClickListener(v -> {
            final String email = emailEditText.getText().toString().trim();

            if (email.isEmpty()) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (getContext() != null) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "비밀번호 재설정 이메일을 전송했습니다.", Toast.LENGTH_SHORT).show();
                                initialInputGroup.setVisibility(View.GONE);
                                resultDisplayGroup.setVisibility(View.VISIBLE);
                                resetEmailDisplayEditText.setText(email);
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(getContext(), "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "비밀번호 재설정 이메일 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        });

        // '로그인하기' 버튼 클릭 리스너 (결과 화면에서)
        loginFromResetButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}
