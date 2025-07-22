package com.example.recycling_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CammeraActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageview);

        findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if( intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try { // 파일 쓰기를 할때는 항상 try catch 문을 적어야함 !
                        photoFile = createImageFile();
                    } catch (IOException e) {
                    }
                    if(photoFile != null) {
                        photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(),photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                        startActivityResult.launch(intent);
                    }
                }
            }

            private File createImageFile() throws IOException {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "TEST_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                );
                imageFilePath = image.getAbsolutePath();
                return image;
            }

            ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            //원하는 기능 작성
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                                ExifInterface exif = null;

                                try {
                                    exif = new ExifInterface(imageFilePath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                int exifOrientation;
                                int exifDegree;

                                if (exif != null) {
                                    exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                                    exifDegree = exifOrientationToDegress(exifOrientation);
                                } else {
                                    exifDegree = 0;
                                }

                                ((ImageView) findViewById(R.id.iv)).setImageBitmap(rotate(bitmap, exifDegree));
                            }
                        }
                    });

            private int exifOrientationToDegress(int exifOrientation) {
                if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90){
                    return 90;
                } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
                    return 180;
                } else if ((exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)) {
                    return 270;
                }
                return 0;
            }

            private Bitmap rotate(Bitmap bitmap, int exifDegree) {
                Matrix matrix = new Matrix();
                matrix.postRotate(exifDegree);
                return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            }

        });
    }
}