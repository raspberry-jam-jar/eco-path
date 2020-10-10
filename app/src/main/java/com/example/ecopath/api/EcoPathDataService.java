package com.example.ecopath.api;

import androidx.lifecycle.LiveData;

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
    // TODO remove after moving to the prod server
    @Headers({"X-Dront-Auth: 1ea8e01c68b4fbbb5cbf5ec97dd711c7482a43a1"})
    @GET("points")
    LiveData<ApiResponse<ApiData>> listMapPoints();

    @Headers({"X-Dront-Auth: 1ea8e01c68b4fbbb5cbf5ec97dd711c7482a43a1"})
    @GET("points/{mapPointId}/positions")
    LiveData<ApiResponse<List<CategoryWithImages>>> listCategories(@Path("mapPointId") Integer mapPointId);

    @Headers({"X-Dront-Auth: 1ea8e01c68b4fbbb5cbf5ec97dd711c7482a43a1"})
    @GET("positions/{categoryId}/images")
    LiveData<ApiResponse<List<Image>>> listCategoryImages(@Path("categoryId") Integer categoryId);
}
