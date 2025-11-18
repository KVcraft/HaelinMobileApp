package com.haelinmobileapp.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("user/login/patient")
    Call<LoginResponse> loginPatient(
            @Header("Authorization") String authHeader,
            @Body LoginRequest request
    );
}

