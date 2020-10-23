package com.example.ecopath.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ecopath.BuildConfig;
import com.example.ecopath.api.CategoriesListDeserializer;
import com.example.ecopath.api.EcoPathDataService;
import com.example.ecopath.api.ImagesListDeserializer;
import com.example.ecopath.api.MapPointListSerializer;
import com.example.ecopath.db.EcoPathDB;
import com.example.ecopath.util.LiveDataCallAdapterFactory;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Image;
import com.example.ecopath.vo.MapPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
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

    private String saveImage(ResponseBody body, Context context, String fileName) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
//        File outputFile = new File(Environment.getExternalStoragePublicDirectory(
//        Environment.DIRECTORY_DOWNLOADS), "journaldev-image-downloaded.jpg");
        File outputFile = new File(context.getFilesDir(), fileName);
        OutputStream outputStream = new FileOutputStream(outputFile);
        long total = 0;
        boolean downloadComplete = false;
        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        while ((count = inputStream.read(data)) != -1) {

            total += count;
            int progress = (int) ((double) (total * 100) / (double) fileSize);


//            updateNotification(progress);
            outputStream.write(data, 0, count);
            downloadComplete = true;
        }
//        onDownloadComplete(downloadComplete);
        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return outputFile.getAbsolutePath();
    }


//        String downloadImage(String imageUrl, String fileName, Context context) {
//        File imageFile = new File(context.getFilesDir(), fileName);
//
//        Glide.with(context)
//            .load(BuildConfig.SERVER_URL + imageUrl)
//            .into(new CustomTarget<Drawable>() {
//                @Override
//                public void onResourceReady(@NonNull Drawable resource,
//                                            @Nullable Transition<? super Drawable> transition) {
//                    Bitmap imageBitmap = ((BitmapDrawable) resource).getBitmap();
////                    File imageFile = new File(context.getFilesDir(), fileName);
//
//                    try {
//                        FileOutputStream outputStream = new FileOutputStream(imageFile);
//                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                        outputStream.close();
//                    } catch (FileNotFoundException e) {
//                        System.out.println("Error to save file: path not found");
//                        e.printStackTrace();
//
//                    } catch (IOException e) {
//                        System.out.println("Error to save file");
//                        e.printStackTrace();
//                    }
//                }
//                @Override
//                public void onLoadCleared(@Nullable Drawable placeholder) {
//                    // Remove the Drawable provided in onResourceReady from any Views and ensure
//                    // no references to it remain.
//                }
//
//                @Override
//                public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                    super.onLoadFailed(errorDrawable);
//                }
//            });
//        return imageFile.getAbsolutePath();
//    }

    @Override
    public Result doWork() {
//        try {
            Context context = getApplicationContext();
            String imageUrl = getInputData().getString("imageUrl");
            Integer mapPointId = getInputData().getInt("id", 0);

            if (imageUrl == null) {
                return Result.success();
            }

            EcoPathDataService service = getEcoPathDataService();
            Call<ResponseBody> request = service.downloadImage(imageUrl.replaceFirst("/attaches/images/", ""));

            String imagePath;
            try {
                imagePath = saveImage(Objects.requireNonNull(request.execute().body()), context,
                        "test_" + mapPointId + ".jpeg");
                EcoPathDB db = getEcoPathDb(context);

                MapPoint mapPoint = db.mapPointDao().findById(mapPointId);
                mapPoint.setImagePath(imagePath);
                db.mapPointDao().updateAll(mapPoint);
                return Result.success();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                return Result.failure();
            }

            //                        Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);



//            EcoPathDataService service = getEcoPathDataService();
//            Call<List<CategoryWithImages>> categoriesCall = service.getCategories(mapPointId);
//
//            categoriesCall.enqueue(new Callback<List<CategoryWithImages>>() {
//                @Override
//                public void onResponse(Call<List<CategoryWithImages>> call, Response<List<CategoryWithImages>> response) {
//                    if (response.isSuccessful()) {
//                        db.beginTransaction();
//                        try {
//                            db.categoryDao().delete(mapPointId);
//                            for (CategoryWithImages categoryWithImages : response.body()) {
//                                db.categoryDao().insert(categoryWithImages.category);
//                                db.imageDao().delete(categoryWithImages.category.getId());
//                                db.setTransactionSuccessful();
//                            }
//                        } catch (Exception e) {
//                            System.out.println(e);
//                        } finally {
//                            db.endTransaction();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<CategoryWithImages>> call, Throwable t) {
//
//                }
//            });
//        } catch (Exception e) {
//            return Result.failure();
//        }
//
//        return Result.success();
    }
}
