package com.teplica.ecopath.api;

import androidx.lifecycle.LiveData;

import com.teplica.ecopath.BuildConfig;
import com.teplica.ecopath.vo.CategoryWithImages;
import com.teplica.ecopath.vo.Image;
import com.teplica.ecopath.vo.MapPoint;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

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

    @GET()
    @Streaming
    Call<ResponseBody> downloadImage(@Url() String fileUrl);

    @Headers({BuildConfig.AUTH_HEADER})
    @GET(BuildConfig.API_VERSION + "positions/{categoryId}/images")
    LiveData<ApiResponse<List<Image>>> listCategoryImages(@Path("categoryId") Integer categoryId);

    @Headers({BuildConfig.AUTH_HEADER})
    @GET(BuildConfig.API_VERSION + "positions/{categoryId}/images")
    Call<List<Image>> getCategoryImages(@Path("categoryId") Integer categoryId);
}
