package com.example.ecopath.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.ecopath.AppExecutors;
import com.example.ecopath.api.ApiResponse;
import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.db.ImageDao;
import com.example.ecopath.util.AbsentLiveData;
import com.example.ecopath.vo.Image;
import com.example.ecopath.vo.Resource;

import java.util.List;

import javax.inject.Inject;

public class ImageRepository {
    private final ImageDao imageDao;
    private final EcoPathDataService ecoPathDataService;
    private final AppExecutors appExecutors;

    @Inject
    public ImageRepository(AppExecutors appExecutors, ImageDao imageDao,
                           EcoPathDataService ecoPathDataService) {
        this.imageDao = imageDao;
        this.appExecutors = appExecutors;
        this.ecoPathDataService = ecoPathDataService;
    }

    public LiveData<Resource<List<Image>>> getAllImages(Integer categoryId) {
        return new NetworkBoundResource<List<Image>, List<Image>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Image> images) {
                try {
                    for (Image image : images) {
                        image.setCategoryId(categoryId);
                        imageDao.insertImage(image);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Image> data) { return true; }

            @NonNull
            @Override
            protected LiveData<List<Image>> loadFromDb() {
                return Transformations.switchMap(
                    imageDao.getAll(categoryId), images -> {
                        if (images == null || images.isEmpty()) {
                            return AbsentLiveData.create();
                        } else {
                            return imageDao.getAll(categoryId);
                        }
                    }
                );
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Image>>> createCall() {
                return ecoPathDataService.listCategoryImages(categoryId);
            }

            @Override
            protected List<Image> processResponse(ApiResponse<List<Image>> response) {
                return response.body;
            }
        }.asLiveData();
    }
}
