package com.example.ecopath.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.ecopath.AppExecutors;
import com.example.ecopath.api.ApiData;
import com.example.ecopath.api.ApiResponse;
import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.db.EcoPathDB;
import com.example.ecopath.db.MapPointDao;
import com.example.ecopath.util.AbsentLiveData;
import com.example.ecopath.vo.MapPoint;
import com.example.ecopath.vo.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MapPointRepository {
    private final EcoPathDB db;
    private final MapPointDao mapPointDao;
    private final EcoPathDataService ecoPathDataService;
    private final AppExecutors appExecutors;

    @Inject
    public MapPointRepository(AppExecutors appExecutors, EcoPathDB db, MapPointDao mapPointDao,
                              EcoPathDataService ecoPathDataService) {
        this.db = db;
        this.mapPointDao = mapPointDao;
        this.ecoPathDataService = ecoPathDataService;
        this.appExecutors = appExecutors;
    }

    public LiveData<Resource<List<MapPoint>>> getAllMapPoints() {
        return new NetworkBoundResource<List<MapPoint>, ApiData>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull ApiData item) {
                List<MapPoint> mapPoints = item.getApiPointsList();
                db.beginTransaction();
                try {
                    mapPointDao.deleteAllMapPoints();
                    mapPointDao.insertMapPoints(mapPoints);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MapPoint> data) { return true; }

            @NonNull
            @Override
            protected LiveData<List<MapPoint>> loadFromDb() {
                return Transformations.switchMap(mapPointDao.getMapPoints(), mapPoints -> {
                    if (mapPoints == null) {
                        return AbsentLiveData.create();
                    } else {
                        return mapPointDao.getMapPoints();
                    }
                });
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ApiData>> createCall() {
                return ecoPathDataService.listMapPoints();
            }

            @Override
            protected ApiData processResponse(ApiResponse<ApiData> response) {
                return response.body;
            }
        }.asLiveData();
    }
}
