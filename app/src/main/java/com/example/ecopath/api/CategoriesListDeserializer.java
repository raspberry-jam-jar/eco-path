package com.example.ecopath.api;

import com.example.ecopath.vo.Category;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Image;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CategoriesListDeserializer implements JsonDeserializer<List<CategoryWithImages>> {
    @Override
    public List deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        List categoriesList = new ArrayList<>();

        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonArray itemsJsonArray = jsonObject.get("items").getAsJsonArray();

        for (JsonElement itemsJsonElement : itemsJsonArray) {

            final JsonObject itemJsonObject = itemsJsonElement.getAsJsonObject();
            final Integer categoryId = itemJsonObject.get("id").getAsInt();
            final String name = itemJsonObject.get("name").getAsString();
            final String preview = itemJsonObject.get("preview").getAsString();
            final String description = itemJsonObject.get("description").getAsString();
            final Integer pointId = itemJsonObject.get("point_id").getAsInt();

            CategoryWithImages category = new CategoryWithImages();
            category.setCategory(new Category(
                    categoryId, name, pointId, preview, description
            ));

            try {
                JsonObject mainImageJson = itemJsonObject.get("image").getAsJsonObject();
                JsonObject mainImageUrls = mainImageJson.get("thumbnails").getAsJsonObject();

                category.category.setImageSmallUrl(mainImageUrls.get("middle").getAsString());
                category.category.setImageBigUrl(mainImageUrls.get("common").getAsString());

            } catch (Exception e) {
                FirebaseCrashlytics.getInstance()
                        .log("Error while decoding image json in CategoriesListDeserializer");
            }

            try {
                JsonObject audioJson = itemJsonObject.get("audio").getAsJsonObject();
                category.category.setAudioUrl(audioJson.get("path").getAsString());
            } catch (IllegalStateException e) {
                FirebaseCrashlytics.getInstance()
                        .log("Error while decoding audio json in CategoriesListDeserializer");
            }

            categoriesList.add(category);
        }
        return categoriesList;
    }
}
