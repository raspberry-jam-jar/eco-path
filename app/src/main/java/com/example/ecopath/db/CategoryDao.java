package com.example.ecopath.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecopath.vo.Category;
import com.example.ecopath.vo.MapPoint;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertMany(Category... categories);

    @Query("DELETE from Category WHERE pointId=:mapPointId")
    public void delete(Integer mapPointId);

    @Query("SELECT * from Category WHERE pointId=:mapPointId")
    public List<Category> findForMapPoint(Integer mapPointId);

    @Query("SELECT * from Category WHERE id=:id")
    Category findById(Integer id);

    @Update
    void update(Category category);

    @Query("DELETE from Category WHERE pointId=:mapPointId and id NOT IN (:actualIds)")
    void deleteOutdated(Integer mapPointId, List<Integer> actualIds);
}
