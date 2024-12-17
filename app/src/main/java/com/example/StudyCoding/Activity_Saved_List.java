package com.example.StudyCoding;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.StudyCoding.Fragment.Fragment_List_Code;
import com.example.StudyCoding.Fragment.Fragment_List_Problem;

public class Activity_Saved_List extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_list);

        // Button 초기화
        Button showProblemListButton = findViewById(R.id.showProblemListButton);
        Button showCodeListButton = findViewById(R.id.showCodeListButton);
        ImageButton homeButton = findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료 (이전 액티비티로 돌아감)
            }
        });



        // 버튼 클릭 시 Fragment 전환
        showProblemListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Fragment_List_Problem());
            }
        });

        showCodeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new Fragment_List_Code());
            }
        });

        // 기본 Fragment 설정 (초기 화면)
        if (savedInstanceState == null) {
            loadFragment(new Fragment_List_Problem());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
