package com.example.recycling_app.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity; // Android의 기본 액티비티 클래스
import android.content.Intent; // 다른 액티비티를 시작할 때 사용
import android.os.Bundle; // 액티비티 상태 저장/복원 시 사용
import android.util.Log; // Log 클래스 임포트
import android.view.View; // UI 컴포넌트의 기본 클래스
import android.widget.ImageButton;
import android.widget.ImageView; // 이미지 뷰
import android.widget.TextView; // 텍스트 뷰
import android.widget.Toast; // 짧은 메시지 팝업

import androidx.activity.EdgeToEdge; // 화면 전체를 사용하는 EdgeToEdge 기능 활성화
import androidx.core.graphics.Insets; // 시스템 바 인셋(inset) 정보
import androidx.core.view.ViewCompat; // 뷰 호환성 유틸리티
import androidx.core.view.WindowInsetsCompat; // 윈도우 인셋(inset) 호환성 유틸리티

import com.example.recycling_app.StartscreenActivity;
import com.example.recycling_app.Camera_recognition.CameraActivity;
import com.example.recycling_app.Location.LocationActivity;
import com.example.recycling_app.MainscreenActivity;
import com.example.recycling_app.Profile.accountmanagement.AccountManagementActivity;
import com.example.recycling_app.Profile.customerservice.CustomerSupportActivity;
import com.example.recycling_app.Profile.detailsofuse.UsageHistoryActivity;
import com.example.recycling_app.Profile.mysetting.SettingsActivity;
import com.example.recycling_app.Profile.profieedit.ProfileEditActivity;
import com.example.recycling_app.R;

// 마이페이지 기능을 담당하는 액티비티
public class MypageActivity extends AppCompatActivity {
    private ImageView backArrowIcon;
    private TextView mypageTitle;
    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private TextView profileName;
    private View itemProfileEdit;
    private View itemAccountManagement;
    private View itemUsageHistory;
    private View itemSettings;
    private View itemCustomerSupport;
    private View itemLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // UI 요소 초기화 (XML 레이아웃의 ID와 연결)
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        mypageTitle = findViewById(R.id.mypage_title);
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);

        itemProfileEdit = findViewById(R.id.item_profile_edit);
        itemAccountManagement = findViewById(R.id.item_account_management);
        itemUsageHistory = findViewById(R.id.item_usage_history);
        itemSettings = findViewById(R.id.item_settings);
        itemCustomerSupport = findViewById(R.id.item_customer_support);
        itemLogout = findViewById(R.id.item_logout);

        // 하단 내비게이션 아이콘들의 클릭 이벤트를 설정
        setupBottomNavigation();

        // --- 클릭 리스너 설정 ---
        backArrowIcon.setOnClickListener(v -> onBackPressed());

        itemProfileEdit.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, ProfileEditActivity.class);
            startActivity(intent);
        });

        itemAccountManagement.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, AccountManagementActivity.class);
            startActivity(intent);
        });

        itemUsageHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, UsageHistoryActivity.class);
            startActivity(intent);
        });

        itemSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        itemCustomerSupport.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, CustomerSupportActivity.class);
            startActivity(intent);
        });

        // 로그아웃 메뉴 클릭 시 Firebase 로그아웃 처리 추가
        itemLogout.setOnClickListener(v -> {
            Toast.makeText(MypageActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            // 로그아웃 후 로그인 화면으로 이동
            Intent intent = new Intent(MypageActivity.this, StartscreenActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
            Intent intent = new Intent(MypageActivity.this, MainscreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        mapIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, LocationActivity.class);
            startActivity(intent);
        });

        cameraIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        accountIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MypageActivity.this, MypageActivity.class);
            startActivity(intent);
        });
    }
}