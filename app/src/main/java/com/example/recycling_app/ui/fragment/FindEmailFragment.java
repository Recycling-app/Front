package com.example.recycling_app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recycling_app.Account.LoginActivity;
import com.example.recycling_app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindEmailFragment extends Fragment {

    private EditText nameEditText, numberEditText;
    private Button findIdBtn;
    private LinearLayout initialInputGroup;
    private LinearLayout resultDisplayGroup;
    private TextView foundEmailTextView, foundEmailTitleTextView, loginLinkTextView;
    private Button resetPasswordBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_email, container, false);

        nameEditText = view.findViewById(R.id.edit_text_find_email_name);
        numberEditText = view.findViewById(R.id.edit_text_find_email_phone_number);
        findIdBtn = view.findViewById(R.id.button_find_email_submit);
        initialInputGroup = view.findViewById(R.id.initial_input_group);
        resultDisplayGroup = view.findViewById(R.id.result_display_group);
        foundEmailTitleTextView = view.findViewById(R.id.text_view_found_email_title);
        foundEmailTextView = view.findViewById(R.id.edit_text_found_email);
        resetPasswordBtn = view.findViewById(R.id.button_reset_password_from_find_email);
        loginLinkTextView = view.findViewById(R.id.text_view_login_link);

        initialInputGroup.setVisibility(View.VISIBLE);
        resultDisplayGroup.setVisibility(View.GONE);

        findIdBtn.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String number = numberEditText.getText().toString().trim();

            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(getContext(), "이름과 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            } else {
                findUserId(name, number);
            }
        });

        resetPasswordBtn.setOnClickListener(v -> {
            // ResetPasswordFragment의 인스턴스를 생성합니다.
            ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();

            // FragmentManager를 사용하여 화면을 교체합니다.
            // getParentFragmentManager()는 Fragment를 관리하는 부모 FragmentManager를 반환합니다.
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, resetPasswordFragment) // R.id.fragment_container는 Fragment가 표시될 레이아웃 ID입니다.
                    .addToBackStack(null) // 이전 Fragment로 돌아갈 수 있도록 백 스택에 추가합니다.
                    .commit(); // 트랜잭션을 실행합니다.
        });

        loginLinkTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void findUserId(String name, String number) {
        // 경로를 'users'로 수정
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("FindEmailFragment", "onDataChange 호출됨");
                Log.d("FindEmailFragment", "전체 데이터 스냅샷: " + dataSnapshot.toString());

                boolean found = false;
                if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                    Log.e("FindEmailFragment", "users 경로에 데이터가 없거나 자식이 없습니다.");
                    Toast.makeText(getContext(), "데이터베이스에 사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userName = userSnapshot.child("name").getValue(String.class);
                    String userNumber = userSnapshot.child("phoneNumber").getValue(String.class);
                    String userId = userSnapshot.child("email").getValue(String.class);

                    Log.d("FindEmailFragment", "조회 중인 사용자: " + userSnapshot.getKey());
                    Log.d("FindEmailFragment", "이름: " + userName + ", 전화번호: " + userNumber);
                    Log.d("FindEmailFragment", "찾을 이름: " + name + ", 찾을 전화번호: " + number);

                    if (userName != null && userName.equals(name) && userNumber != null && userNumber.equals(number)) {
                        if (userId != null) {
                            foundEmailTextView.setText(userId);
                            showResultView(true);
                            found = true;
                            Log.d("FindEmailFragment", "아이디를 찾았습니다: " + userId);
                        }
                        break;
                    }
                }

                if (!found) {
                    showResultView(false);
                    Toast.makeText(getContext(), "해당하는 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    Log.d("FindEmailFragment", "일치하는 사용자를 찾지 못했습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("FindEmailFragment", "데이터베이스 오류: " + databaseError.getMessage());
            }
        });
    }

    private void showResultView(boolean isSuccess) {
        if (isSuccess) {
            initialInputGroup.setVisibility(View.GONE);
            resultDisplayGroup.setVisibility(View.VISIBLE);
        } else {
            initialInputGroup.setVisibility(View.VISIBLE);
            resultDisplayGroup.setVisibility(View.GONE);
        }
    }
}