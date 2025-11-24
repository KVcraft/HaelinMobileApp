package com.haelinmobileapp.retrofit;

import com.haelinmobileapp.UserMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("user/login/patient")
    Call<LoginResponse> loginPatient(
            @Header("Authorization") String authHeader,
            @Body LoginRequest request
    );

    @GET("map/hospitals")
    Call<List<Hospital>> getNearbyHospitals(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("radius") int radius
    );


    @GET("map/test")
    Call<String> testMapEndpoint();

    @POST("chat")
    Call<ChatReponse> chat(@Body UserMessage userMessage);

    @POST("predict_dengue")
    Call<PredReponse> sendDengueSymptoms(@Body DengueSymptoms dengueSymptoms);

    @POST("predict_chikun")  // or whatever your endpoint is
    Call<PredReponse> sendChikunSymptoms(@Body ChikunSymptoms symptoms);

}


