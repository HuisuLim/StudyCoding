package com.example.StudyCoding.API;

import com.example.StudyCoding.API.APIModels.SolvedACModels.ProblemResponse;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client_SolvedAC {
    private final API_SolvedAC api;

    public Client_SolvedAC(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(API_SolvedAC.class);
    }

    // 문제 ID로 API 호출
    public void fetchProblems(List<Integer> problemIds, Callback<List<ProblemResponse>> callback) {
        String problemIdsParam = problemIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Call<List<ProblemResponse>> call = api.getProblems(problemIdsParam, "application/json", "ko");
        call.enqueue(callback);
    }
}
