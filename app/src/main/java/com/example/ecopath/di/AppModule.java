package com.example.ecopath.di;

import android.app.Application;

import androidx.room.Room;

import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.db.EcoPathDB;
import com.example.ecopath.db.MapPointDao;
import com.example.ecopath.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
    @Singleton
    @Provides
    EcoPathDataService provideEcoPathDataService() {
        return new Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(EcoPathDataService.class);
    }

    @Singleton @Provides
    EcoPathDB provideEcoPathDb(Application app) {
        return Room.databaseBuilder(app, EcoPathDB.class,"ecoPath.db").build();
    }

    @Singleton @Provides
    MapPointDao provideMapPointDao(EcoPathDB db) {
        return db.mapPointDao();
    }
}
