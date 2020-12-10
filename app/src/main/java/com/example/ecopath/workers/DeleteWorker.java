package com.example.ecopath.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ecopath.db.EcoPathDB;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DeleteWorker extends Worker {
    public DeleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    EcoPathDB getEcoPathDb(Context context) {
        return Room.databaseBuilder(context, EcoPathDB.class,"ecoPath.db").build();
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        Integer mapPointId = getInputData().getInt("id", 0);
        if (mapPointId == 0) {
            return Result.success();
        }

        File mapPointFilesDirectory = new File(context.getFilesDir() + "/" + mapPointId);
        try {
            FileUtils.deleteDirectory(mapPointFilesDirectory);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        }

        return Result.success();
    }
}
