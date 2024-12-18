package com.example.StudyCoding.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTaskRepository;
import com.example.StudyCoding.API.APIModels.BrowseAIModels.RobotRetrievedResponse;
import com.example.StudyCoding.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fragment_Saved_Problem extends Fragment {
    private static final String TAG = "SIMPLIFIED_RESPONSE";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.viewpager_problem, container, false);
        String targetUrl = getArguments().getString("url");
        String problemNumber = getProblemNumber(targetUrl);
        ProblemTaskRepository repository = new ProblemTaskRepository(requireContext());

        RobotRetrievedResponse taskResponse = repository.getTaskByUrl(targetUrl);
        if (taskResponse != null) {
            // Result 객체 가져오기
            RobotRetrievedResponse.Result result = taskResponse.getResult();
            // 변수에 저장
            String taskId = result.getId();
            String originUrl = result.getInputParameters().getOriginUrl();
            Map<String, String> capturedTexts = result.getCapturedTexts();
            String constraint = capturedTexts.get("constraint");
            String title = capturedTexts.get("title");
            String problemText = capturedTexts.get("problem_text");
            String input = capturedTexts.get("input");
            String output = capturedTexts.get("output");
            String screenshotUrl = result.getCapturedScreenshots().getProblem_capture().getSrc();
            // Constraint 가져오기
            ArrayList<String> contraintsElement = extractTDContents(constraint);
            String content1 = contraintsElement.get(0);
            String content2 = contraintsElement.get(1);
            String content3 = contraintsElement.get(2);
            String content4 = contraintsElement.get(3);
            String content5 = contraintsElement.get(4);
            String content6 = contraintsElement.get(5);

            //뷰에 contents연결
            TextView titleView = rootView.findViewById(R.id.titleView);
            TextView titleTextView = rootView.findViewById(R.id.title);
            TextView problemTextView = rootView.findViewById(R.id.problemText);
            WebView problemWebView = rootView.findViewById(R.id.problemSrc);
            TextView inputTextView = rootView.findViewById(R.id.input);
            TextView outputTextView = rootView.findViewById(R.id.output);
            TableLayout tableLayout = rootView.findViewById(R.id.constraintTable);
            Button textButton = rootView.findViewById(R.id.textButton);
            Button webButton = rootView.findViewById(R.id.webButton);
            ImageButton homeButton = rootView.findViewById(R.id.homeButton);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() != null) {
                        getActivity().finish(); // 현재 액티비티 종료
                    }
                }
            });

            // TextView 버튼 클릭
            textButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    problemTextView.setVisibility(View.VISIBLE);
                    problemWebView.setVisibility(View.GONE);
                }
            });


            // WebView 버튼 클릭
            webButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    problemTextView.setVisibility(View.GONE);
                    problemWebView.setVisibility(View.VISIBLE);
                }
            });
            titleView.setText(problemNumber);
            titleTextView.setText(title);
            problemTextView.setText(problemText);
            loadImageIntoWebView(problemWebView, screenshotUrl);
            inputTextView.setText(input);
            outputTextView.setText(output);
            if (!contraintsElement.isEmpty()) {
                TableRow dataRow = new TableRow(requireContext());
                int index = 0;
                for (String content : contraintsElement) {
                    TextView cell = createCell(content);
                    if(index == 5) {
                        cell.setBackgroundResource(R.drawable.table_boarder_only_left);
                    } else if (index != 4) {
                        cell.setBackgroundResource(R.drawable.table_boarder_only_right);
                    }
                    dataRow.addView(cell);
                }
                tableLayout.addView(dataRow);
            }
            // 변수 확인 (Log 출력)
            Log.d(TAG, "Task ID: " + taskId);
            Log.d(TAG, "Origin URL: " + originUrl);
            Log.d(TAG, "Constraint: " + constraint);
            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Problem Text: " + problemText);
            Log.d(TAG, "Input: " + input);
            Log.d(TAG, "Output: " + output);
            Log.d(TAG, "Screenshot URL: " + screenshotUrl);
        } else {
            Log.d(TAG, "No task found for URL: " + targetUrl);
        }

        // 모든 데이터 출력
        List<String> allTasks = repository.getAllTasks();
        for (String task : allTasks) {
            Log.d(TAG, task);
        }
        repository.close();
        return rootView;
    }
    private void loadImageIntoWebView(WebView webView, String imageUrl) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // JS 활성화 (필요 시)
        webSettings.setLoadWithOverviewMode(true); // 화면에 맞게 로드
        webSettings.setUseWideViewPort(true); // 콘텐츠를 WebView에 맞추기

        // HTML로 이미지 태그 생성
        String html = "<html><body style=\"margin:0; padding:0;\">" +
                "<img src=\"" + imageUrl + "\" style=\"width:100%; height:auto;\" />" +
                "</body></html>";

        // WebView에 HTML 로드
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }
    private TextView createCell(String content) {
        TextView cell = new TextView(requireContext());
        cell.setText(content);
        cell.setPadding(3, 3, 3, 3);
        cell.setGravity(Gravity.CENTER);
        return cell;
    }
    private ArrayList<String> extractTDContents(String html) {
        ArrayList<String> extractedContents = new ArrayList<>();

        String regex = "<td>(.*?)</td>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        // 매칭된 텍스트 추출
        while (matcher.find()) {
            extractedContents.add(matcher.group(1)); // 첫 번째 그룹 매칭 내용 추가
        }

        return extractedContents;
    }
    public String getProblemNumber(String url) {
        if (url != null && url.contains("/")) {
            // 마지막 '/' 이후의 부분을 추출
            String[] parts = url.split("/");
            return parts[parts.length - 1];
        }
        return null; // URL이 유효하지 않을 경우 null 반환
    }
}


