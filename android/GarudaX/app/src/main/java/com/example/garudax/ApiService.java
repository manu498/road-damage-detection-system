package com.example.garudax;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Login
    @POST("login")
    Call<Map<String, Object>> login(
            @Body Map<String, String> data
    );

    // Register
    @POST("register")
    Call<Map<String, Object>> register(
            @Body Map<String, String> data
    );

    // Submit Complaint + Image
    @Multipart
    @POST("complaint")
    Call<Map<String, Object>> submitComplaint(
            @Part("citizen_id") RequestBody citizenId,
            @Part("location_id") RequestBody locationId,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part image
    );

    // Get Complaint Details
    @GET("complaint/{complaint_id}")
    Call<Map<String, Object>> getComplaintDetails(
            @Path("complaint_id") int complaintId
    );
}