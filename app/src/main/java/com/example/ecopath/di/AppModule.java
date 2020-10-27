package com.example.ecopath.di;

import android.app.Application;
import android.content.res.Resources;

import androidx.room.Room;

import com.example.ecopath.BuildConfig;
import com.example.ecopath.R;
import com.example.ecopath.api.CategoriesListDeserializer;
import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.api.ImagesListDeserializer;
import com.example.ecopath.db.CategoryDao;
import com.example.ecopath.db.CategoryWithImagesDao;
import com.example.ecopath.db.EcoPathDB;
import com.example.ecopath.db.ImageDao;
import com.example.ecopath.db.MapPointDao;
import com.example.ecopath.util.LiveDataCallAdapterFactory;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Image;
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

import static android.provider.Settings.System.getString;

@Module(includes = ViewModelModule.class)
class AppModule {
    private static Converter.Factory createGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();

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
                .baseUrl(BuildConfig.SERVER_URL + BuildConfig.API_VERSION)
                .addConverterFactory(createGsonConverter())
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
