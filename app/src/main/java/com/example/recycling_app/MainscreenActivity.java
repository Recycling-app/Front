package com.example.recycling_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen);

        ImageButton camerabox = findViewById(R.id.camera_box);
        ImageButton mapbox = findViewById(R.id.map_box);
        ImageButton howtobox = findViewById(R.id.howto_box);
        ImageButton communitybox = findViewById(R.id.community_box);

        ImageButton mapicon = findViewById(R.id.map_icon);
        ImageButton cameraicon = findViewById(R.id.camera_icon);
        ImageButton messageicon = findViewById(R.id.message_icon);
        ImageButton accounticon = findViewById(R.id.account_icon);


        // 카메라 이동
        camerabox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        cameraicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });


        // 지도 이동
        mapbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, LocationActivity.class);
                startActivity(intent);
            }
        });

        mapicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, LocationActivity.class);
                startActivity(intent);
            }
        });


        /*      <!-- Acitvity 미정 -->

        // 재활용 방법 이동
        howtobox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, Activity.class);
                startActivity(intent);
            }
        });


        // 커뮤니티 이동
        communitybox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, Activity.class);
                startActivity(intent);
            }
        });

        messageicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, Activity.class);
                startActivity(intent);
            }
        });

         */


        // 마이페이지 이동
        accounticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, MypageActivity.class);
                startActivity(intent);
            }
        });


    }

}
