package com.example.ecopath.api;

import androidx.lifecycle.LiveData;

import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * REST API access points
 */
public interface EcoPathDataService {
    // TODO remove after moving to the prod server
    @Headers({"X-Dront-Auth: "})
    @GET("points")
    LiveData<ApiResponse<ApiData>> listMapPoints();
}
