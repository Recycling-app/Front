package com.example.recycling_app.Account;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.recycling_app.R;
import com.example.recycling_app.ui.fragment.FindEmailFragment;
import com.example.recycling_app.ui.fragment.ResetPasswordFragment;

public class FindInfoActivity extends AppCompatActivity {

    private Button buttonFindEmail;
    private Button buttonResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_info);

        buttonFindEmail = findViewById(R.id.button_find_email_tab);
        buttonResetPassword = findViewById(R.id.button_reset_password_tab);

        // 초기 화면 설정: 아이디 찾기 프래그먼트 로드 및 탭 상태 설정
        if (savedInstanceState == null) {
            loadFragment(new FindEmailFragment());
            setTabSelected(buttonFindEmail, true);
            setTabSelected(buttonResetPassword, false);
        }

        buttonFindEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new FindEmailFragment());
                setTabSelected(buttonFindEmail, true);
                setTabSelected(buttonResetPassword, false);
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ResetPasswordFragment());
                setTabSelected(buttonResetPassword, true);
                setTabSelected(buttonFindEmail, false);
            }
        });

        // 상단바 아이콘과 글씨 색상을 어둡게 설정 (Light Mode)
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    // 탭 선택 상태에 따라 배경 틴트와 텍스트 색상을 설정하는 헬퍼 메서드
    private void setTabSelected(Button button, boolean isSelected) {
        if (isSelected) {
            // 선택된 탭: 새로운 색상 (90E2A1) 적용
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.selected_tab_color)));
            button.setTextColor(ContextCompat.getColor(this, R.color.black)); // 텍스트 색상 검정
        } else {
            // 선택되지 않은 탭: 흰색 배경
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            button.setTextColor(ContextCompat.getColor(this, R.color.black)); // 텍스트 색상 검정
        }
    }
}