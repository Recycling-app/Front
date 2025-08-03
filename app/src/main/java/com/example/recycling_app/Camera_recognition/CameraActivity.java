package com.example.recycling_app.Camera_recognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.recycling_app.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final String TAG = "CameraActivity";

    private PreviewView cameraPreview;
    private Button captureButton;
    private ProgressBar progressBar;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private View recognitionFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameraview);

        cameraPreview = findViewById(R.id.cameraPreview);
        captureButton = findViewById(R.id.button_capture);
        progressBar = findViewById(R.id.progressBar);
        recognitionFrame = findViewById(R.id.recognition_frame);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        captureButton.setOnClickListener(v -> takePhoto());
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "카메라 시작 실패", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        progressBar.setVisibility(View.VISIBLE);
        captureButton.setEnabled(false);

        // 원본 이미지를 저장할 파일
        File photoFile = new File(getCacheDir(), "original_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        try {
                            // 1. 저장된 원본 이미지 파일을 Bitmap으로 로드
                            Bitmap originalBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                            // 2. 이미지 회전 문제 해결
                            originalBitmap = rotateImageIfRequired(originalBitmap, photoFile.getAbsolutePath());

                            // 3. PreviewView와 원본 이미지의 비율 계산
                            // PreviewView의 크기와 실제 이미지의 크기는 다를 수 있으므로 비율을 맞춰줘야 합니다.
                            float previewWidth = cameraPreview.getWidth();
                            float previewHeight = cameraPreview.getHeight();
                            float imageWidth = originalBitmap.getWidth();
                            float imageHeight = originalBitmap.getHeight();

                            // PreviewView의 fillCenter 스케일 타입을 가정하여 비율 계산
                            // (이미지의 짧은 쪽이 PreviewView의 짧은 쪽에 맞춰지고 긴 쪽은 잘려나감)
                            float scaleFactor = Math.max(previewWidth / imageWidth, previewHeight / imageHeight);

                            // 화면에 보이는 이미지 영역의 실제 이미지 내 크기 계산
                            float scaledImageWidth = imageWidth * scaleFactor;
                            float scaledImageHeight = imageHeight * scaleFactor;

                            // 화면에 보이는 이미지의 시작점(좌상단) 계산
                            float imageStartX = (previewWidth - scaledImageWidth) / 2;
                            float imageStartY = (previewHeight - scaledImageHeight) / 2;

                            // 4. recognition_frame의 화면상 좌표를 이미지 좌표로 변환
                            int frameLeft = (int) ((recognitionFrame.getLeft() - imageStartX) / scaleFactor);
                            int frameTop = (int) ((recognitionFrame.getTop() - imageStartY) / scaleFactor);
                            int frameWidth = (int) (recognitionFrame.getWidth() / scaleFactor);
                            int frameHeight = (int) (recognitionFrame.getHeight() / scaleFactor);

                            // 5. Bitmap 자르기 (이미지 경계를 벗어나지 않도록 보정)
                            frameLeft = Math.max(0, frameLeft);
                            frameTop = Math.max(0, frameTop);
                            if (frameLeft + frameWidth > imageWidth) {
                                frameWidth = (int)imageWidth - frameLeft;
                            }
                            if (frameTop + frameHeight > imageHeight) {
                                frameHeight = (int)imageHeight - frameTop;
                            }
                            Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, frameLeft, frameTop, frameWidth, frameHeight);

                            // 6. 잘라낸 Bitmap을 새 파일로 저장
                            String croppedFileName = "cropped_" + System.currentTimeMillis() + ".jpg";
                            File croppedFile = new File(getCacheDir(), croppedFileName);
                            try (FileOutputStream out = new FileOutputStream(croppedFile)) {
                                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            }

                            // 원본 파일은 삭제
                            photoFile.delete();

                            // 7. 잘라낸 이미지의 Uri를 다음 액티비티로 전달
                            Uri savedUri = Uri.fromFile(croppedFile);
                            Intent intent = new Intent(CameraActivity.this, Photo_Recognition.class);
                            intent.putExtra("imageUri", savedUri.toString());
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            Log.e(TAG, "이미지 자르기 또는 저장 실패", e);
                            onError(new ImageCaptureException(ImageCapture.ERROR_FILE_IO, "Failed to crop/save image", e));
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "사진 촬영 실패: " + exception.getMessage(), exception);
                        progressBar.setVisibility(View.GONE);
                        captureButton.setEnabled(true);
                        Toast.makeText(CameraActivity.this, "사진 촬영에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // EXIF 정보에 따라 이미지를 회전시키는 헬퍼 메서드
    private Bitmap rotateImageIfRequired(Bitmap img, String imagePath) throws IOException {
        ExifInterface ei = new ExifInterface(imagePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    // 이미지를 회전시키는 헬퍼 메서드
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}