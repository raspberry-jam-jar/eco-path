package com.example.ecopath.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.ecopath.AppExecutors;
import com.example.ecopath.api.ApiResponse;
import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.db.EcoPathDB;
import com.example.ecopath.db.MapPointDao;
import com.example.ecopath.util.AbsentLiveData;
import com.example.ecopath.vo.MapPoint;
import com.example.ecopath.vo.Resource;

import java.util.ArrayList;
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

    public LiveData<List<MapPoint>> getLoadedMapPoints() {
        return mapPointDao.filterLoaded(true);
    }

    public LiveData<List<MapPoint>> getToBeLoadedMapPoints() {
        return mapPointDao.filterLoaded(false);
    }

    public void update(MapPoint mapPoint) {
        EcoPathDB.databaseWriteExecutor.execute(() -> {
            mapPointDao.updateAll(mapPoint);
        });
    }

    public void updateIsLoaded(MapPoint mapPoint) {
        EcoPathDB.databaseWriteExecutor.execute(() -> {
            mapPointDao.updateIsLoadedAndIsLoading(
                    mapPoint.getId(), mapPoint.getIsLoaded(), mapPoint.getIsLoading()
            );
        });
    }

    public LiveData<Resource<List<MapPoint>>> getAllMapPoints() {
        return new NetworkBoundResource<List<MapPoint>, List<MapPoint>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<MapPoint> mapPoints) {
                db.beginTransaction();
                try {
                    List<Integer> actualIds = new ArrayList<>();

                    for (MapPoint mapPoint : mapPoints) {
                        actualIds.add(mapPoint.getId());

                        if (mapPointDao.findById(mapPoint.getId()) == null) {
                            mapPoint.setIsLoaded(false);
                            mapPointDao.insert(mapPoint);
                        } else {
                            mapPointDao.update(
                                    mapPoint.getId(), mapPoint.getName(), mapPoint.getLatitude(),
                                    mapPoint.getLongitude(), mapPoint.getFullSize()
                            );
                        }
                    }
                    // TODO delete linked instances and files
                    mapPointDao.deleteOutdated(actualIds);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<MapPoint> data) {
                return true;
            }

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
            protected LiveData<ApiResponse<List<MapPoint>>> createCall() {
                return ecoPathDataService.listMapPoints();
            }

            @Override
            protected List<MapPoint> processResponse(ApiResponse<List<MapPoint>> response) {
                return response.body;
            }
        }.asLiveData();
    }
}
