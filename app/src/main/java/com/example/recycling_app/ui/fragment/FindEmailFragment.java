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
import androidx.fragment.app.FragmentTransaction; // FragmentTransaction 추가

import com.example.recycling_app.Account.LoginActivity; // LoginActivity import
import com.example.recycling_app.R;
import com.example.recycling_app.api.ApiService;
import com.example.recycling_app.dto.FindEmailRequest;
import com.example.recycling_app.dto.FindEmailResponse;
import com.example.recycling_app.Network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindEmailFragment extends Fragment {

    private static final String TAG = "FindEmailFragment";

    // UI 그룹들
    private LinearLayout initialInputGroup; // 초기 입력 화면 전체 그룹
    private LinearLayout resultDisplayGroup; // 결과 화면 전체 그룹

    // 초기 화면 요소
    private EditText editTextName;
    private EditText editTextPhoneNumber;
    private Button buttonFindEmailSubmit;

    // 결과 화면 요소
    private EditText editTextFoundEmail; // 찾은 이메일
    private Button buttonResetPassword; // 비밀번호 찾기 버튼
    private TextView textViewLoginLink; // 로그인하기 텍스트

    private ApiService apiService;

    public FindEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();

        // UI 그룹 연결
        initialInputGroup = view.findViewById(R.id.initial_input_group);
        resultDisplayGroup = view.findViewById(R.id.result_display_group);

        // 초기 화면 요소 연결
        editTextName = view.findViewById(R.id.edit_text_find_email_name);
        editTextPhoneNumber = view.findViewById(R.id.edit_text_find_email_phone_number);
        buttonFindEmailSubmit = view.findViewById(R.id.button_find_email_submit);

        // 결과 화면 요소 연결
        editTextFoundEmail = view.findViewById(R.id.edit_text_found_email);
        buttonResetPassword = view.findViewById(R.id.button_reset_password_from_find_email);
        textViewLoginLink = view.findViewById(R.id.text_view_login_link);

        // 초기 화면 설정: 입력 필드 그룹을 보이게 하고, 결과 그룹을 숨깁니다.
        showInitialInputView();

        buttonFindEmailSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptFindEmail();
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 찾기 프래그먼트로 이동
                if (getActivity() != null) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new ResetPasswordFragment());
                    transaction.addToBackStack(null); // 뒤로가기 버튼으로 돌아올 수 있도록 스택에 추가
                    transaction.commit();
                }
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 Activity로 이동
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    // 현재 FindInfoActivity를 종료하고 MainActivity로 돌아가면서 LoginActivity를 띄웁니다.
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    private void attemptFindEmail() {
        String name = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getContext(), "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        FindEmailRequest request = new FindEmailRequest(name, phoneNumber);
        Call<FindEmailResponse> call = apiService.findEmail(request);

        call.enqueue(new Callback<FindEmailResponse>() {
            @Override
            public void onResponse(Call<FindEmailResponse> call, Response<FindEmailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FindEmailResponse findEmailResponse = response.body();
                    String foundEmail = findEmailResponse.getEmail();
                    if (foundEmail != null && !foundEmail.isEmpty()) {
                        editTextFoundEmail.setText(foundEmail);
                        showResultView(); // 결과 화면 표시
                        Toast.makeText(getContext(), "이메일을 찾았습니다!", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "이메일 찾기 성공: " + foundEmail);
                    } else {
                        Toast.makeText(getContext(), "일치하는 이메일이 없습니다.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "이메일 찾기 성공했으나, 일치하는 이메일 없음.");
                        showInitialInputView(); // 입력 화면 유지
                    }
                } else {
                    String errorMessage = "이메일 찾기 실패";
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
                    Log.e(TAG, "이메일 찾기 응답 실패: HTTP " + response.code() + " " + response.message());
                    showInitialInputView(); // 입력 화면 유지
                }
            }

            @Override
            public void onFailure(Call<FindEmailResponse> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "이메일 찾기 요청 실패 (네트워크 오류)", t);
                showInitialInputView(); // 입력 화면 유지
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
