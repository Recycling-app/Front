package com.example.recycling_app.Profile.accountmanagement;

import androidx.appcompat.app.AppCompatActivity; // 안드로이드의 기본 액티비티 클래스
import android.content.Intent; // 다른 액티비티를 시작할 때 사용
import android.os.Bundle; // 액티비티 상태 저장/복원 시 사용
import android.view.View; // UI 컴포넌트의 기본 클래스
import android.widget.ImageView; // 이미지 뷰
import android.widget.LinearLayout; // LinearLayout 위젯 (레이아웃 그룹)
import android.widget.TextView; // 텍스트 뷰

import com.example.recycling_app.Profile.profieedit.PasswordEditActivity;
import com.example.recycling_app.R;

// 계정 관리 기능을 담당하는 액티비티
// 사용자에게 개인 정보 수정, 비밀번호 수정, 회원 탈퇴 등의 메뉴를 제공
public class AccountManagementActivity extends AppCompatActivity {

    // UI 요소들을 위한 변수 선언
    private ImageView backArrowIcon; // 뒤로가기 화살표 아이콘
    private TextView accountManagementTitle; // 화면 제목 텍스트
    private LinearLayout itemPersonalInfoEdit; // '개인 정보 수정' 메뉴 아이템 (LinearLayout)
    private LinearLayout itemPasswordEdit;     // '비밀번호 수정' 메뉴 아이템 (LinearLayout)
    private LinearLayout itemAccountDeletion;  // '회원 탈퇴' 메뉴 아이템 (LinearLayout)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management); // 이 액티비티에 연결될 레이아웃 파일 지정

        // UI 요소 초기화 (XML 레이아웃의 ID와 연결)
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        accountManagementTitle = findViewById(R.id.account_management_title);
        itemPersonalInfoEdit = findViewById(R.id.item_personal_info_edit);
        itemPasswordEdit = findViewById(R.id.item_password_edit); // ID 수정
        itemAccountDeletion = findViewById(R.id.item_account_deletion);

        // 뒤로가기 아이콘 클릭 리스너 설정
        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onBackPressed(); // 안드로이드의 뒤로가기 동작 수행 (이전 화면으로 돌아감)
            }
        });

        // "개인 정보 수정" 메뉴 아이템 클릭 리스너
        itemPersonalInfoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PersonalInfoEditActivity로 이동하는 Intent 생성 및 시작
                Intent intent = new Intent(AccountManagementActivity.this, PersonalInfoEditActivity.class);
                startActivity(intent);
            }
        });

        // "비밀번호 수정" 메뉴 아이템 클릭 리스너
        itemPasswordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PasswordEditActivity로 이동하는 Intent 생성 및 시작
                Intent intent = new Intent(AccountManagementActivity.this, PasswordEditActivity.class);
                startActivity(intent);
            }
        });

        // "회원 탈퇴" 메뉴 아이템 클릭 리스너
        itemAccountDeletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AccountDeletionActivity로 이동하는 Intent 생성 및 시작
                Intent intent = new Intent(AccountManagementActivity.this, AccountDeletionActivity.class);
                startActivity(intent);
            }
        });
    }
}