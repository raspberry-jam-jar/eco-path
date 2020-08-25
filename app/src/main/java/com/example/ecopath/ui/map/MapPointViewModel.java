package com.example.ecopath.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecopath.vo.MapPoint;
import com.example.ecopath.repository.MapPointRepository;
import com.example.ecopath.vo.Resource;

import java.util.List;

import javax.inject.Inject;

public class MapPointViewModel extends ViewModel {
    private LiveData<Resource<List<MapPoint>>> mAllMapPoints;

    @Inject
    public MapPointViewModel(MapPointRepository mapPointRepository) {
        mAllMapPoints = mapPointRepository.getAllMapPoints();
    }

    public LiveData<Resource<List<MapPoint>>> getAllMapPoints() { return mAllMapPoints; }
}
