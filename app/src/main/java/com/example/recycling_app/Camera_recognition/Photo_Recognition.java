package com.example.recycling_app.Camera_recognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.recycling_app.BuildConfig;
import com.example.recycling_app.Howtobox.Wasteguide;
import com.example.recycling_app.LocationActivity;
import com.example.recycling_app.MainscreenActivity;
import com.example.recycling_app.MypageActivity;
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
    private TextView resultTextView;
    private ProgressBar progressBar;
    private final List<Module> pytorchModules = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final String[] modelFiles = {"Train_model8.ptl", "Train_model13.ptl"};
    private final String[] classLabels = new String[]{"건전지", "금속", "비닐", "스티로폼", "유리", "종이", "종이박스", "플라스틱", "형광등"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // Edge-to-Edge UI를 활성화
        setContentView(R.layout.photo_recognition_result); // 레이아웃을 설정

        resultImageView = findViewById(R.id.resultImageView);
        resultTextView = findViewById(R.id.resultTextView);
        progressBar = findViewById(R.id.progressBar);

        setupBottomNavigation(); // 하단 내비게이션 아이콘들의 클릭 이벤트를 설정하는 메서드
        applyWindowInsets(); // 시스템 UI와 여백을 맞추는 로직을 적용

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString == null) {
            Toast.makeText(this, "오류: 이미지 경로를 받지 못했습니다.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Uri imageUri = Uri.parse(imageUriString);
        Glide.with(this).load(imageUri).into(resultImageView);

        progressBar.setVisibility(View.VISIBLE);
        resultTextView.setText("쓰레기를 인식하고 있습니다...");
        executorService.execute(() -> runAnalysis(imageUri));

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

    //학습 모델을 이용해서 쓰레기 인식
    private void runAnalysis(Uri localUri) {
        try {
            String classificationResult = runEnsembleInference(localUri);
            if (classificationResult == null) {
                throw new RuntimeException("PyTorch 모델 분석에 실패했습니다.");
            }
            runOnUiThread(() -> resultTextView.setText("'" + classificationResult + "'(으)로 인식되었습니다.\n자세한 정보를 조회합니다..."));

            askGemini(classificationResult);

        } catch (Exception e) {
            Log.e("PhotoRecognition", "분석 중 오류 발생", e);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                resultTextView.setText("오류가 발생했습니다: " + e.getMessage());
            });
        }
    }

    // 인식한 쓰레기를 토대로 Gemini API 호출 및 답변
    private void askGemini(String topic) {

        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        String prompt = "'" + topic + "'의 올바른 분리수거 방법을 단계별로 특수기호, 특수문자 빼고 설명할 때 앞에 1., 2., 3. 이런 거 붙여서 순서마다 한줄 씩 띄어서 분리수거 방법을 알려주세요.";
        Content content = new Content.Builder().addText(prompt).build();

        Futures.addCallback(model.generateContent(content), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    resultTextView.setText(resultText);
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("GeminiAPI", "API 호출 실패", t);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    resultTextView.setText("상세 정보를 가져오는 데 실패했습니다.\n" + t.getMessage());
                });
            }
        }, MoreExecutors.directExecutor());
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
}