package com.example.StudyCoding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.ViewGroup;

import com.example.StudyCoding.API.Client_BrowseAI;
import com.example.StudyCoding.API.API_BrowseAI;
import com.example.StudyCoding.Database.Database_Problem_Info.ProblemTaskRepository;
import com.example.StudyCoding.Models.BrowseAIModels.RobotRetrievedResponse;
import com.example.StudyCoding.Models.BrowseAIModels.RobotTaskRequest;
import com.example.StudyCoding.Models.BrowseAIModels.RobotTaskResponse;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
/*
public class getProblemFromWeb extends AppCompatActivity {

    private static final String TAG = "API_RESPONSE";
    private ProblemTaskRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_viewer);  // 직접 레이아웃을 setContentView로 지정

        repository = new ProblemTaskRepository(this);

        Button acceptButton2 = findViewById(R.id.acceptButton2);
        EditText editTextNumber = findViewById(R.id.editTextNumber);


        acceptButton2.setOnClickListener(v -> {
            String problemNumber = editTextNumber.getText().toString().trim();

            if (!problemNumber.isEmpty()) {
                String dynamicUrl = "https://www.acmicpc.net/problem/" + problemNumber;
                Log.d(TAG, "Dynamic URL: " + dynamicUrl);

                if (repository.isUrlExists(dynamicUrl)) {
                    showAlert("URL Exists", "This URL is already in the database.");
                } else {
                    performAcceptAction(dynamicUrl);
                }
            } else {
                showAlert("Input Required", "Please enter a problem number.");
            }
        });




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
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        RobotTaskResponse taskResponse = gson.fromJson(responseBody, RobotTaskResponse.class);

                        String taskId = taskResponse.getResult().getId();
                        Log.d(TAG, "Task Created: ID = " + taskId);

                        // 태스크 상태를 확인하는 폴링 시작
                        pollTaskStatus(service, robotId, taskId);
                    } else {
                        Log.e(TAG, "Error creating task: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing task creation response", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Create Task API Call Failed: " + t.getMessage());
            }
        });
    }



    private void pollTaskStatus(API_BrowseAI service, String robotId, String taskId) {
        new Thread(() -> {
            boolean isCompleted = false;


            while (!isCompleted) {
                try {
                    // 일정 시간 대기 (예: 3초)
                    Thread.sleep(2000);
                    Call<ResponseBody> call = service.retrieveTask(robotId, taskId);
                    Response<ResponseBody> response = call.execute();
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    RobotRetrievedResponse taskResponse = gson.fromJson(jsonResponse, RobotRetrievedResponse.class);
                    Long finishedAt = taskResponse.getResult().getFinishedAt();
                    Log.d(TAG, "Task Status: " + finishedAt);
                    Log.d(TAG, "Json Response: " + jsonResponse);

                    if (response.isSuccessful() && response.body() != null && (finishedAt != null)) {
                        isCompleted = true;
                        // 태스크 완료되었으므로 데이터베이스에 저장
                        repository.insertTask(taskResponse);
                        Log.d(TAG, "Task completed and saved to database");

                    } else {
                        Log.e(TAG, "Error retrieving task status: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in polling task status", e);
                    isCompleted = true; // 오류 발생 시 폴링 종료
                }
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    // 테스트용
    private void retrieveAndLogTaskDetails(String taskId) {
        API_BrowseAI service = Client_BrowseAI.getRetrofitInstance().create(API_BrowseAI.class);
        String robotId = BuildConfig.BROWSEAI_ROBOT_ID;

        Call<ResponseBody> call = service.retrieveTask(robotId, taskId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        Gson gson = new Gson();
                        RobotRetrievedResponse taskResponse = gson.fromJson(jsonResponse, RobotRetrievedResponse.class);
                        String fullResponse = gson.toJson(taskResponse);
                        Log.d(TAG, "Full Task Response: " + jsonResponse);
                        repository.insertTask(taskResponse);
                        List<String> allTasks = repository.getAllTasks();
                        for (String task : allTasks) {
                            Log.d(TAG, task);
                        }

                    } else {
                        Log.e(TAG, "Error retrieving task details: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing task details", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Retrieve Task API Call Failed: " + t.getMessage());
            }
        });
    }




}


 */

/*
package com.example.StudyCoding;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.StudyCoding.API.BrowseAIClient;
import com.example.StudyCoding.API.BrowseAI_API;
import com.example.StudyCoding.Database.TaskDatabase.TaskRepository;
import com.example.StudyCoding.Models.BrowseAIModels.RobotRetrievedResponse;
import com.example.StudyCoding.Models.BrowseAIModels.RobotTaskRequest;
import com.example.StudyCoding.Models.BrowseAIModels.RobotTaskResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class getProblemFromWeb extends AppCompatActivity {
    private static final String TAG = "API_RESPONSE";
    private TaskRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_viewer);
        //retrieveAndLogTaskDetails("e4908d2d-5d27-4da9-aa95-231733e1be9c");

        repository = new TaskRepository(this);

        Button acceptButton2 = findViewById(R.id.acceptButton2);
        EditText editTextNumber = findViewById(R.id.editTextNumber);


        acceptButton2.setOnClickListener(v -> {
            String problemNumber = editTextNumber.getText().toString().trim();

            if (!problemNumber.isEmpty()) {
                String dynamicUrl = "https://www.acmicpc.net/problem/" + problemNumber;
                Log.d(TAG, "Dynamic URL: " + dynamicUrl);

                if (repository.isUrlExists(dynamicUrl)) {
                    showAlert("URL Exists", "This URL is already in the database.");
                } else {
                    performAcceptAction(dynamicUrl);
                }
            } else {
                showAlert("Input Required", "Please enter a problem number.");
            }
        });





    }

    private void performAcceptAction(String currentUrl) {
        HashMap<String, Object> inputParameters = new HashMap<>();
        inputParameters.put("originUrl", currentUrl);

        RobotTaskRequest request = new RobotTaskRequest(inputParameters);
        BrowseAI_API service = BrowseAIClient.getRetrofitInstance().create(BrowseAI_API.class);
        String robotId = BuildConfig.BROWSEAI_ROBOT_ID;

        createTaskAndRetrieve(service, robotId, request);


    }

    private void createTaskAndRetrieve(BrowseAI_API service, String robotId, RobotTaskRequest request) {
        Call<ResponseBody> call = service.createTask(robotId, request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        RobotTaskResponse taskResponse = gson.fromJson(responseBody, RobotTaskResponse.class);

                        String taskId = taskResponse.getResult().getId();
                        Log.d(TAG, "Task Created: ID = " + taskId);

                        // 태스크 상태를 확인하는 폴링 시작
                        pollTaskStatus(service, robotId, taskId);
                    } else {
                        Log.e(TAG, "Error creating task: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing task creation response", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Create Task API Call Failed: " + t.getMessage());
            }
        });
    }



    private void pollTaskStatus(BrowseAI_API service, String robotId, String taskId) {
        new Thread(() -> {
            boolean isCompleted = false;


            while (!isCompleted) {
                try {
                    // 일정 시간 대기 (예: 3초)
                    Thread.sleep(2000);
                    Call<ResponseBody> call = service.retrieveTask(robotId, taskId);
                    Response<ResponseBody> response = call.execute();
                    String jsonResponse = response.body().string();
                    Gson gson = new Gson();
                    RobotRetrievedResponse taskResponse = gson.fromJson(jsonResponse, RobotRetrievedResponse.class);
                    Long finishedAt = taskResponse.getResult().getFinishedAt();
                    Log.d(TAG, "Task Status: " + finishedAt);
                    Log.d(TAG, "Json Response: " + jsonResponse);

                    if (response.isSuccessful() && response.body() != null && (finishedAt != null)) {
                            isCompleted = true;
                            // 태스크 완료되었으므로 데이터베이스에 저장
                            repository.insertTask(taskResponse);
                            Log.d(TAG, "Task completed and saved to database");

                    } else {
                        Log.e(TAG, "Error retrieving task status: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in polling task status", e);
                    isCompleted = true; // 오류 발생 시 폴링 종료
                }
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
// 테스트용
    private void retrieveAndLogTaskDetails(String taskId) {
        BrowseAI_API service = BrowseAIClient.getRetrofitInstance().create(BrowseAI_API.class);
        String robotId = BuildConfig.BROWSEAI_ROBOT_ID;

        Call<ResponseBody> call = service.retrieveTask(robotId, taskId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        Gson gson = new Gson();
                        RobotRetrievedResponse taskResponse = gson.fromJson(jsonResponse, RobotRetrievedResponse.class);
                        String fullResponse = gson.toJson(taskResponse);
                        Log.d(TAG, "Full Task Response: " + jsonResponse);
                        repository.insertTask(taskResponse);
                        List<String> allTasks = repository.getAllTasks();
                        for (String task : allTasks) {
                            Log.d(TAG, task);
                        }

                    } else {
                        Log.e(TAG, "Error retrieving task details: " + response.code());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing task details", e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Retrieve Task API Call Failed: " + t.getMessage());
            }
        });
    }




}


 */
