package com.example.StudyCoding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.StudyCoding.API.API_BrowseAI;
import com.example.StudyCoding.API.Client_BrowseAI;
import com.example.StudyCoding.Database.Database_Problem.Problem;
import com.example.StudyCoding.Database.Database_Problem.ProblemRepository;
import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTaskRepository;
import com.example.StudyCoding.Fragment.Adapter.Adpater_Search_Problem;
import com.example.StudyCoding.Fragment.Adapter.Decoration_SpacingItem;
import com.example.StudyCoding.API.APIModels.BrowseAIModels.RobotRetrievedResponse;
import com.example.StudyCoding.API.APIModels.BrowseAIModels.RobotTaskRequest;
import com.example.StudyCoding.API.APIModels.BrowseAIModels.RobotTaskResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Activity_Search_Problem extends AppCompatActivity {

    private boolean isPageOpen = false; // 슬라이딩 상태 여부
    private LinearLayout expandableLayout;
    private RecyclerView recyclerView;
    private Adpater_Search_Problem adapter;
    private ProblemRepository repository;
    private ProblemTaskRepository problemTaskRepository;
    private static final String TAG = "API_RESPONSE";

    private ImageButton searchViewButton;
    private LinearLayout page;
    private Animation tranlateBottomAnim;
    private Animation tranlateTopAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_problem); // 여기에 recyclerView가 있다고 가정
        Spinner levelSpinner = findViewById(R.id.problem_level);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.levels_array,
                R.layout.spinner_selected_item
        );
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);
        Spinner tagSpinner = findViewById(R.id.problem_tags);
        ProblemRepository repository = new ProblemRepository(this);
        List<String> tagList = repository.getAllDistinctTags(this);

        // 맨 앞에 "Select a tag" 추가
        tagList.add(0, "Select a tag");
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_selected_item,
                tagList
        );
        tagAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        tagSpinner.setAdapter(tagAdapter);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        recyclerView.addItemDecoration(new Decoration_SpacingItem(spacingInPixels));
        repository = new ProblemRepository(this);
        List<Problem> problemList = repository.getAllProblems();

        adapter = new Adpater_Search_Problem(problemList);
        recyclerView.setAdapter(adapter);
        // 콜백 설정
        problemTaskRepository = new ProblemTaskRepository(this);
        adapter.setOnDownloadClickListener(url -> {
            if (problemTaskRepository.isUrlExists(url)) {
                showAlert("URL Exists", "This URL is already in the database.");
            } else {
                performAcceptAction(url);
            }
        });

        searchViewButton = findViewById(R.id.searchViewButton);
        page = findViewById(R.id.expandableLayout);

        searchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPageOpen){
                    page.setVisibility(View.GONE);
                    isPageOpen=false;
                }else{
                    page.setVisibility(View.VISIBLE);
                    isPageOpen=true;
                }
            }
        });



        //for search element
        EditText numberEdit = findViewById(R.id.problem_number);
        EditText titleEdit = findViewById(R.id.problem_title);
        Button searchButton = findViewById(R.id.searchButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> finish());
        ProblemRepository finalRepository = repository;
        searchButton.setOnClickListener(v -> {
            String numberText = numberEdit.getText().toString().trim();
            String titleText = titleEdit.getText().toString().trim();
            String selectedLevel = (String) levelSpinner.getSelectedItem();
            String selectedTag = (String) tagSpinner.getSelectedItem();

            // "Select a level"이면 null 처리
            if ("Select a level".equals(selectedLevel)) {
                selectedLevel = null;
            }

            // "Select a tag"이면 null 처리
            if ("Select a tag".equals(selectedTag)) {
                selectedTag = null;
            }

            // 동적 쿼리로 DB 조회
            final List<Problem> searchResults = finalRepository.searchProblems(numberText, titleText, selectedLevel, selectedTag);

            // RecyclerView 갱신
            problemList.clear();
            problemList.addAll(searchResults);
            adapter.notifyDataSetChanged();
        });

    }
    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {

        }

        public void onAnimationEnd(Animation animation){
            if(isPageOpen){
                page.setVisibility(View.GONE);
                isPageOpen = false;
            }else{
                isPageOpen = true;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
    private void performAcceptAction(String currentUrl) {
        HashMap<String, Object> inputParameters = new HashMap<>();
        inputParameters.put("originUrl", currentUrl);

        RobotTaskRequest request = new RobotTaskRequest(inputParameters);
        API_BrowseAI service = Client_BrowseAI.getRetrofitInstance().create(API_BrowseAI.class);
        String robotId = BuildConfig.BROWSEAI_ROBOT_ID;

        createTaskAndRetrieve(service, robotId, request);
    }

    private void createTaskAndRetrieve(API_BrowseAI service, String robotId, RobotTaskRequest request) {
        Call<ResponseBody> call = service.createTask(robotId, request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        RobotTaskResponse taskResponse = gson.fromJson(responseBody, RobotTaskResponse.class);

                        String taskId = taskResponse.getResult().getId();
                        Log.d(TAG, "Task Created: ID = " + taskId);

                        // 폴링 시작
                        pollTaskStatus(service, robotId, taskId);
                    } else {
                        Log.e(TAG, "Error creating task: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing task creation response", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                Log.e(TAG, "Create Task API Call Failed: " + t.getMessage());
            }
        });
    }

    private void pollTaskStatus(API_BrowseAI service, String robotId, String taskId) {
        new Thread(() -> {
            boolean isCompleted = false;

            while (!isCompleted) {
                try {
                    Thread.sleep(2000);
                    Call<ResponseBody> call = service.retrieveTask(robotId, taskId);
                    Response<ResponseBody> response = call.execute();

                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e(TAG, "Error retrieving task status: " + (response != null ? response.code() : "no response"));
                        continue;
                    }

                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    RobotRetrievedResponse taskResponse = gson.fromJson(jsonResponse, RobotRetrievedResponse.class);
                    Long finishedAt = taskResponse.getResult().getFinishedAt();
                    Log.d(TAG, "Task Status: " + finishedAt);
                    Log.d(TAG, "Json Response: " + jsonResponse);

                    if (finishedAt != null) {
                        isCompleted = true;
                        problemTaskRepository.insertTask(taskResponse);
                        Log.d(TAG, "Task completed and saved to database");

                        // UI 업데이트 (예: 다이얼로그)
                        runOnUiThread(() -> showAlert("Download Complete", "Task completed and saved to database."));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in polling task status", e);
                    isCompleted = true;
                }
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        // 다이얼로그용 커스텀 레이아웃 인플레이트
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.cutom_alert_dialog, null);

        // 레이아웃 내 UI 요소 연결
        TextView dialogTitle = customView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = customView.findViewById(R.id.dialogMessage);
        Button okButton = customView.findViewById(R.id.okButton);

        // 텍스트 설정
        dialogTitle.setText(title);
        dialogMessage.setText(message);

        // AlertDialog 생성 및 설정
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(customView); // 커스텀 레이아웃 적용
        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // OK 버튼 클릭 리스너
        okButton.setOnClickListener(v -> dialog.dismiss());

        // 다이얼로그 표시
        dialog.show();
    }






}
