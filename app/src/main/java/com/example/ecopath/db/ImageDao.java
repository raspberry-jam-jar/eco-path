package com.example.ecopath.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ecopath.vo.Image;

import java.util.List;

@Dao
public interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImages(List<Image> images);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(Image image);

    @Query("DELETE from Image")
    public void deleteAll();

    @Query("SELECT * from Image WHERE categoryId=:categoryId")
    public LiveData<List<Image>> getAll(Integer categoryId);
}
