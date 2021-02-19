package com.teplica.ecopath.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.teplica.ecopath.workers.DeleteWorker;
import com.teplica.ecopath.workers.DownloadWorker;
import com.teplica.ecopath.vo.MapPoint;
import com.teplica.ecopath.repository.MapPointRepository;
import com.teplica.ecopath.vo.Resource;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

public class MapPointViewModel extends ViewModel {
    MapPointRepository mapPointRepository;
    WorkManager workManager;
    private LiveData<List<WorkInfo>> savedWorkInfo;

    private LiveData<Resource<List<MapPoint>>> mAllMapPoints;
    private LiveData<List<MapPoint>> loadedMapPoints;
    private LiveData<List<MapPoint>> toBeLoadedMapPoints;

    @Inject
    public MapPointViewModel(MapPointRepository mapPointRepository, WorkManager workManager) {
        this.mapPointRepository = mapPointRepository;
        this.workManager = workManager;
    }

    LiveData<WorkInfo> getOutputWorkInfo(UUID workerId) {
        return workManager.getWorkInfoByIdLiveData(workerId);
    }

    public void update(MapPoint mapPoint) { mapPointRepository.update(mapPoint); }

    public void updateIsLoaded(MapPoint mapPoint) { mapPointRepository.updateIsLoaded(mapPoint); }

    public LiveData<Resource<List<MapPoint>>> getAllMapPoints() { return mapPointRepository.getAllMapPoints(); }

    public LiveData<List<MapPoint>> getLoadedMapPoints() {return mapPointRepository.getLoadedMapPoints(); }

    public LiveData<List<MapPoint>> getToBeLoadedMapPoint() {return mapPointRepository.getToBeLoadedMapPoints();}

    public UUID downloadMapPoint(MapPoint mapPoint) {
        mapPoint.setIsLoading(true);
        update(mapPoint);

        Data.Builder builder = new Data.Builder();
        builder.putInt("id", mapPoint.getId());
        builder.putString("imageUrl", mapPoint.getImageSmallUrl());

        Data mapPointData = builder.build();

        OneTimeWorkRequest downloadWorkRequest =
                new OneTimeWorkRequest.Builder(DownloadWorker.class)
                        .setInputData(mapPointData)
                        .build();

        WorkContinuation continuation = workManager.beginUniqueWork(
                "download_" + mapPoint.getId(),
                ExistingWorkPolicy.KEEP,
                downloadWorkRequest
        );
        continuation.enqueue();

        return downloadWorkRequest.getId();
    }

    public UUID deleteMapPoint(MapPoint mapPoint) {
        mapPoint.setIsLoading(true);
        update(mapPoint);

        Data.Builder builder = new Data.Builder();
        builder.putInt("id", mapPoint.getId());

        Data mapPointData = builder.build();

        OneTimeWorkRequest deleteWorkRequest =
                new OneTimeWorkRequest.Builder(DeleteWorker.class)
                        .setInputData(mapPointData)
                        .build();

        workManager.enqueue(deleteWorkRequest);

        return deleteWorkRequest.getId();
    }
}
