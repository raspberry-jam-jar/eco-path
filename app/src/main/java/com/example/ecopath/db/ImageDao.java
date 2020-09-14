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

    @Query("DELETE from Image")
    public void deleteAll();

    @Query("SELECT * from Image")
    public LiveData<List<Image>> getImages();
}
