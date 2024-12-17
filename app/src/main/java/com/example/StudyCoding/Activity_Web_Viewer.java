package com.example.StudyCoding;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Web_Viewer extends AppCompatActivity {

    private WebView webView;
    private ImageButton backButton;
    private TextView titleView; // Title TextView 추가
    private String url; // URL 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_viewer); // XML 레이아웃 연결

        // TitleView 참조
        titleView = findViewById(R.id.titleView);

        // WebView 설정
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        // URL 받아서 로드
        url = getIntent().getStringExtra("WEB_URL");
        if (url != null) {
            webView.loadUrl(url);
            updateTitle(url); // 문제 번호 추출 후 TitleView에 설정
        }

        // 뒤로 가기 버튼 설정
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // 현재 Activity 종료
    }

    // 문제 번호를 추출하여 반환하는 메서드
    private String getProblemNumber(String url) {
        if (url != null && url.contains("/")) {
            // 마지막 '/' 이후의 부분을 추출
            String[] parts = url.split("/");
            return parts[parts.length - 1];
        }
        return null; // URL이 유효하지 않을 경우 null 반환
    }

    // TitleView 업데이트 메서드
    private void updateTitle(String url) {
        String problemNumber = getProblemNumber(url);
        if (problemNumber != null) {
            titleView.setText(problemNumber + " Problem");
        } else {
            titleView.setText("WebViewer");
        }
    }
}
