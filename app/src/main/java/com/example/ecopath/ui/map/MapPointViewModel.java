package com.example.ecopath.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.ecopath.workers.DownloadWorker;
import com.example.ecopath.vo.MapPoint;
import com.example.ecopath.repository.MapPointRepository;
import com.example.ecopath.vo.Resource;

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
//        mAllMapPoints = mapPointRepository.getAllMapPoints();
//        loadedMapPoints = mapPointRepository.getLoadedMapPoints();
//        toBeLoadedMapPoints = mapPointRepository.getToBeLoadedMapPoints();
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
        Data.Builder builder = new Data.Builder();
        builder.putInt("id", mapPoint.getId());
        builder.putString("imageUrl", mapPoint.getImageSmallUrl());

        Data mapPointData = builder.build();

        WorkRequest downloadWorkRequest =
                new OneTimeWorkRequest.Builder(DownloadWorker.class)
                        .setInputData(mapPointData)
                        .build();
        workManager.enqueue(downloadWorkRequest);
        return downloadWorkRequest.getId();
    }
}
