package com.teplica.ecopath.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.teplica.ecopath.vo.MapPoint;

import java.util.List;

@Dao
public interface MapPointDao {
    @Query("SELECT * from map_points_table")
    LiveData<List<MapPoint>> getMapPoints();

    @Query("SELECT * from map_points_table WHERE isLoaded=:isLoaded")
    LiveData<List<MapPoint>> filterLoaded(Boolean isLoaded);

    @Query("SELECT * from map_points_table WHERE id=:id")
    MapPoint findById(Integer id);

    @Query("UPDATE map_points_table " +
            "SET mName=:name, mLatitude=:latitude, mLongitude=:longitude, fullSize=:fullSize " +
            "WHERE id=:id")
    void update(Integer id, String name, Float latitude, Float longitude, Double fullSize);

    @Update
    void updateAll(MapPoint mapPoint);

    @Query("UPDATE map_points_table SET isLoaded=:isLoaded, isLoading=:isLoading WHERE id=:id")
    void updateIsLoadedAndIsLoading(Integer id, Boolean isLoaded, Boolean isLoading);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MapPoint mapPoint);

    @Query("DELETE from map_points_table WHERE id NOT IN (:actualIds)")
    void deleteOutdated(List<Integer> actualIds);
}
