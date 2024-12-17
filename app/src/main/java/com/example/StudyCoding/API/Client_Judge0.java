package com.example.StudyCoding.API;

import com.example.StudyCoding.BuildConfig;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class Client_Judge0 {
    private static final String BASE_URL = "https://judge029.p.rapidapi.com/";

    public static Retrofit getInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("x-rapidapi-host", "judge029.p.rapidapi.com")
                                .header("x-rapidapi-key", BuildConfig.JUDGE0_API_KEY);
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
