package com.example.ecopath.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecopath.vo.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Category category);

    @Update
    public void update(Category... categories);

    @Delete
    public void delete(Category... categories);

    @Query("SELECT * from Category WHERE pointId=:mapPointId")
    public List<Category> findForMapPoint(Integer mapPointId);
}
