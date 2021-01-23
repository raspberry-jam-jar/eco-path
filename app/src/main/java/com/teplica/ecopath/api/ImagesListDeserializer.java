package com.teplica.ecopath.api;

import com.teplica.ecopath.vo.Image;
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

public class ImagesListDeserializer implements JsonDeserializer<List<Image>> {
    @Override
    public List deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        List categoryImagesList = new ArrayList<>();

        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonArray itemsJsonArray = jsonObject.get("items").getAsJsonArray();

        for (JsonElement itemsJsonElement : itemsJsonArray) {
            final JsonObject itemJsonObject = itemsJsonElement.getAsJsonObject();
            final Integer imageId = itemJsonObject.get("id").getAsInt();

            try {
                JsonObject imageUrls = itemJsonObject.get("thumbnails").getAsJsonObject();
                categoryImagesList.add(
                        new Image(
                                imageId,
                                imageUrls.get("small").getAsString(),
                                imageUrls.get("common").getAsString()
                        )
                );
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance()
                        .log("Error while decoding image json in CategoriesListDeserializer");
            }
        }
        return categoryImagesList;
    }
}
