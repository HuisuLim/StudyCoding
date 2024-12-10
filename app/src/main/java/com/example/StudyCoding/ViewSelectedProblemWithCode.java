package com.example.StudyCoding;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.StudyCoding.Fragment.Adapter.ViewPageAdapter;

public class ViewSelectedProblemWithCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String selectedUrl = getIntent().getStringExtra("selected_url");

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ViewPageAdapter adapter = new ViewPageAdapter(this, selectedUrl);
        viewPager.setAdapter(adapter);
    }
}
