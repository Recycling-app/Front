package com.example.recycling_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// 사용 기록을 보여주는 액티비티
public class UsageHistoryActivity extends AppCompatActivity {

    // UI 요소 선언
    private ImageView backArrowIcon; // 뒤로 가기 아이콘
    private TextView usageHistoryTitle; // 사용 기록 화면 타이틀
    private LinearLayout itemAiRecognitionHistory; // AI 인식 기록 항목

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_history); // 업데이트된 레이아웃 파일 사용

        // UI 요소 초기화
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        usageHistoryTitle = findViewById(R.id.usage_history_title);
        itemAiRecognitionHistory = findViewById(R.id.item_ai_recognition_history);

        // 뒤로가기 아이콘 클릭 리스너 설정
        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onBackPressed(); // 이전 화면으로 돌아감
            }
        });

        // "AI 인식 기록" 클릭 리스너 설정
        itemAiRecognitionHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AiRecognitionHistoryActivity로 이동하는 Intent 생성
                Intent intent = new Intent(UsageHistoryActivity.this, AiRecognitionHistoryActivity.class);
                startActivity(intent); // 액티비티 시작
            }
        });
    }
}