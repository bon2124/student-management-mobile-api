package com.example.student_management.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Thêm dòng này


public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.74/student_api/";
    private static RetrofitClient instance;
    private ApiService apiService;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}