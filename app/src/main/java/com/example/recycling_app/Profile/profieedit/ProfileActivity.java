package com.example.recycling_app.Profile.profieedit;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent; // Intent 임포트: 다른 Activity를 시작할 때 사용
import android.os.Bundle; // Bundle 임포트: Activity 상태 저장 및 복원 시 사용
import android.view.View; // View 임포트: UI 컴포넌트의 기본 클래스
import android.widget.ImageView; // ImageView 임포트: 이미지 뷰
import android.widget.TextView; // TextView 임포트: 텍스트 뷰
import android.widget.Toast; // Toast 임포트: 짧은 메시지 팝업

import androidx.activity.EdgeToEdge; // EdgeToEdge 임포트: 화면 전체를 사용하는 기능 활성화
import androidx.core.graphics.Insets; // Insets 임포트: 시스템 바 인셋(inset) 정보
import androidx.core.view.ViewCompat; // ViewCompat 임포트: 뷰 호환성 유틸리티
import androidx.core.view.WindowInsetsCompat; // WindowInsetsCompat 임포트: 윈도우 인셋(inset) 호환성 유틸리티

import com.example.recycling_app.Profile.mysetting.SettingsActivity;
import com.example.recycling_app.Profile.detailsofuse.UsageHistoryActivity;
import com.example.recycling_app.Profile.accountmanagement.AccountManagementActivity;
import com.example.recycling_app.Profile.customerservice.CustomerSupportActivity;
import com.example.recycling_app.R;

//사용자의 프로필 정보를 표시하고, 다양한 마이페이지 메뉴로 이동할 수 있는 Activity.
public class ProfileActivity extends AppCompatActivity {

    // UI 요소들을 위한 변수 선언
    private ImageView backArrowIcon; // 뒤로가기 화살표 아이콘
    private TextView mypageTitle; // 마이페이지 제목 텍스트
    private de.hdodenhof.circleimageview.CircleImageView profileImage; // 원형 프로필 이미지 뷰 (CircleImageView 라이브러리 사용)
    private TextView profileName; // 사용자 프로필 이름 텍스트

    // 메뉴 항목들을 나타내는 View 변수들 (주로 LinearLayout 등 클릭 가능한 컨테이너)
    private View itemProfileEdit;       // '프로필 수정' 메뉴
    private View itemAccountManagement; // '계정 관리' 메뉴
    private View itemUsageHistory;      // '이용 내역' 메뉴
    private View itemSettings;          // '설정' 메뉴
    private View itemCustomerSupport;   // '고객 지원' 메뉴
    private View itemLogout;            // '로그아웃' 메뉴

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // EdgeToEdge 기능을 활성화하여 시스템 바(상단바, 하단바) 영역까지 콘텐츠 확장
        setContentView(R.layout.activity_profile); // 이 Activity에 연결될 레이아웃 파일 지정 (activity_profile.xml)

        // UI 요소 초기화 (XML 레이아웃의 ID와 연결)
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        mypageTitle = findViewById(R.id.mypage_title);
        profileImage = findViewById(R.id.profile_image); // CircleImageView 위젯과 연결
        profileName = findViewById(R.id.profile_name);

        // 각 메뉴 항목 View들을 XML ID로 찾아서 연결
        itemProfileEdit = findViewById(R.id.item_profile_edit);
        itemAccountManagement = findViewById(R.id.item_account_management);
        itemUsageHistory = findViewById(R.id.item_usage_history);
        itemSettings = findViewById(R.id.item_settings);
        itemCustomerSupport = findViewById(R.id.item_customer_support);
        itemLogout = findViewById(R.id.item_logout);

        // 예시 데이터 설정 (실제 앱에서는 사용자 로그인 정보 등을 동적으로 가져와 설정)
        profileName.setText("홍길동");
        // profileImage.setImageResource(R.drawable.your_user_profile_image); // 실제 사용자 이미지로 변경 필요

        // --- 클릭 리스너 설정 ---

        // 뒤로가기 아이콘 클릭 리스너: 클릭 시 이전 화면으로 돌아감
        backArrowIcon.setOnClickListener(v -> onBackPressed());

        // '프로필 수정' 메뉴 클릭 리스너
        itemProfileEdit.setOnClickListener(v -> {
            // ProfileEditActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivity(intent);
        });

        // '계정 관리' 메뉴 클릭 리스너
        itemAccountManagement.setOnClickListener(v -> {
            // AccountManagementActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(ProfileActivity.this, AccountManagementActivity.class);
            startActivity(intent);
        });

        // '이용 내역' 메뉴 클릭 리스너
        itemUsageHistory.setOnClickListener(v -> {
            // UsageHistoryActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(ProfileActivity.this, UsageHistoryActivity.class);
            startActivity(intent);
        });

        // '설정' 메뉴 클릭 리스너
        itemSettings.setOnClickListener(v -> {
            // SettingsActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // '고객 지원' 메뉴 클릭 리스너
        itemCustomerSupport.setOnClickListener(v -> {
            // CustomerSupportActivity로 이동하는 Intent 생성 및 시작
            Intent intent = new Intent(ProfileActivity.this, CustomerSupportActivity.class);
            startActivity(intent);
        });

        // '로그아웃' 메뉴 클릭 리스너
        itemLogout.setOnClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            // TODO: 실제 로그아웃 로직 구현 필요 (예: 사용자 세션 삭제, 로그인 화면으로 리디렉션 등)
            finish(); // 현재 Activity를 종료하여 이전 스택의 Activity로 돌아감 (또는 로그인 화면으로 이동)
        });

        // EdgeToEdge 관련 코드: 시스템 바(상단바, 하단바)의 인셋을 고려하여 뷰의 패딩을 조정
        // 이 코드는 레이아웃 콘텐츠가 시스템 바 아래로 확장될 때, 콘텐츠가 시스템 바에 가려지지 않도록 패딩을 추가
        // `main_layout`은 해당 액티비티의 최상위 레이아웃 ID여야 함
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}