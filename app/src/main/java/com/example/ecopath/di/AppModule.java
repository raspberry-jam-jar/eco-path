package com.example.ecopath.di;

import android.app.Application;

import androidx.room.Room;

import com.example.ecopath.api.CategoriesListDeserializer;
import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.db.CategoryDao;
import com.example.ecopath.db.CategoryWithImagesDao;
import com.example.ecopath.db.EcoPathDB;
import com.example.ecopath.db.ImageDao;
import com.example.ecopath.db.MapPointDao;
import com.example.ecopath.util.LiveDataCallAdapterFactory;
import com.example.ecopath.vo.CategoryWithImages;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
    private static Converter.Factory createGsonConverter(Type type, Object typeAdapter) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);

    }

    @Singleton
    @Provides
    EcoPathDataService provideEcoPathDataService() {
        Type categoriesList = new TypeToken<List<CategoryWithImages>>() {}.getType();
        Object categoriesListAdapter = new CategoriesListDeserializer();

        return new Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(createGsonConverter(categoriesList, categoriesListAdapter))
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

    @Singleton @Provides
    CategoryWithImagesDao provideCategoryWithImagesDao(EcoPathDB db) {
        return db.categoryWithImagesDao();
    }

    @Singleton @Provides
    CategoryDao provideCategoryDao(EcoPathDB db) {
        return db.categoryDao();
    }

    @Singleton @Provides
    ImageDao provideImageDao(EcoPathDB db) {
        return db.imageDao();
    }
}
