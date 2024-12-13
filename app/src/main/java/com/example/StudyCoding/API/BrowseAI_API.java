package com.example.StudyCoding.API;

import com.example.StudyCoding.Models.BrowseAIModels.RobotTaskRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BrowseAI_API {
    @POST("robots/{robotId}/tasks")
    Call<ResponseBody> createTask(
            @Path("robotId") String robotId,
            @Body RobotTaskRequest request
    );
    @GET("robots/{robotId}/tasks/{taskId}")
    Call<ResponseBody> retrieveTask(
            @Path("robotId") String robotId,
            @Path("taskId") String taskId
    );

}
