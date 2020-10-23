package com.example.ecopath.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.ecopath.vo.Image;
import com.example.ecopath.vo.MapPoint;
import com.example.ecopath.vo.Category;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MapPoint.class, Category.class, Image.class},
          version = 1, exportSchema = false)
public abstract class EcoPathDB extends RoomDatabase {
    public abstract MapPointDao mapPointDao();
    public abstract CategoryWithImagesDao categoryWithImagesDao();
    public abstract CategoryDao categoryDao();
    public abstract ImageDao imageDao();

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

}
