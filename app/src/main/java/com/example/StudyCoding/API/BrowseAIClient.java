package com.example.StudyCoding.API;

import com.example.StudyCoding.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BrowseAIClient {
    private static final String BASE_URL = "https://api.browse.ai/v2/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // OkHttpClient에 Interceptor 추가하여 헤더에 API 키 포함
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
                    .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃
                    .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", BuildConfig.BROWSEAI_API_KEY); // API 키 추가
                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    })
                    .build();

            // Retrofit 빌더 설정
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
