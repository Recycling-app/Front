package com.example.recycling_app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout; // LinearLayout 추가
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recycling_app.LoginActivity; // LoginActivity import
import com.example.recycling_app.R;
import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.ResetPasswordRequest;
import com.example.recycling_app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {

    private static final String TAG = "ResetPasswordFragment";

    // UI 그룹들
    private LinearLayout initialInputGroup; // 초기 입력 화면 전체 그룹
    private LinearLayout resultDisplayGroup; // 결과 화면 전체 그룹

    // 초기 화면 요소
    private EditText editTextEmail;
    private Button buttonResetPasswordSubmit;

    // 결과 화면 요소
    private EditText editTextResetEmailDisplay; // 이메일 표시 필드
    private Button buttonLoginFromReset; // "로그인하기" 버튼

    private ApiService apiService;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();

        // UI 그룹 연결
        initialInputGroup = view.findViewById(R.id.initial_input_group_reset_password);
        resultDisplayGroup = view.findViewById(R.id.result_display_group_reset_password);

        // 초기 화면 요소 연결
        editTextEmail = view.findViewById(R.id.edit_text_reset_password_email);
        buttonResetPasswordSubmit = view.findViewById(R.id.button_reset_password_submit);

        // 결과 화면 요소 연결
        editTextResetEmailDisplay = view.findViewById(R.id.edit_text_reset_email_display);
        buttonLoginFromReset = view.findViewById(R.id.button_login_from_reset);

        // 초기 화면 설정: 입력 필드 그룹을 보이게 하고, 결과 그룹을 숨깁니다.
        showInitialInputView();

        buttonResetPasswordSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptResetPassword();
            }
        });

        buttonLoginFromReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 Activity로 이동
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish(); // 현재 FindInfoActivity 종료
                }
            }
        });
    }

    private void attemptResetPassword() {
        String email = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "유효한 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest(email);
        Call<String> call = apiService.resetPassword(request);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String successMessage = response.body();
                    Toast.makeText(getContext(), successMessage != null ? successMessage : "임시 비밀번호가 이메일로 발송되었습니다.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "비밀번호 재설정 성공: " + successMessage);

                    // 성공 시 결과 화면 표시 및 이메일 설정
                    editTextResetEmailDisplay.setText(email); // 사용자가 입력한 이메일 표시
                    showResultView();
                } else {
                    String errorMessage = "비밀번호 재설정 실패";
                    try {
                        if (response.errorBody() != null) {
                            String errorBodyString = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBodyString);
                            errorMessage += ": " + errorBodyString;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "비밀번호 재설정 응답 실패: HTTP " + response.code() + " " + response.message());
                    showInitialInputView(); // 실패 시 입력 화면 유지
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "비밀번호 재설정 요청 실패 (네트워크 오류)", t);
                showInitialInputView(); // 실패 시 입력 화면 유지
            }
        });
    }

    // 초기 입력 화면을 보여주는 메서드
    private void showInitialInputView() {
        initialInputGroup.setVisibility(View.VISIBLE);
        resultDisplayGroup.setVisibility(View.GONE);
    }

    // 결과 화면을 보여주는 메서드
    private void showResultView() {
        initialInputGroup.setVisibility(View.GONE);
        resultDisplayGroup.setVisibility(View.VISIBLE);
    }
}
