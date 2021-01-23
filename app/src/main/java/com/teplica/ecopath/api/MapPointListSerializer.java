package com.teplica.ecopath.api;

import com.teplica.ecopath.vo.MapPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapPointListSerializer implements JsonDeserializer<List<MapPoint>> {
    @Override
    public List deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        List mapPointsList = new ArrayList<>();

        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonArray itemsJsonArray = jsonObject.get("items").getAsJsonArray();

        for (JsonElement itemsJsonElement : itemsJsonArray) {
            final JsonObject itemJsonObject = itemsJsonElement.getAsJsonObject();
            final Integer id = itemJsonObject.get("id").getAsInt();
            final String name = itemJsonObject.get("name").getAsString();
            final Float latitude = itemJsonObject.get("latitude").getAsFloat();
            final Float longitude = itemJsonObject.get("longitude").getAsFloat();
            final Double fullSize = itemJsonObject.get("full_size").getAsDouble();

            MapPoint mapPoint = new MapPoint(id, name, latitude, longitude, fullSize);

            try {
                JsonObject mainImageJson = itemJsonObject.get("image").getAsJsonObject();
//                JsonObject mainImageUrls = mainImageJson.get("thumbnails").getAsJsonObject();
//                mapPoint.setImageSmallUrl(mainImageUrls.get("small").getAsString());
                mapPoint.setImageSmallUrl(mainImageJson.get("path").getAsString());

            } catch (Exception e) {
                System.out.println(e);
            }

            mapPointsList.add(mapPoint);
        }

        return mapPointsList;
    }
}
