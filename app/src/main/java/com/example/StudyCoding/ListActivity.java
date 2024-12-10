package com.example.StudyCoding;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.StudyCoding.Fragment.CodeListFragment;
import com.example.StudyCoding.Fragment.ProblemListFragment;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combine_2_lists);

        // Button 초기화
        Button showProblemListButton = findViewById(R.id.showProblemListButton);
        Button showCodeListButton = findViewById(R.id.showCodeListButton);

        // 버튼 클릭 시 Fragment 전환
        showProblemListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProblemListFragment());
            }
        });

        showCodeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new CodeListFragment());
            }
        });

        // 기본 Fragment 설정 (초기 화면)
        if (savedInstanceState == null) {
            loadFragment(new ProblemListFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
