package com.example.StudyCoding;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.StudyCoding.Fragment.Adapter.Adapter_ViewPager;

public class Activity_ViewPager_SavedProblemWithCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_parent);
        String selectedUrl = getIntent().getStringExtra("selected_url");
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        Adapter_ViewPager adapter = new Adapter_ViewPager(this, selectedUrl);
        viewPager.setAdapter(adapter);
    }
}
