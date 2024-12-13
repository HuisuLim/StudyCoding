package com.example.StudyCoding.Models.SolvedACModels;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.StudyCoding.API.SolvedACAPI;
import com.example.StudyCoding.Database.ProblemDatabase.ProblemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestForSolvedAC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // API 서비스 초기화
        ApiService apiService = new ApiService(this);

        // 다중 문제 ID 요청 테스트
        //List<Integer> problemIds = Arrays.asList(1005,1006); // 예제 문제 ID
        // apiService.fetchAndSaveProblems(problemIds);
        // 저장된 데이터 Logcat에 출력
        //apiService.fetchAndSaveProblemsBatch(1000, 32940, 50);
        ProblemRepository repository = new ProblemRepository(this);
        repository.printAllProblems();
    }

    public static class ApiService {
        private final SolvedACAPI api;
        private final ProblemRepository repository;

        public ApiService(Context context) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://solved.ac/api/v3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(SolvedACAPI.class);
            repository = new ProblemRepository(context);
        }

        public void fetchAndSaveProblems(List<Integer> problemIds) {
            // 문제 번호 목록을 쉼표로 구분된 문자열로 변환
            String problemIdsParam = problemIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            Log.d("ApiService", "Requesting problems with IDs: " + problemIdsParam); // 요청 파라미터 로그

            api.getProblems(problemIdsParam, "application/json", "ko").enqueue(new Callback<List<ProblemResponse>>() {
                @Override
                public void onResponse(Call<List<ProblemResponse>> call, Response<List<ProblemResponse>> response) {
                    Log.d("ApiService", "Response Code: " + response.code()); // 응답 코드 로그
                    Log.d("ApiService", "Raw JSON: " + response.raw().body().toString());
                    if (response.isSuccessful() && response.body() != null) {
                        List<ProblemResponse> problems = response.body();
                        Log.d("ApiService", "Received " + problems.size() + " problems."); // 응답 크기 로그

                        // 문제 처리
                        for (ProblemResponse problem : problems) {
                            Log.d("ApiService", "Processing problem: " + problem);

                            // 태그 이름 추출
                            List<String> tags = new ArrayList<>();
                            if (problem.tags != null) {
                                for (ProblemResponse.Tag tag : problem.tags) {
                                    if (tag.displayNames != null) {
                                        for (ProblemResponse.Tag.DisplayName displayName : tag.displayNames) {
                                            if ("ko".equals(displayName.language)) {
                                                tags.add(displayName.name);
                                                break; // 한 언어만 저장
                                            }
                                        }
                                    }
                                }
                            }

                            // 데이터 저장
                            repository.UpsertProblem(problem.problemId, problem.titleKo, problem.level, tags);
                            Log.d("ApiService", "Problem saved: ID = " + problem.problemId);
                        }
                    } else {
                        // 응답 실패 처리
                        Log.e("ApiService", "Failed to fetch problems. HTTP Status: " + response.code());

                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e("ApiService", "Error Body: " + errorBody); // 에러 본문 로그
                            }
                        } catch (Exception e) {
                            Log.e("ApiService", "Failed to read error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ProblemResponse>> call, Throwable t) {
                    Log.e("ApiService", "API call failed", t);
                }
            });
        }
        public void fetchAndSaveProblemsBatch(int startId, int endId, int batchSize) {
            int currentStartId = startId;

            while (currentStartId <= endId) {
                // 현재 배치의 문제 ID 생성
                int currentEndId = Math.min(currentStartId + batchSize - 1, endId);
                List<Integer> problemIds = new ArrayList<>();
                for (int i = currentStartId; i <= currentEndId; i++) {
                    problemIds.add(i);
                }

                // 배치 요청
                fetchAndSaveProblems(problemIds);

                // 다음 배치로 이동
                currentStartId = currentEndId + 1;

                // 서버 부하 방지를 위해 딜레이 추가 (옵션)
                try {
                    Thread.sleep(500); // 500ms 대기
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
