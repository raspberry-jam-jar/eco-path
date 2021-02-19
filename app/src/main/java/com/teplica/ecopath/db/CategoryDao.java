package com.teplica.ecopath.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.teplica.ecopath.vo.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Query("DELETE from Category WHERE pointId=:mapPointId")
    void delete(Integer mapPointId);

    @Query("SELECT * from Category WHERE id=:id")
    Category findById(Integer id);

    @Update
    void update(Category category);

    @Query("DELETE from Category WHERE pointId=:mapPointId and id NOT IN (:actualIds)")
    void deleteOutdated(Integer mapPointId, List<Integer> actualIds);
}
