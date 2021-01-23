package com.teplica.ecopath.workers;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.teplica.ecopath.BuildConfig;
import com.teplica.ecopath.api.CategoriesListDeserializer;
import com.teplica.ecopath.api.EcoPathDataService;
import com.teplica.ecopath.api.ImagesListDeserializer;
import com.teplica.ecopath.api.MapPointListSerializer;
import com.teplica.ecopath.db.EcoPathDB;
import com.teplica.ecopath.util.LiveDataCallAdapterFactory;
import com.teplica.ecopath.vo.Category;
import com.teplica.ecopath.vo.CategoryWithImages;
import com.teplica.ecopath.vo.Image;
import com.teplica.ecopath.vo.MapPoint;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadWorker extends Worker {

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // TODO: refactor and add proper injection
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

    EcoPathDataService getEcoPathDataService() {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL)
                .addConverterFactory(createGsonConverter())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(EcoPathDataService.class);
    }

    EcoPathDB getEcoPathDb(Context context) {
        return Room.databaseBuilder(context, EcoPathDB.class,"ecoPath.db").build();
    }

    private String saveFile(ResponseBody body, File directory, String fileName) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
//        File outputFile = new File(Environment.getExternalStoragePublicDirectory(
//        Environment.DIRECTORY_DOWNLOADS), "journaldev-image-downloaded.jpg");
        File outputFile = new File(directory, fileName);
        OutputStream outputStream = new FileOutputStream(outputFile);
        long total = 0;
//        boolean downloadComplete = false;
//        int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        while ((count = inputStream.read(data)) != -1) {

            total += count;
            int progress = (int) ((double) (total * 100) / (double) fileSize);


//            updateNotification(progress);
            outputStream.write(data, 0, count);
//            downloadComplete = true;
        }
//        onDownloadComplete(downloadComplete);
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return outputFile.getAbsolutePath();
    }

    private File getOrCreateDir(Context context, String dirName) {
        File directory = new File(context.getFilesDir() + "/" +dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    @NotNull
    @Override
    public Result doWork() {
        try {
            Context context = getApplicationContext();
            String imageUrl = getInputData().getString("imageUrl");
            Integer mapPointId = getInputData().getInt("id", 0);

            EcoPathDataService service = getEcoPathDataService();
            EcoPathDB db = getEcoPathDb(context);

            MapPoint mapPoint;

            db.beginTransaction();
            try {
                mapPoint = db.mapPointDao().findById(mapPointId);
                mapPoint.setIsLoading(true);
                db.mapPointDao().updateAll(mapPoint);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            File directory = getOrCreateDir(context, mapPointId.toString());

            if (imageUrl != null) {
                Call<ResponseBody> mainImageRequest = service.downloadImage(imageUrl);

                String imagePath;
                try {
                    imagePath = saveFile(
                            Objects.requireNonNull(mainImageRequest.execute().body()),
                            directory,
                            "mainImage.jpeg"
                    );

                    mapPoint.setImagePath(imagePath);
                    db.mapPointDao().updateAll(mapPoint);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return Result.failure();
                }
            }

//          Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

            Call<List<CategoryWithImages>> categoriesRequest = service.getCategories(mapPointId);
            try {
                Response<List<CategoryWithImages>> response = categoriesRequest.execute();
                if (response.isSuccessful()) {
                    db.beginTransaction();
                    try {
                        db.categoryDao().delete(mapPointId);
                        for (CategoryWithImages categoryWithImages : response.body()) {
                            db.categoryDao().insert(categoryWithImages.category);
                            db.imageDao().delete(categoryWithImages.category.getId());
                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        System.out.println(e);
                    } finally {
                        db.endTransaction();
                    }

                    for (CategoryWithImages categoryWithImages : response.body()) {
                        Category category = categoryWithImages.category;
                        Integer categoryId = category.getId();

                        Call<ResponseBody> categorySmallImageRequest = service.downloadImage(
                                category.getImageSmallUrl()
                        );

                        String categorySmallImagePath = saveFile(
                                Objects.requireNonNull(categorySmallImageRequest.execute().body()),
                                directory,
                                "categorySmallImage" + categoryId + ".jpeg"
                        );
                        Call<ResponseBody> categoryBigImageRequest = service.downloadImage(
                                category.getImageBigUrl()
                        );

                        saveFile(
                                Objects.requireNonNull(categoryBigImageRequest.execute().body()),
                                directory,
                                "categoryBigImage" + categoryId + ".jpeg"
                        );

                        category.setImagePath(
                                categorySmallImagePath
                                        .replace("categorySmallImage" + categoryId + ".jpeg", "")
                        );

                        if (category.getAudioUrl() != null) {
                            Call<ResponseBody> categoryAudioRequest = service.downloadImage(
                                    category.getAudioUrl()
                            );
                            String categoryAudioPath = saveFile(
                                    Objects.requireNonNull(categoryAudioRequest.execute().body()),
                                    directory,
                                    "categoryAudio" + categoryId + ".jpeg"
                            );
                            category.setAudioPath(categoryAudioPath);
                        }

                        db.categoryDao().update(category);
                        Response<List<Image>> categoryImagesResponse = service
                                .getCategoryImages(categoryId)
                                .execute();
                        if (categoryImagesResponse.isSuccessful()) {
                            db.imageDao().delete(categoryId);

                            for (Image image : categoryImagesResponse.body()) {
                                image.setCategoryId(categoryId);
                                Call<ResponseBody> categoryGalleryImageRequest = service.downloadImage(
                                        image.getImageBigUrl()
                                );

                                String categoryGalleryImagePath = saveFile(
                                        Objects.requireNonNull(categoryGalleryImageRequest.execute().body()),
                                        directory,
                                        "categoryGalleryImage" +
                                                categoryId +
                                                "image" + image.getId() + ".jpeg"
                                );

                                image.setImagePath(categoryGalleryImagePath);
                                db.imageDao().insertImage(image);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e);
                return Result.failure();
            }

            db.beginTransaction();
            try {
                mapPoint.setIsLoading(false);
                mapPoint.setIsLoaded(true);
                db.mapPointDao().updateAll(mapPoint);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
            return Result.failure();
        }
        return Result.success();
    }
}
