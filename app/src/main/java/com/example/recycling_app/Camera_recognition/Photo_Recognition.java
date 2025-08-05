package com.example.recycling_app.Camera_recognition;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Photo_Recognition extends AppCompatActivity {

    private ImageView resultImageView;
    private TextView titleTextView; // titleTextView 추가
    private TextView resultTextView;
    private ProgressBar progressBar;
    private final List<Module> pytorchModules = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final String[] modelFiles = {"Train_model8.ptl", "Train_model13.ptl"};
    private final String[] classLabels = new String[]{"건전지", "금속", "비닐", "스티로폼", "유리", "종이", "종이박스", "플라스틱", "형광등"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.photo_recognition_result);

        resultImageView = findViewById(R.id.resultImageView);
        titleTextView = findViewById(R.id.titleTextView); // titleTextView 초기화
        resultTextView = findViewById(R.id.resultTextView);
        progressBar = findViewById(R.id.progressBar);

        setupBottomNavigation();

        // 상단바 색상 변경 코드 추가
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.setAppearanceLightStatusBars(true);
        }

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString == null) {
            Toast.makeText(this, "오류: 이미지 경로를 받지 못했습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Uri imageUri = Uri.parse(imageUriString);
        Glide.with(this).load(imageUri).into(resultImageView);

        progressBar.setVisibility(View.VISIBLE);
        executorService.execute(() -> runAnalysis(imageUri));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

    private void askGemini(String photoresultname) {
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        String prompt = String.format(
                "'" + photoresultname + "'분리수거 방법을 알려주세요. 아래 조건들을 반드시 지켜주세요:\n" +
                        "1. 단계별로 번호를 붙여 설명해주세요 (예: 1. , 2. , 3. ).\n" +
                        "2. 각 단계는 명확하고 간결한 문장으로 작성해주세요.\n" +
                        "3. 특수문자나 기호는 사용하지 마세요.\n" +
                        "4. 각 단계 사이에 한 줄씩 띄어주세요."
        );
        Content content = new Content.Builder().addText(prompt).build();

        Futures.addCallback(model.generateContent(content), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> {
                    // ProgressBar를 숨기고 결과를 표시
                    progressBar.setVisibility(View.GONE);
                    displayResults(photoresultname, resultText);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("GeminiAPI", "API 호출 실패", t);
                runOnUiThread(() -> {
                    // ProgressBar를 숨기고 오류 메시지 표시 후 로그인 화면으로 이동
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Photo_Recognition.this, "상세 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(Photo_Recognition.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();
                });
            }
        }, MoreExecutors.directExecutor());
    }

    // PyTorch 모델 분석 결과에 맞는 이미지와 Gemini API 결과를 표시하는 메서드
    private void displayResults(String classification, String details) {
        int resultImageId = getDrawableIdForClassification(classification);
        resultImageView.setImageResource(resultImageId);

        // 이미지와 텍스트를 조합하여 titleTextView에 표시
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

        // Gemini API 응답을 resultTextView에 표시
        resultTextView.setText(details);
    }

    // 분류 결과에 따라 다른 Drawable 리소스 ID를 반환하는 메서드
    @DrawableRes
    private int getDrawableIdForClassification(String classification) {
        switch (classification) {
            case "건전지": return R.drawable.battery_recycle_img;
            case "금속": return R.drawable.metal_recycle_img;
            case "비닐": return R.drawable.plasticbag_recycle_img;
            case "스티로폼": return R.drawable.styrofoam_recycle_img;
            case "유리": return R.drawable.glassbottle_recycle_img;
            case "종이": return R.drawable.paper_recycle_img;
            case "종이박스": return R.drawable.paperbox_recycle_img;
            case "플라스틱": return R.drawable.plastic_recycle_img;
            case "형광등": return R.drawable.fluorescent_lamp_recycle_img;
            default: return R.drawable.recycle_prohibition;
        }
    }

    // Drawable 리소스를 Bitmap으로 변환하는 함수
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
        if (pytorchModules.isEmpty()) {
            for (String modelFile : modelFiles) {
                pytorchModules.add(LiteModuleLoader.load(assetFilePath(modelFile)));
            }
        }

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

    // 하단 내비게이션 아이콘들의 클릭 이벤트를 설정하는 메서드
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