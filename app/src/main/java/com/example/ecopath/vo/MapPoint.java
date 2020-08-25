package com.example.ecopath.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "map_points_table")
public class MapPoint {
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;
    @NonNull
    @SerializedName("name")
    private String mName;
    @NonNull
    @SerializedName("latitude")
    private Float mLatitude;
    @NonNull
    @SerializedName("longitude")
    private Float mLongitude;

    public MapPoint(int id, @NonNull String name, @NonNull Float latitude, @NonNull Float longitude) {
        this.mId = id;
        this.mName = name;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public void setId(int id) { this.mId = id; }
    public void setName(@NonNull String name) { this.mName = name; }
    public void setLatitude(@NonNull Float latitude) { this.mLatitude = latitude; }
    public void setLongitude(@NonNull Float longitude) { this.mLongitude = longitude; }

    public int getId(){ return this.mId; }
    public String getName(){ return this.mName; }
    public Float getLatitude(){ return this.mLatitude; }
    public Float getLongitude(){ return this.mLongitude; }
}
