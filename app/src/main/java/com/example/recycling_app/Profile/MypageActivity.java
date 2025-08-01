package com.example.recycling_app.Profile;

import androidx.appcompat.app.AppCompatActivity; // Android의 기본 액티비티 클래스
import android.content.Intent; // 다른 액티비티를 시작할 때 사용
import android.os.Bundle; // 액티비티 상태 저장/복원 시 사용
import android.view.View; // UI 컴포넌트의 기본 클래스
import android.widget.ImageButton;
import android.widget.ImageView; // 이미지 뷰
import android.widget.TextView; // 텍스트 뷰
import android.widget.Toast; // 짧은 메시지 팝업

import androidx.activity.EdgeToEdge; // 화면 전체를 사용하는 EdgeToEdge 기능 활성화
import androidx.core.graphics.Insets; // 시스템 바 인셋(inset) 정보
import androidx.core.view.ViewCompat; // 뷰 호환성 유틸리티
import androidx.core.view.WindowInsetsCompat; // 윈도우 인셋(inset) 호환성 유틸리티

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
// 사용자에게 프로필 정보 표시 및 다양한 메뉴(프로필 수정, 계정 관리, 이용 내역, 설정, 고객 지원, 로그아웃) 제공
public class MypageActivity extends AppCompatActivity {

    // UI 요소들을 위한 변수 선언
    private ImageView backArrowIcon; // 뒤로가기 화살표 아이콘
    private TextView mypageTitle; // 마이페이지 제목 텍스트
    private de.hdodenhof.circleimageview.CircleImageView profileImage; // 원형 프로필 이미지 뷰 (서드파티 라이브러리)
    private TextView profileName; // 사용자 프로필 이름 텍스트

    // 메뉴 항목들을 나타내는 View 변수들 (주로 LinearLayout이나 ConstraintLayout)
    private View itemProfileEdit; // '프로필 수정' 메뉴
    private View itemAccountManagement; // '계정 관리' 메뉴
    private View itemUsageHistory; // '이용 내역' 메뉴
    private View itemSettings; // '설정' 메뉴
    private View itemCustomerSupport; // '고객 지원' 메뉴
    private View itemLogout; // '로그아웃' 메뉴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // EdgeToEdge 기능을 활성화하여 시스템 바(상단바, 하단바) 영역까지 콘텐츠를 확장
        setContentView(R.layout.activity_profile); // 이 액티비티에 연결될 레이아웃 파일 지정 (activity_profile.xml)

        // UI 요소 초기화 (XML 레이아웃의 ID와 연결)
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        mypageTitle = findViewById(R.id.mypage_title);
        profileImage = findViewById(R.id.profile_image); // CircleImageView 위젯과 연결
        profileName = findViewById(R.id.profile_name);

        // 각 메뉴 항목 View들을 ID로 찾아서 연결
        itemProfileEdit = findViewById(R.id.item_profile_edit);
        itemAccountManagement = findViewById(R.id.item_account_management);
        itemUsageHistory = findViewById(R.id.item_usage_history);
        itemSettings = findViewById(R.id.item_settings);
        itemCustomerSupport = findViewById(R.id.item_customer_support);
        itemLogout = findViewById(R.id.item_logout);

        // 예시 데이터 설정 (실제 앱에서는 사용자 로그인 정보 등으로 설정)
        profileName.setText("홍길동");
        // profileImage.setImageResource(R.drawable.your_user_profile_image); // 실제 사용자 프로필 이미지로 변경 필요

        // --- 클릭 리스너 설정 ---

        // 뒤로가기 아이콘 클릭 리스너
        backArrowIcon.setOnClickListener(v -> onBackPressed()); // 클릭 시 이전 화면으로 돌아감

        // '프로필 수정' 메뉴 클릭 리스너
        itemProfileEdit.setOnClickListener(v -> {
            // ProfileEditActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(MypageActivity.this, ProfileEditActivity.class);
            startActivity(intent);
        });

        // '계정 관리' 메뉴 클릭 리스너
        itemAccountManagement.setOnClickListener(v -> {
            // AccountManagementActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(MypageActivity.this, AccountManagementActivity.class);
            startActivity(intent);
        });

        // '이용 내역' 메뉴 클릭 리스너
        itemUsageHistory.setOnClickListener(v -> {
            // UsageHistoryActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(MypageActivity.this, UsageHistoryActivity.class);
            startActivity(intent);
        });

        // '설정' 메뉴 클릭 리스너
        itemSettings.setOnClickListener(v -> {
            // SettingsActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(MypageActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // '고객 지원' 메뉴 클릭 리스너
        itemCustomerSupport.setOnClickListener(v -> {
            // CustomerSupportActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(MypageActivity.this, CustomerSupportActivity.class);
            startActivity(intent);
        });

        // '로그아웃' 메뉴 클릭 리스너
        itemLogout.setOnClickListener(v -> {
            Toast.makeText(MypageActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            // TODO: 실제 로그아웃 로직 구현 필요 (예: Firebase Auth 로그아웃, 로컬 세션 데이터 삭제 등)
            // 로그인 화면으로 돌아가거나 앱을 종료하는 등의 후속 처리가 필요함
            finish(); // 현재 액티비티 종료
        });

        // EdgeToEdge 관련 코드: 시스템 바(상단바, 하단바)의 인셋을 고려하여 뷰의 패딩을 조정
        // 이 코드는 레이아웃이 시스템 바 아래로 확장될 때 콘텐츠가 시스템 바에 가려지지 않도록 함
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            // `main_layout`은 해당 액티비티의 최상위 레이아웃 ID여야 함
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // 시스템 바의 인셋만큼 뷰의 좌, 상, 우, 하 패딩을 설정
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    // 언더바 아이콘 클릭 메소드
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