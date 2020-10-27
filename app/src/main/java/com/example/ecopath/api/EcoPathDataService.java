package com.example.ecopath.api;

import androidx.lifecycle.LiveData;

import com.example.ecopath.BuildConfig;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Image;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * REST API access points
 */
public interface EcoPathDataService {

    @Headers({BuildConfig.AUTH_HEADER})
    @GET("points")
    LiveData<ApiResponse<ApiData>> listMapPoints();

    @Headers({BuildConfig.AUTH_HEADER})
    @GET("points/{mapPointId}/positions")
    LiveData<ApiResponse<List<CategoryWithImages>>> listCategories(@Path("mapPointId") Integer mapPointId);

    @Headers({BuildConfig.AUTH_HEADER})
    @GET("positions/{categoryId}/images")
    LiveData<ApiResponse<List<Image>>> listCategoryImages(@Path("categoryId") Integer categoryId);
}
