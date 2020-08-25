package com.example.ecopath.api;

import com.example.ecopath.vo.MapPoint;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiData {
    @SerializedName("items")
    private List<MapPoint> items;

    public ApiData(List<MapPoint> items) {
        this.items = items;
    }

    public List<MapPoint> getApiPointsList() {
        return items;
    }
}
