package com.example.StudyCoding.API;

import com.example.StudyCoding.Models.Judge0Models.SubmissionLanguage;
import com.example.StudyCoding.Models.Judge0Models.SubmissionRequest;
import com.example.StudyCoding.Models.Judge0Models.SubmissionResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API_Judge0 {
    @GET("languages")
    Call<List<SubmissionLanguage>> getLanguages();

    @POST("submissions")
    Call<SubmissionResponse> createSubmission(
            @Query("base64_encoded") boolean base64Encoded,
            @Query("wait") boolean wait,
            @Query("fields") String fields,
            @Body SubmissionRequest request
    );

    @GET("submissions/{token}")
    Call<SubmissionResponse> getSubmission(
            @Path("token") String token,
            @Query("base64_encoded") boolean base64Encoded,
            @Query("fields") String fields
    );
}
