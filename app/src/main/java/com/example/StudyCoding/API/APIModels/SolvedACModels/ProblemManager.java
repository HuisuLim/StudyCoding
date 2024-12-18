package com.example.StudyCoding.API.APIModels.SolvedACModels;

import android.content.Context;
import android.util.Log;

import com.example.StudyCoding.API.Client_SolvedAC;
import com.example.StudyCoding.Database.Database_Problem.ProblemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProblemManager {
    private final Client_SolvedAC apiService;
    private final ProblemRepository repository;

    public ProblemManager(Context context) {
        this.apiService = new Client_SolvedAC("https://solved.ac/api/v3/");
        this.repository = new ProblemRepository(context);
    }

    public void fetchAndSaveProblems(List<Integer> problemIds) {
        // 데이터베이스에서 존재하지 않는 문제만 필터링
        List<Integer> newProblemIds = problemIds.stream()
                .filter(problemId -> !repository.isProblemExists(problemId)) // 존재하지 않는 문제만 가져오기
                .collect(Collectors.toList());

        if (newProblemIds.isEmpty()) {
            Log.d("ProblemManager", "No new problems to fetch.");
            return; // 새로 가져올 문제 없음
        }
        apiService.fetchProblems(newProblemIds, new Callback<List<ProblemResponse>>() {
            @Override
            public void onResponse(Call<List<ProblemResponse>> call, Response<List<ProblemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProblemResponse> problems = response.body();
                    for (ProblemResponse problem : problems) {
                        List<String> tags = new ArrayList<>();
                        if (problem.tags != null) {
                            for (ProblemResponse.Tag tag : problem.tags) {
                                if (tag.displayNames != null) {
                                    for (ProblemResponse.Tag.DisplayName displayName : tag.displayNames) {
                                        if ("ko".equals(displayName.language)) {
                                            tags.add(displayName.name);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        repository.UpsertProblem(problem.problemId, problem.titleKo, problem.level, tags);
                    }
                    Log.d("ProblemManager", "Problems saved successfully.");
                } else {
                    Log.e("ProblemManager", "Failed to fetch problems. HTTP Status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ProblemResponse>> call, Throwable t) {
                Log.e("ProblemManager", "API call failed", t);
            }
        });
    }

    public void fetchAndSaveProblemsBatch(int startId, int endId, int batchSize) {
        int currentStartId = startId;

        while (currentStartId <= endId) {
            int currentEndId = Math.min(currentStartId + batchSize - 1, endId);
            List<Integer> problemIds = new ArrayList<>();
            for (int i = currentStartId; i <= currentEndId; i++) {
                problemIds.add(i);
            }

            fetchAndSaveProblems(problemIds);

            currentStartId = currentEndId + 1;

            try {
                Thread.sleep(500); // 서버 부하 방지
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
