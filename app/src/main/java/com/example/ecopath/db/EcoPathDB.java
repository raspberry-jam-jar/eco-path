package com.example.ecopath.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.ecopath.vo.MapPoint;

@Database(entities = {MapPoint.class}, version = 1, exportSchema = false)
public abstract class EcoPathDB extends RoomDatabase {
    public abstract MapPointDao mapPointDao();
}
