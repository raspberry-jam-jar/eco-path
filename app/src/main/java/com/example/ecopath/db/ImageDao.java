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

    @Query("DELETE from Image WHERE categoryId=:categoryId")
    public void delete(Integer categoryId);

    @Query("SELECT * from Image WHERE categoryId=:categoryId")
    public LiveData<List<Image>> getAll(Integer categoryId);

    @Query("SELECT * from Image WHERE id=:id")
    Image findById(Integer id);

    @Query("DELETE from Image WHERE categoryId=:categoryId and id NOT IN (:actualIds)")
    void deleteOutdated(Integer categoryId, List<Integer> actualIds);

    @Query("DELETE from Image WHERE categoryId NOT IN (:actualCategoriesIds)")
    void deleteForOutdatedCategories(List<Integer> actualCategoriesIds);
}
