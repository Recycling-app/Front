package com.example.recycling_app.Profile.customerservice;

import androidx.appcompat.app.AppCompatActivity; // Android 기본 Activity 클래스
import android.content.Intent; // 다른 Activity를 시작할 때 사용
import android.os.Bundle; // Activity 상태 저장 및 복원 시 사용
import android.view.View; // UI 컴포넌트의 기본 클래스
import android.widget.ImageView; // 이미지 뷰
import android.widget.LinearLayout; // LinearLayout 위젯 (레이아웃 그룹)
import android.widget.TextView; // 텍스트 뷰

import com.example.recycling_app.R;

// 고객 지원 기능을 담당하는 Activity
// 사용자에게 문의하기, 내 문의 내역 보기, FAQ 등의 메뉴를 제공
public class CustomerSupportActivity extends AppCompatActivity {

    // UI 요소들을 위한 변수 선언
    private ImageView backArrowIcon; // 뒤로가기 화살표 아이콘
    private TextView customerSupportTitle; // 화면 제목 텍스트
    private LinearLayout itemSubmitInquiry; // '문의하기' 메뉴 아이템 (LinearLayout)
    private LinearLayout itemMyInquiries;   // '내 문의 내역' 메뉴 아이템 (LinearLayout)
    private LinearLayout itemFaq;           // 'FAQ' 메뉴 아이템 (LinearLayout)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support); // 이 Activity에 연결될 레이아웃 파일 지정

        // UI 요소 초기화 (XML 레이아웃의 ID와 연결)
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        customerSupportTitle = findViewById(R.id.customer_support_title);

        itemSubmitInquiry = findViewById(R.id.menu_items_container);
        itemMyInquiries = findViewById(R.id.item_inquiry);
        itemFaq = findViewById(R.id.item_faq);

        // 뒤로가기 아이콘 클릭 리스너 설정
        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onBackPressed(); // Android의 뒤로가기 동작 수행 (이전 화면으로 돌아감)
            }
        });

        // "문의하기" 메뉴 아이템 클릭 리스너
        itemSubmitInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SubmitInquiryActivity로 이동하는 Intent 생성 및 시작
                Intent intent = new Intent(CustomerSupportActivity.this, SubmitInquiryActivity.class);
                startActivity(intent);
            }
        });

        // "내 문의 내역" 메뉴 아이템 클릭 리스너
        itemMyInquiries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MyInquiriesActivity로 이동하는 Intent 생성 및 시작
                Intent intent = new Intent(CustomerSupportActivity.this, MyInquiriesActivity.class);
                startActivity(intent);
            }
        });

        // "FAQ" 메뉴 아이템 클릭 리스너
        itemFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FaqActivity로 이동하는 Intent 생성 및 시작
                Intent intent = new Intent(CustomerSupportActivity.this, FaqActivity.class);
                startActivity(intent);
            }
        });
    }
}