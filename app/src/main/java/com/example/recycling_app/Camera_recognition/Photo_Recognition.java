package com.example.recycling_app.Camera_recognition;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.recycling_app.Account.LoginActivity;
import com.example.recycling_app.BuildConfig;
import com.example.recycling_app.Location.LocationActivity;
import com.example.recycling_app.MainscreenActivity;
import com.example.recycling_app.Profile.MypageActivity;
import com.example.recycling_app.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Photo_Recognition extends AppCompatActivity {

    private ImageView resultImageView;
    private TextView titleTextView;
    private TextView resultTextView;
    private ProgressBar progressBar;
    private ImageButton recycling_baseline_list;
    private FirebaseFirestore db;
    private final List<Module> pytorchModules = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isModelLoaded = new AtomicBoolean(false);

    // 현재 인식된 분류 결과 저장
    private String currentClassification = "";

    // Gemini API 응답을 저장할 캐시 맵 추가
    private final Map<String, String> geminiCache = new HashMap<>();
    private final String[] modelFiles = {"Train_model8.ptl", "Train_model13.ptl"};
    private final String[] classLabels = new String[]{"금속캔", "비닐", "스티로폼", "유리", "종이", "플라스틱", "페트병"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.photo_recognition_result);

        resultImageView = findViewById(R.id.resultImageView);
        titleTextView = findViewById(R.id.titleTextView);
        resultTextView = findViewById(R.id.resultTextView);
        progressBar = findViewById(R.id.progressBar);
        recycling_baseline_list = findViewById(R.id.recycling_baseline_list);

        // Firebase Firestore 초기화
        db = FirebaseFirestore.getInstance();

        setupBottomNavigation();
        setupSystemBars();
        loadPytorchModels();

        // 오른쪽 아래 버튼 클릭 리스너 추가
        setupOptionsButton();

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString == null) {
            Toast.makeText(this, "오류: 이미지 경로를 받지 못했습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Uri imageUri = Uri.parse(imageUriString);
        Glide.with(this).load(imageUri).into(resultImageView);

        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            while (!isModelLoaded.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            runAnalysis(imageUri);
        });

        // EdgeToEdge 관련 코드: 시스템 바(상단바, 하단바)의 인셋을 고려하여 뷰의 패딩을 조정
        // 이 코드는 레이아웃이 시스템 바 아래로 확장될 때 콘텐츠가 시스템 바에 가려지지 않도록 함
        // 시스템 바의 인셋만큼 뷰의 좌, 상, 우, 하 패딩을 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 상단바 아이콘과 글씨 색상을 어둡게 설정 (Light Mode)
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
    }

    // 오른쪽 아래 옵션 버튼 설정
    private void setupOptionsButton() {
        recycling_baseline_list.setOnClickListener(v -> showOptionsDialog());
    }

    // 옵션 선택 다이얼로그 표시
    private void showOptionsDialog() {
        // 1. 커스텀 XML 레이아웃을 inflate하여 View 객체로 만듭니다.
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_selection_list, null);

        // 2. 다이얼로그에 표시할 데이터
        String[] recyclingTypes = {
                "플라스틱", "유리병", "금속", "비닐", "종이", "페트병", "스티로폼", "폐휴대폰", "폐소형가전", "폐의약품", "의류", "폐건전지", "폐형광등"
        };

        // 3. ListView와 ArrayAdapter를 사용하여 데이터와 뷰를 연결합니다.
        ListView recyclingListView = dialogView.findViewById(R.id.recycling_list_view);
        // ArrayAdapter를 생성할 때 커스텀 레이아웃 사용
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dialog_selection_list_style, R.id.recycling_type_text, recyclingTypes);
        recyclingListView.setAdapter(adapter);

        // 4. AlertDialog.Builder를 사용하여 다이얼로그를 설정합니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setView(dialogView);

        // 5. 다이얼로그를 생성하고 리스트뷰의 클릭 리스너를 설정합니다.
        AlertDialog dialog = builder.create();
        recyclingListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = recyclingTypes[position];
            updateClassificationResult(selectedType);
            Toast.makeText(this, selectedType + "(으)로 변경되었습니다.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // 6. 취소 버튼을 추가합니다 (선택 사항)
        TextView cancelButton = dialogView.findViewById(R.id.dialog_title);

        dialog.show();
    }

    // 선택된 분류로 결과 업데이트
    private void updateClassificationResult(String newClassification) {
        currentClassification = newClassification;

        // 이미지 업데이트
        int resultImageId = getDrawableIdForClassification(newClassification);
        resultImageView.setImageResource(resultImageId);

        // 제목 업데이트
        SpannableStringBuilder titleTextBuilder = new SpannableStringBuilder();
        Drawable drawable = getResources().getDrawable(R.drawable.recyclecontainer, getTheme());
        int size = (int) (titleTextView.getTextSize() * 1.2);
        drawable.setBounds(0, 0, size, size);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        titleTextBuilder.append(" ", imageSpan, 0)
                .append(" ")
                .append("분리수거 종류 : ")
                .append(newClassification);
        titleTextView.setText(titleTextBuilder);

        // Firebase에서 분리수거 정보 가져오기
        progressBar.setVisibility(View.VISIBLE);
        getRecyclingDataFromFirebase(newClassification);
    }

    private void getRecyclingDataFromFirebase(String classificationType) {
        // Firebase 문서 이름 매핑 (필요에 따라 조정)
        String documentName = getFirebaseDocumentName(classificationType);

        db.collection("Separate_recycling")
                .document(documentName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);
                    if (documentSnapshot.exists()) {
                        // 문서에서 데이터 추출
                        // instructions 필드는 List<String> 타입으로 가져옵니다.
                        List<String> instructions = (List<String>) documentSnapshot.get("instructions");
                        String description = documentSnapshot.getString("description"); // description 필드가 있다면 가져옴

                        // 데이터 조합하여 표시
                        StringBuilder resultText = new StringBuilder();

                        if (description != null && !description.isEmpty()) {
                            resultText.append(description).append("\n\n");
                        }

                        if (instructions != null && !instructions.isEmpty()) {
                            // 배열의 각 항목을 반복문을 통해 StringBuilder에 추가
                            for (String instruction : instructions) {
                                resultText.append("• ").append(instruction).append("\n\n");
                            }
                        }

                        // 결과가 비어있지 않으면 표시, 비어있으면 기본 메시지
                        if (resultText.length() > 0) {
                            resultTextView.setText(resultText.toString().trim());
                        } else {
                            resultTextView.setText("'" + classificationType + "'(으)로 분류되었습니다.\n분리수거 정보를 불러오는 중입니다.");
                        }
                    } else {
                        // 문서가 존재하지 않는 경우 Gemini API 사용
                        Log.d("Firebase", "문서가 존재하지 않음: " + documentName + ", Gemini API 사용");
                        askGemini(classificationType);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Firebase", "데이터 가져오기 실패", e);
                    // Firebase 실패 시 Gemini API로 대체
                    askGemini(classificationType);
                });
    }

    // 분류 이름을 Firebase 문서 이름으로 매핑
    private String getFirebaseDocumentName(String classification) {
        switch (classification) {
            case "플라스틱": return "plastic";
            case "유리병": return "glass_bottle";
            case "금속캔": return "metal";
            case "비닐": return "plasticbag";
            case "종이": return "paper";
            case "페트병": return "plastic_bottle";
            case "스티로폼": return "styrofoam";
            case "폐의약품": return "unused_medicines";
            case "의류": return "used_clothing";
            case "폐건전지": return "waste_battery";
            case "폐형광등": return "waste_fluorescent_lamp";
            case "폐휴대폰": return "waste_phone";
            case "폐소형가전": return "waste_home_appliances";

            default: return classification.toLowerCase();
        }
    }

    private void setupSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }
    }

    private void loadPytorchModels() {
        executorService.execute(() -> {
            try {
                for (String modelFile : modelFiles) {
                    pytorchModules.add(LiteModuleLoader.load(assetFilePath(modelFile)));
                }
                isModelLoaded.set(true);
                Log.d("PhotoRecognition", "PyTorch 모델 로딩 완료");
            } catch (IOException e) {
                Log.e("PhotoRecognition", "모델 로딩 실패", e);
                runOnUiThread(() -> Toast.makeText(this, "모델 로딩 실패", Toast.LENGTH_LONG).show());
            }
        });
    }

    private void runAnalysis(Uri localUri) {
        try {
            String classificationResult = runEnsembleInference(localUri);
            if (classificationResult == null) {
                throw new RuntimeException("PyTorch 모델 분석에 실패했습니다.");
            }
            runOnUiThread(() -> resultTextView.setText("'" + classificationResult + "'(으)로 인식되었습니다."));
            askGemini(classificationResult);
        } catch (Exception e) {
            Log.e("PhotoRecognition", "분석 중 오류 발생", e);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                resultTextView.setText("오류가 발생했습니다: " + e.getMessage());
            });
        }
    }

    // 캐싱 로직이 추가된 Gemini API 호출 메서드
    private void askGemini(String photoresultname) {
        // 캐시에 이미 결과가 있는지 확인
        if (geminiCache.containsKey(photoresultname)) {
            Log.d("GeminiCache", "캐시된 결과 사용: " + photoresultname);
            String cachedResult = geminiCache.get(photoresultname);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                displayResults(photoresultname, cachedResult);
            });
            return; // 캐시된 결과가 있으면 API 호출 없이 바로 반환
        }

        GenerativeModel gm = new GenerativeModel("gemini-2.5-pro", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        String prompt = String.format(
                "'" + photoresultname + "'분리수거 방법을 알려주세요. 아래 조건들을 반드시 지켜주세요:\n" +
                        "단계별로 번호를 붙여 설명" +
                        "각 단계는 간결한 문장으로 작성" +
                        "특수문자나 기호는 사용하지 마세요." +
                        "각 단계 사이에 한 줄씩 띄어주세요." +
                        "방법은 최대 3개로 해서 압축해서 알려주세요"
        );
        Content content = new Content.Builder().addText(prompt).build();

        Futures.addCallback(model.generateContent(content), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                // **개선:** 새로운 결과를 캐시에 저장
                geminiCache.put(photoresultname, resultText);
                Log.d("GeminiCache", "새로운 결과 캐싱: " + photoresultname);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    displayResults(photoresultname, resultText);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("GeminiAPI", "API 호출 실패", t);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Photo_Recognition.this, "상세 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(Photo_Recognition.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                });
            }
        }, MoreExecutors.directExecutor());
    }

    private void displayResults(String classification, String details) {
        int resultImageId = getDrawableIdForClassification(classification);
        resultImageView.setImageResource(resultImageId);

        SpannableStringBuilder titleTextBuilder = new SpannableStringBuilder();
        Drawable drawable = getResources().getDrawable(R.drawable.recyclecontainer, getTheme());
        int size = (int) (titleTextView.getTextSize() * 1.2);
        drawable.setBounds(0, 0, size, size);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        titleTextBuilder.append(" ", imageSpan, 0)
                .append(" ")
                .append("분리수거 종류 : ")
                .append(classification);
        titleTextView.setText(titleTextBuilder);

        resultTextView.setText(details);
    }

    @DrawableRes
    private int getDrawableIdForClassification(String classification) {
        switch (classification) {
            case "금속": return R.drawable.metal_recycle_img;
            case "비닐": return R.drawable.plasticbag_recycle_img;
            case "스티로폼": return R.drawable.styrofoam_recycle_img;
            case "유리병": return R.drawable.glassbottle_recycle_img;
            case "종이": return R.drawable.paper_recycle_img;
            case "플라스틱": return R.drawable.plastic_recycle_img;
            case "페트병": return R.drawable.plastic_bottle_recycle_img;
            default: return R.drawable.recycle_prohibition;
        }
    }

    private Bitmap getBitmapFromDrawable(int drawableId) {
        Drawable drawable = getResources().getDrawable(drawableId, getTheme());
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private String runEnsembleInference(Uri uri) throws IOException {
        // 이미 모델이 로딩되었는지 확인하는 로직은 loadPytorchModels에서 처리됨
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

        float[] aggregatedProbabilities = new float[classLabels.length];

        for (Module module : pytorchModules) {
            final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
            final float[] logits = outputTensor.getDataAsFloatArray();
            final float[] probabilities = softmax(logits);

            for (int i = 0; i < probabilities.length; i++) {
                aggregatedProbabilities[i] += probabilities[i];
            }
        }

        float maxProbability = -Float.MAX_VALUE;
        int maxProbIdx = -1;
        for (int i = 0; i < aggregatedProbabilities.length; i++) {
            if (aggregatedProbabilities[i] > maxProbability) {
                maxProbability = aggregatedProbabilities[i];
                maxProbIdx = i;
            }
        }

        return (maxProbIdx != -1) ? classLabels[maxProbIdx] : null;
    }

    private float[] softmax(float[] logits) {
        float[] probabilities = new float[logits.length];
        float maxLogit = -Float.MAX_VALUE;
        for (float logit : logits) {
            if (logit > maxLogit) {
                maxLogit = logit;
            }
        }

        float sumExp = 0.0f;
        for (int i = 0; i < logits.length; i++) {
            probabilities[i] = (float) Math.exp(logits[i] - maxLogit);
            sumExp += probabilities[i];
        }

        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= sumExp;
        }
        return probabilities;
    }

    public String assetFilePath(String assetName) throws IOException {
        File file = new File(getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        try (InputStream is = getAssets().open(assetName); OutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        }
        return file.getAbsolutePath();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private void setupBottomNavigation() {
        ImageButton homeIcon = findViewById(R.id.home_icon);
        ImageButton mapIcon = findViewById(R.id.map_icon);
        ImageButton cameraIcon = findViewById(R.id.camera_icon);
        ImageButton messageIcon = findViewById(R.id.message_icon);
        ImageButton accountIcon = findViewById(R.id.account_icon);

        homeIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Photo_Recognition.this, MainscreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        mapIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Photo_Recognition.this, LocationActivity.class);
            startActivity(intent);
        });

        cameraIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Photo_Recognition.this, CameraActivity.class);
            startActivity(intent);
        });

        accountIcon.setOnClickListener(v -> {
            Intent intent = new Intent(Photo_Recognition.this, MypageActivity.class);
            startActivity(intent);
        });
    }
}