package com.teplica.ecopath.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.teplica.ecopath.vo.CategoryWithImages;

import java.util.List;

@Dao
public interface CategoryWithImagesDao {
    @Transaction
    @Query("SELECT * FROM category WHERE pointId=:mapPointId")
    public LiveData<List<CategoryWithImages>> getAll(Integer mapPointId);
}
