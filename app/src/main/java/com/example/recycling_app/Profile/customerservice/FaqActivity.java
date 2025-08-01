package com.example.recycling_app.Profile.customerservice;

import androidx.appcompat.app.AppCompatActivity; // Android 기본 Activity 클래스
import android.os.Bundle; // Activity 상태 저장 및 복원 시 사용
import android.view.View; // UI 컴포넌트의 기본 클래스
import android.widget.ImageView; // 이미지 뷰
import android.widget.LinearLayout; // LinearLayout 위젯 (레이아웃 그룹)
import android.widget.TextView; // 텍스트 뷰
import android.widget.Toast; // 짧은 메시지 팝업

import androidx.recyclerview.widget.LinearLayoutManager; // RecyclerView의 항목을 선형으로 배열
import androidx.recyclerview.widget.RecyclerView; // 스크롤 가능한 대량의 항목을 효율적으로 표시

import com.example.recycling_app.R;
import com.example.recycling_app.dto.FaqDTO; // FAQ 데이터 DTO
import com.example.recycling_app.service.ProfileApiService; // 백엔드 프로필 API 서비스 인터페이스
import com.example.recycling_app.Network.RetrofitClient; // Retrofit 클라이언트 (API 서비스 인스턴스 생성)

import java.util.ArrayList; // 동적 배열 리스트
import java.util.List; // 리스트 컬렉션

import retrofit2.Call; // Retrofit의 비동기 네트워크 요청 객체
import retrofit2.Callback; // Retrofit의 비동기 응답 처리 콜백
import retrofit2.Response; // Retrofit의 HTTP 응답 객체

// FAQ(자주 묻는 질문) 목록을 표시하는 Activity
public class FaqActivity extends AppCompatActivity {

    // UI 요소들을 위한 변수 선언
    private ImageView backArrowIcon; // 뒤로가기 화살표 아이콘
    private TextView faqTitle; // 화면 제목 텍스트
    private RecyclerView faqRecyclerView; // FAQ 목록을 표시할 RecyclerView
    private LinearLayout tvNoFaqsContainer; // FAQ가 없을 때 표시될 메시지를 담는 컨테이너
    private FaqAdapter adapter; // RecyclerView에 데이터를 연결할 어댑터
    private List<FaqDTO> faqList; // FAQ 데이터 리스트 (FaqDTO 타입)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq); // 레이아웃 파일을 이 Activity에 연결

        // UI 요소 초기화 (XML 레이아웃의 ID와 연결)
        backArrowIcon = findViewById(R.id.back_arrow_icon);
        faqTitle = findViewById(R.id.faq_title);
        faqRecyclerView = findViewById(R.id.faq_recycler_view);
        tvNoFaqsContainer = findViewById(R.id.tv_no_faqs_container); // "FAQ 없음" 메시지 컨테이너

        // RecyclerView 설정
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // 세로 스크롤 가능한 선형 레이아웃 설정
        faqList = new ArrayList<>(); // FAQ 데이터를 담을 빈 리스트 초기화
        adapter = new FaqAdapter(faqList); // 어댑터에 데이터 리스트 연결 (외부 FaqAdapter 사용)
        faqRecyclerView.setAdapter(adapter); // RecyclerView에 어댑터 설정

        // 뒤로가기 아이콘 클릭 리스너 설정
        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                onBackPressed(); // Android의 뒤로가기 동작 수행 (이전 화면으로 돌아감)
            }
        });

        // FAQ 데이터 로드 메서드 호출
        loadFaqs();
    }

    // 백엔드에서 FAQ 데이터를 비동기로 로드하는 메서드
    private void loadFaqs() {
        // RetrofitClient를 통해 ProfileApiService 인터페이스의 구현체(API 서비스 객체)를 가져옴
        ProfileApiService apiService = RetrofitClient.getProfileApiService();

        // API 서비스의 `getAllFaqs` 메서드 호출하여 네트워크 요청 전송
        // `enqueue` 메서드는 비동기적으로 요청을 실행하고, 응답이 오면 `Callback` 인터페이스의 메서드를 호출
        apiService.getAllFaqs().enqueue(new Callback<List<FaqDTO>>() {
            @Override
            public void onResponse(Call<List<FaqDTO>> call, Response<List<FaqDTO>> response) {
                // HTTP 응답이 성공적일 때 (2xx 코드)
                if (response.isSuccessful() && response.body() != null) {
                    faqList.clear(); // 기존 데이터 모두 지우기
                    faqList.addAll(response.body()); // 새로 받아온 데이터 추가
                    adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경을 알려 RecyclerView 업데이트

                    // 로드된 FAQ 목록이 비어있는지에 따라 "FAQ 없음" 메시지 또는 RecyclerView 표시
                    if (faqList.isEmpty()) {
                        tvNoFaqsContainer.setVisibility(View.VISIBLE); // "FAQ 없음" 컨테이너 보이기
                        faqRecyclerView.setVisibility(View.GONE); // RecyclerView 숨기기
                    } else {
                        tvNoFaqsContainer.setVisibility(View.GONE); // "FAQ 없음" 컨테이너 숨기기
                        faqRecyclerView.setVisibility(View.VISIBLE); // RecyclerView 보이기
                    }
                    Toast.makeText(FaqActivity.this, "FAQ 로드 성공", Toast.LENGTH_SHORT).show();
                } else {
                    // HTTP 응답이 실패했을 때 (4xx, 5xx 코드 등)
                    String errorMessage = "FAQ 로드 실패";
                    try {
                        // 에러 본문이 있다면 메시지에 추가
                        if (response.errorBody() != null) {
                            errorMessage += ": " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력 (디버깅용)
                    }
                    Toast.makeText(FaqActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    tvNoFaqsContainer.setVisibility(View.VISIBLE); // 실패 시에도 "FAQ 없음" 메시지 표시
                    faqRecyclerView.setVisibility(View.GONE); // RecyclerView 숨기기
                }
            }

            @Override
            public void onFailure(Call<List<FaqDTO>> call, Throwable t) {
                // 네트워크 요청 자체가 실패했을 때 (예: 인터넷 연결 없음, 서버 응답 없음)
                Toast.makeText(FaqActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace(); // 오류 스택 트레이스 출력 (디버깅용)
                tvNoFaqsContainer.setVisibility(View.VISIBLE); // 네트워크 오류 시에도 "FAQ 없음" 메시지 표시
                faqRecyclerView.setVisibility(View.GONE); // RecyclerView 숨기기
            }
        });
    }
}