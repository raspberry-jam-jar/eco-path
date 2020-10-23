package com.example.ecopath.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecopath.vo.MapPoint;

import java.util.List;

@Dao
public interface MapPointDao {
    @Query("SELECT * from map_points_table")
    public LiveData<List<MapPoint>> getMapPoints();

    @Query("SELECT * from map_points_table WHERE isLoaded=:isLoaded")
    public LiveData<List<MapPoint>> filterLoaded(Boolean isLoaded);

    @Query("SELECT * from map_points_table WHERE id=:id")
    MapPoint findById(Integer id);

    @Query("UPDATE map_points_table " +
            "SET mName=:name, mLatitude=:latitude, mLongitude=:longitude, fullSize=:fullSize " +
            "WHERE id=:id")
    void update(Integer id, String name, Float latitude, Float longitude, Double fullSize);

    @Update
    void updateAll(MapPoint mapPoint);

    @Query("UPDATE map_points_table SET isLoaded=:isLoaded WHERE id=:id")
    void updateIsLoaded(Integer id, Boolean isLoaded);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MapPoint mapPoint);

    @Query("DELETE from map_points_table WHERE id NOT IN (:actualIds)")
    void deleteOutdated(List<Integer> actualIds);
}
