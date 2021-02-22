package com.teplica.ecopath.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.teplica.ecopath.AppExecutors;
import com.teplica.ecopath.api.ApiResponse;
import com.teplica.ecopath.api.EcoPathDataService;
import com.teplica.ecopath.db.CategoryDao;
import com.teplica.ecopath.db.CategoryWithImagesDao;
import com.teplica.ecopath.db.EcoPathDB;
import com.teplica.ecopath.util.AbsentLiveData;
import com.teplica.ecopath.vo.Category;
import com.teplica.ecopath.vo.CategoryWithImages;
import com.teplica.ecopath.vo.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CategoryRepository {
    private final EcoPathDB db;
    private final CategoryWithImagesDao categoryWithImagesDao;
    private final CategoryDao categoryDao;
    private final EcoPathDataService ecoPathDataService;
    private final AppExecutors appExecutors;

    @Inject
    public CategoryRepository(AppExecutors appExecutors, EcoPathDB db,
                              CategoryWithImagesDao categoryWithImagesDao,
                              CategoryDao categoryDao, EcoPathDataService ecoPathDataService) {
        this.db = db;
        this.categoryWithImagesDao = categoryWithImagesDao;
        this.categoryDao = categoryDao;
        this.appExecutors = appExecutors;
        this.ecoPathDataService = ecoPathDataService;
    }

    public LiveData<List<CategoryWithImages>> loadFromDb(Integer mapPointId) {
        return Transformations.switchMap(
            categoryWithImagesDao.getAll(mapPointId), categories -> {
                if (categories == null || categories.isEmpty()) {
                    return AbsentLiveData.create();
                } else {
                    return categoryWithImagesDao.getAll(mapPointId);
                }
            });
    }

    public LiveData<Resource<List<CategoryWithImages>>> getAllCategories(Integer mapPointId) {
        return new NetworkBoundResource<List<CategoryWithImages>, List<CategoryWithImages>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<CategoryWithImages> categories) {
                db.beginTransaction();
                try {
                    List<Integer> actualIds = new ArrayList<>();

                    for (CategoryWithImages categoryWithImages : categories) {
                        Category remoteServerCategory = categoryWithImages.category;
                        Integer remoteServerCategoryId = remoteServerCategory.getId();
                        actualIds.add(remoteServerCategoryId);

                        Category savedInDbCategory = categoryDao.findById(remoteServerCategoryId);
                        if (savedInDbCategory == null) {
                            categoryDao.insert(categoryWithImages.category);
                        } else {
                            remoteServerCategory.setImagePath(savedInDbCategory.getImagePath());
                            remoteServerCategory.setAudioPath(savedInDbCategory.getAudioPath());
                            categoryDao.update(remoteServerCategory);
                        }
                    }
                    categoryDao.deleteOutdated(mapPointId, actualIds);
                    db.setTransactionSuccessful();
                }
                catch (Exception e){
                    System.out.println(e);
                } finally {
                    db.endTransaction();
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
