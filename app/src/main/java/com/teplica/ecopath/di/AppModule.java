package com.teplica.ecopath.di;

import android.app.Application;

import androidx.room.Room;
import androidx.work.WorkManager;

import com.teplica.ecopath.BuildConfig;
import com.teplica.ecopath.api.CategoriesListDeserializer;
import com.teplica.ecopath.api.EcoPathDataService;
import com.teplica.ecopath.api.ImagesListDeserializer;
import com.teplica.ecopath.api.MapPointListSerializer;
import com.teplica.ecopath.db.CategoryDao;
import com.teplica.ecopath.db.CategoryWithImagesDao;
import com.teplica.ecopath.db.EcoPathDB;
import com.teplica.ecopath.db.ImageDao;
import com.teplica.ecopath.db.MapPointDao;
import com.teplica.ecopath.util.LiveDataCallAdapterFactory;
import com.teplica.ecopath.vo.CategoryWithImages;
import com.teplica.ecopath.vo.Image;
import com.teplica.ecopath.vo.MapPoint;
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
    private static Converter.Factory createGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        Type mapPoint = new TypeToken<List<MapPoint>>() {}.getType();
        Object mapPointListSerializer = new MapPointListSerializer();
        gsonBuilder.registerTypeAdapter(mapPoint, mapPointListSerializer);

        Type category= new TypeToken<List<CategoryWithImages>>() {}.getType();
        Object categoriesListDeserializer = new CategoriesListDeserializer();
        gsonBuilder.registerTypeAdapter(category, categoriesListDeserializer);

        Type image = new TypeToken<List<Image>>() {}.getType();
        Object imagesListDeserializer = new ImagesListDeserializer();
        gsonBuilder.registerTypeAdapter(image, imagesListDeserializer);

        Gson gson = gsonBuilder.create();
        return GsonConverterFactory.create(gson);

    }

    @Singleton
    @Provides
    EcoPathDataService provideEcoPathDataService() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(createGsonConverter())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(EcoPathDataService.class);
    }

    @Singleton @Provides
    WorkManager provideWorkManager(Application app) {
        return WorkManager.getInstance(app);
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
