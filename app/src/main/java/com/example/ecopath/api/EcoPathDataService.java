package com.example.ecopath.api;

import androidx.lifecycle.LiveData;

import com.example.ecopath.BuildConfig;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Image;
import com.example.ecopath.vo.MapPoint;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * REST API access points
 */
public interface EcoPathDataService {

    @Headers({BuildConfig.AUTH_HEADER})
    @GET(BuildConfig.API_VERSION + "points")
    LiveData<ApiResponse<List<MapPoint>>> listMapPoints();

    @Headers({BuildConfig.AUTH_HEADER})
    @GET(BuildConfig.API_VERSION + "points/{mapPointId}/positions")
    LiveData<ApiResponse<List<CategoryWithImages>>> listCategories(@Path("mapPointId") Integer mapPointId);

    @Headers({BuildConfig.AUTH_HEADER})
    @GET(BuildConfig.API_VERSION + "points/{mapPointId}/positions")
    Call<List<CategoryWithImages>> getCategories(@Path("mapPointId") Integer mapPointId);

    @GET("attaches/images/{fileUrl}")
    @Streaming
    Call<ResponseBody> downloadImage(@Path("fileUrl") String fileUrl);

    @Headers({BuildConfig.AUTH_HEADER})
    @GET(BuildConfig.API_VERSION + "positions/{categoryId}/images")
    LiveData<ApiResponse<List<Image>>> listCategoryImages(@Path("categoryId") Integer categoryId);
}
