package com.example.StudyCoding.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.StudyCoding.Database.TaskDatabase.TaskRepository;
import com.example.StudyCoding.Models.BrowseAIModels.RobotRetrievedResponse;
import com.example.StudyCoding.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProblemViewFragment extends Fragment {
    private static final String TAG = "SIMPLIFIED_RESPONSE";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.problem_viewer, container, false);
        String targetUrl = getArguments().getString("url");

        TaskRepository repository = new TaskRepository(requireContext());

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
            TextView urlTextView = rootView.findViewById(R.id.urlText);
            TextView titleTextView = rootView.findViewById(R.id.title);
            TextView problemTextView = rootView.findViewById(R.id.problemText);
            WebView problemWebView = rootView.findViewById(R.id.problemSrc);
            TextView inputTextView = rootView.findViewById(R.id.input);
            TextView outputTextView = rootView.findViewById(R.id.output);
            TableLayout tableLayout = rootView.findViewById(R.id.constraintTable);
            Button textButton = rootView.findViewById(R.id.textButton);
            Button webButton = rootView.findViewById(R.id.webButton);
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


            titleTextView.setText(title);
            problemTextView.setText(problemText);
            loadImageIntoWebView(problemWebView, screenshotUrl);
            inputTextView.setText(input);
            outputTextView.setText(output);
            if (!contraintsElement.isEmpty()) {
                TableRow dataRow = new TableRow(requireContext());
                for (String content : contraintsElement) {
                    TextView cell = createCell(content);
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
        cell.setPadding(8, 8, 8, 8);
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
}





/*
package com.example.StudyCoding;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.StudyCoding.Database.TaskDatabase.TaskRepository;
import com.example.StudyCoding.Models.BrowseAIModels.RobotRetrievedResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getProblemFromDatabase extends AppCompatActivity {
    private static final String TAG = "SIMPLIFIED_RESPONSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_viewer);


        Log.d(TAG, "Starting SimplifiedResponseActivity...");
        // JSON 응답 예시
        String jsonResponse = "{\n" +
                "  \"statusCode\": 200,\n" +
                "  \"messageCode\": \"success\",\n" +
                "  \"result\": {\n" +
                "    \"id\": \"d0ee6668-241d-453a-be9b-6b44c3d81a2f\",\n" +
                "    \"status\": \"failed\",\n" +
                "    \"createdAt\": 1733330399917,\n" +
                "    \"finishedAt\": 1733330423377,\n" +
                "    \"retriedOriginalTaskId\": null,\n" +
                "    \"retriedTaskId\": null,\n" +
                "    \"retriedByTaskId\": \"9a658e34-380a-49a1-8391-2a0c72e0486a\",\n" +
                "    \"startedAt\": 1733330403605,\n" +
                "    \"robotId\": \"d29f68c7-3c17-4efb-a9ba-fc644d744a1d\",\n" +
                "    \"triedRecordingVideo\": false,\n" +
                "    \"robotBulkRunId\": null,\n" +
                "    \"runByAPI\": true,\n" +
                "    \"runByTaskMonitorId\": null,\n" +
                "    \"runByUserId\": null,\n" +
                "    \"userFriendlyError\": \"Could not find target element.\",\n" +
                "    \"inputParameters\": {\n" +
                "      \"originUrl\": \"https://www.acmicpc.net/problem/1006\",\n" +
                "      \"boarder_limit\": 100\n" +
                "    },\n" +
                "    \"videoRemovedAt\": null,\n" +
                "    \"videoUrl\": null,\n" +
                "    \"capturedDataTemporaryUrl\": null,\n" +
                "    \"capturedTexts\": {\n" +
                "      \"constraint\": \"<td>2 초 </td><td>512 MB</td><td>21630</td><td>3714</td><td>2555</td><td>20.478%</td>\",\n" +
                "      \"title\": \"습격자 초라기\",\n" +
                "      \"problem_text\": \"초라기는 한국의 비밀국방기지(원타곤)를 습격하라는 임무를 받은 특급요원이다. 원타곤의 건물은 도넛 형태이며, 초라기는 효율적인 타격 포인트를 정하기 위해 구역을 아래와 같이 두 개의 원 모양으로 나누었다. (그림의 숫자는 각 구역의 번호이다.)\\n\\n초라기는 각각 W명으로 구성된 특수소대를 다수 출동시켜 모든 구역에 침투시킬 예정이며, 각 구역 별로 적이 몇 명씩 배치되어 있는지는 초라기가 모두 알고 있다. 특수소대를 아래 조건에 따라 침투 시킬 수 있다.\\n\\n한 특수소대는 침투한 구역 외에, 인접한 한 구역 더 침투할 수 있다. (같은 경계를 공유하고 있으면 인접 하다고 한다. 위 그림에서 1구역은 2, 8, 9 구역과 서로 인접한 상태다.) 즉, 한 특수소대는 한 개 혹은 두 개의 구역을 커버할 수 있다.\\n특수소대끼리는 아군인지 적인지 구분을 못 하기 때문에, 각 구역은 하나의 소대로만 커버해야 한다.\\n한 특수소대가 커버하는 구역의 적들의 합은 특수소대원 수 W 보다 작거나 같아야 한다.\\n\\n이때 초라기는 원타곤의 모든 구역을 커버하기 위해 침투 시켜야 할 특수 소대의 최소 개수를 알고 싶어 한다.\",\n" +
                "      \"input\": \"첫째 줄에 테스트 케이스의 개수 T가 주어진다. 각 테스트 케이스는 다음과 같이 구성되어있다.\",\n" +
                "      \"output\": \"각 테스트케이스에 대해서 한 줄에 하나씩 원타곤의 모든 구역을 커버하기 위해 침투 시켜야 할 특수 소대의 최소 개수를 출력하시오.\"\n" +
                "    },\n" +
                "    \"capturedScreenshots\": {\n" +
                "      \"problem_capture\": {\n" +
                "        \"id\": \"228b8b61-7a57-432c-afc9-d4471ece9947\",\n" +
                "        \"changePercentage\": 0,\n" +
                "        \"comparedToScreenshotId\": null,\n" +
                "        \"deviceScaleFactor\": \"2.000000\",\n" +
                "        \"diffImageSrc\": null,\n" +
                "        \"diffThreshold\": 0.01,\n" +
                "        \"width\": 1440,\n" +
                "        \"height\": 1380,\n" +
                "        \"name\": \"problem_capture\",\n" +
                "        \"src\": \"https://prod-browseai-captured-data.s3.us-east-1.amazonaws.com/0d3076e8-7b94-42ba-8a85-45e87b70da74/d29f68c7-3c17-4efb-a9ba-fc644d744a1d/d0ee6668-241d-453a-be9b-6b44c3d81a2f/00003-user-1733330419-4d6d7ae9-612a-4bb1-9767-ef29caae0c9a.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Credential=AKIAQVG3TPBV6JSYQLFU%2F20241204%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20241204T164048Z&X-Amz-Expires=604800&X-Amz-Signature=3df3fdaab1f06c94e52e18f813a29e1245ecc6c06605c3309127166eda58ddd2&X-Amz-SignedHeaders=host&x-id=GetObject\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"capturedLists\": {\n" +
                "      \"boarder\": []\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // JSON 매핑
        Gson gson = new Gson();
        RobotRetrievedResponse response = gson.fromJson(jsonResponse, RobotRetrievedResponse.class);

        // 데이터베이스에 저장
        TaskRepository repository = new TaskRepository(this);
        //repository.insertTask(response);

        //repository.deleteRowByUrl("https://www.acmicpc.net/problem/1024");

        String targetUrl = "https://www.acmicpc.net/problem/1003";
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
            TextView urlTextView = findViewById(R.id.urlText);
            TextView titleTextView = findViewById(R.id.title);
            TextView problemTextView = findViewById(R.id.problemText);
            WebView problemWebView = findViewById(R.id.problemSrc);
            TextView inputTextView = findViewById(R.id.input);
            TextView outputTextView = findViewById(R.id.output);
            TableLayout tableLayout = findViewById(R.id.constraintTable);
            Button textButton = findViewById(R.id.textButton);
            Button webButton = findViewById(R.id.webButton);
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


            titleTextView.setText(title);
            problemTextView.setText(problemText);
            loadImageIntoWebView(problemWebView, screenshotUrl);
            inputTextView.setText(input);
            outputTextView.setText(output);
            if (!contraintsElement.isEmpty()) {
                TableRow dataRow = new TableRow(this);
                for (String content : contraintsElement) {
                    TextView cell = createCell(content);
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
        TextView cell = new TextView(this);
        cell.setText(content);
        cell.setPadding(8, 8, 8, 8);
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
}

 */



