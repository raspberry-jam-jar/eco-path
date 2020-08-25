package com.example.ecopath.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ecopath.vo.MapPoint;

import java.util.List;

@Dao
public interface MapPointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMapPoints(List<MapPoint> mapPoints);

    @Query("DELETE from map_points_table")
    void deleteAllMapPoints();

    @Query("SELECT * from map_points_table")
    public LiveData<List<MapPoint>> getMapPoints();
}
