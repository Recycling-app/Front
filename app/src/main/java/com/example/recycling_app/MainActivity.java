package com.example.recycling_app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

// MainActivity 클래스는 앱의 메인 화면을 담당합니다.
public class MainActivity extends AppCompatActivity {

    // 액티비티가 처음 생성될 때 호출되는 메서드입니다.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 부모 클래스의 onCreate 메서드를 호출합니다.

        // start.xml 레이아웃 파일을 이 액티비티의 화면으로 설정합니다.
        // R.layout.start는 res/layout/start.xml 파일을 참조합니다.
        setContentView(R.layout.start);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA)
                .check();
    }
}
