package com.example.StudyCoding.API;

import com.example.StudyCoding.Models.SolvedACModels.ProblemResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface API_SolvedAC {
    @GET("problem/lookup")
    Call<List<ProblemResponse>> getProblems(
            @Query("problemIds") String problemIds, // 쉼표로 구분된 문제 번호 목록
            @Header("Accept") String accept,
            @Header("x-solvedac-language") String language
    );
}
