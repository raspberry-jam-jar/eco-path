package com.teplica.ecopath.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.teplica.ecopath.AppExecutors;
import com.teplica.ecopath.api.ApiResponse;
import com.teplica.ecopath.api.EcoPathDataService;
import com.teplica.ecopath.db.EcoPathDB;
import com.teplica.ecopath.db.ImageDao;
import com.teplica.ecopath.util.AbsentLiveData;
import com.teplica.ecopath.vo.Image;
import com.teplica.ecopath.vo.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ImageRepository {
    private final EcoPathDB db;
    private final ImageDao imageDao;
    private final EcoPathDataService ecoPathDataService;
    private final AppExecutors appExecutors;

    @Inject
    public ImageRepository(AppExecutors appExecutors, ImageDao imageDao, EcoPathDB db,
                           EcoPathDataService ecoPathDataService) {
        this.db = db;
        this.imageDao = imageDao;
        this.appExecutors = appExecutors;
        this.ecoPathDataService = ecoPathDataService;
    }

    public LiveData<List<Image>> loadFromDb(Integer categoryId) {
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

    public LiveData<Resource<List<Image>>> getAllImages(Integer categoryId) {
        return new NetworkBoundResource<List<Image>, List<Image>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Image> images) {
                db.beginTransaction();
                try {
                    List<Integer> actualIds = new ArrayList<>();
                    for (Image image : images) {
                        actualIds.add(image.getId());
                        image.setCategoryId(categoryId);

                        Image savedInDbImage = imageDao.findById(image.getId());
                        if (savedInDbImage != null) {
                            image.setImagePath(savedInDbImage.getImagePath());
                        }
                        imageDao.insertImage(image);
                    }
                    imageDao.deleteOutdated(categoryId, actualIds);
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    db.endTransaction();
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
