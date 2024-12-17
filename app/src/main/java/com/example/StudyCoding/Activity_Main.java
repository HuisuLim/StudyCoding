package com.example.StudyCoding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Main extends AppCompatActivity {

    private ImageButton codeButton, searchButton, listButton, quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 레이아웃 파일 이름 확인

        // 커스텀 ImageButton 초기화
        codeButton = findViewById(R.id.codeButton);
        searchButton = findViewById(R.id.searchButton);
        listButton = findViewById(R.id.ListButton);
        quitButton = findViewById(R.id.terminateButton);

        // 클릭 리스너 설정
        codeButton.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Main.this, Activity_Code_Executer.class);
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Main.this, Activity_Search_Problem.class);
            startActivity(intent);
        });

        listButton.setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Main.this, Activity_Saved_List.class);
            startActivity(intent);
        });

        quitButton.setOnClickListener(v -> {
            Toast.makeText(Activity_Main.this, "앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            finishAffinity(); // 현재 액티비티 스택을 모두 종료
            System.exit(0); // 프로세스 종료
        });

    }
}
