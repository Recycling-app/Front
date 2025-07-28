package com.example.recycling_app.Howtobox;

import static com.google.android.material.internal.EdgeToEdgeUtils.applyEdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.recycling_app.Camera_recognition.CameraActivity;
import com.example.recycling_app.Camera_recognition.Photo_Recognition;
import com.example.recycling_app.Commuity.Commuity;
import com.example.recycling_app.LocationActivity;
import com.example.recycling_app.MainscreenActivity;
import com.example.recycling_app.MypageActivity;
import com.example.recycling_app.R;
import com.example.recycling_app.Howtobox.Guidearea_select;

public class Wasteguide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wasteguide);

        setupGuideAreaNavigation(); // 주소 검색 클릭 시 이동 설정
        setupBottomNavigation(); // 하단 내비게이션 아이콘들의 클릭 이벤트를 설정하는 메서드
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // Edge-to-Edge UI를 활성화
        applyWindowInsets(); // 시스템 UI와 여백을 맞추는 로직을 적용
    }
    
    // 주소 선택 클릭 이벤트를 설정하는 메서드
    private void setupGuideAreaNavigation() {
        View guideAreaSearchBox = findViewById(R.id.guidearea_search_box);

        guideAreaSearchBox.setOnClickListener(v -> {
            Intent intent = new Intent(Wasteguide.this, Guidearea_select.class);
            startActivity(intent);
        });
    }

    // 하단 내비게이션 아이콘들의 클릭 이벤트를 설정하는 메서드
    private void setupBottomNavigation() {
        ImageButton homeIcon = findViewById(R.id.home_icon);
        ImageButton mapIcon = findViewById(R.id.map_icon);
        ImageButton cameraIcon = findViewById(R.id.camera_icon);
        ImageButton messageIcon = findViewById(R.id.message_icon);
        ImageButton accountIcon = findViewById(R.id.account_icon);


        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Wasteguide.this, MainscreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        mapIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Wasteguide.this, LocationActivity.class);
            startActivity(intent);
        });

        cameraIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Wasteguide.this, CameraActivity.class);
            startActivity(intent);
        });


        accountIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Wasteguide.this, MypageActivity.class);
            startActivity(intent);
        });
    }

    // 시스템 UI 여백 조절
    private void applyWindowInsets() {
        View main_header = findViewById(R.id.main_header);
        View underbar = findViewById(R.id.underbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (view, insets) -> {
            // 시스템 바의 크기를 가져옵니다.
            int topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
            int oneDp = (int) (getResources().getDisplayMetrics().density); // 1dp에 해당하는 픽셀 값

            // 상단 헤더의 마진을 조절합니다.
            if (main_header != null && main_header.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) main_header.getLayoutParams();
                params.topMargin = topInset + oneDp;
                main_header.setLayoutParams(params);
            }

            // 하단 바의 마진을 조절합니다.
            if (underbar != null && underbar.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) underbar.getLayoutParams();
                params.bottomMargin = bottomInset + oneDp;
                underbar.setLayoutParams(params);
            }

            return WindowInsetsCompat.CONSUMED; // Insets을 소비했음을 시스템에 알립니다.
        });
    }
}