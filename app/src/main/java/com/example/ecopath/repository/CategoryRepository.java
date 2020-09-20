package com.example.ecopath.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.ecopath.AppExecutors;
import com.example.ecopath.api.ApiResponse;
import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.db.CategoryDao;
import com.example.ecopath.db.CategoryWithImagesDao;
import com.example.ecopath.db.EcoPathDB;
import com.example.ecopath.db.ImageDao;
import com.example.ecopath.util.AbsentLiveData;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CategoryRepository {
    private final EcoPathDB db;
    private final CategoryWithImagesDao categoryWithImagesDao;
    private final CategoryDao categoryDao;
    private final ImageDao imageDao;
    private final EcoPathDataService ecoPathDataService;
    private final AppExecutors appExecutors;

    @Inject
    public CategoryRepository(AppExecutors appExecutors, EcoPathDB db,
                              CategoryWithImagesDao categoryWithImagesDao,
                              CategoryDao categoryDao, ImageDao imageDao,
                              EcoPathDataService ecoPathDataService) {
        this.db = db;
        this.categoryWithImagesDao = categoryWithImagesDao;
        this.categoryDao = categoryDao;
        this.imageDao = imageDao;
        this.appExecutors = appExecutors;
        this.ecoPathDataService = ecoPathDataService;
    }

    public LiveData<Resource<List<CategoryWithImages>>> getAllCategories(Integer mapPointId) {
        return new NetworkBoundResource<List<CategoryWithImages>, List<CategoryWithImages>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<CategoryWithImages> categories) {
//                db.beginTransaction();
                try {
                    for (CategoryWithImages categoryWithImages : categories) {
                        categoryDao.insert(categoryWithImages.category);
                        if (!categoryWithImages.imagesList.isEmpty()) {
                            imageDao.insertImages(categoryWithImages.imagesList);
                        }
                    }
                }
                catch (Exception e){
                    System.out.println(e);
                } finally {
//                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<CategoryWithImages> data) { return true; }

            @NonNull
            @Override
            protected LiveData<List<CategoryWithImages>> loadFromDb() {
                return Transformations.switchMap(
                        categoryWithImagesDao.getAll(mapPointId), categories -> {
                    if (categories == null || categories.isEmpty()) {
                        return AbsentLiveData.create();
                    } else {
                        return categoryWithImagesDao.getAll(mapPointId);
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<CategoryWithImages>>> createCall() {
                return ecoPathDataService.listCategories(mapPointId);
            }

            @Override
            protected List<CategoryWithImages> processResponse(ApiResponse<List<CategoryWithImages>> response) {
                return response.body;
            }
        }.asLiveData();
    }
}
