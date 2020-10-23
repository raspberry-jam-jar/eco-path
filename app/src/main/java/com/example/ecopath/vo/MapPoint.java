package com.example.ecopath.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ecopath.binding.BaseModel;

@Entity(tableName = "map_points_table")
public class MapPoint implements BaseModel {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int mId;
    @NonNull
    private String mName;
    @NonNull
    private Float mLatitude;
    @NonNull
    private Float mLongitude;
    private String imageSmallUrl;
    private Double fullSize;
    @ColumnInfo(defaultValue="0")
    private Boolean isLoaded;
    private String imagePath;


//    @Ignore
//    Bitmap picture;


    public MapPoint(int id, @NonNull String name, @NonNull Float latitude, @NonNull Float longitude,
                    Double fullSize) {
        this.mId = id;
        this.mName = name;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.fullSize = fullSize;
    }

    public void setId(int id) { this.mId = id; }
    public void setName(@NonNull String name) { this.mName = name; }
    public void setLatitude(@NonNull Float latitude) { this.mLatitude = latitude; }
    public void setLongitude(@NonNull Float longitude) { this.mLongitude = longitude; }
    public void setImageSmallUrl(String imageSmallUrl) { this.imageSmallUrl = imageSmallUrl; }
    public void setFullSize(Double fullSize) { this.fullSize = fullSize; }
    public void setIsLoaded(Boolean loaded) { this.isLoaded = loaded; }
    public void setImagePath(String path) {this.imagePath = path;}

    public int getId(){ return this.mId; }
    public String getName(){ return this.mName; }
    public Float getLatitude(){ return this.mLatitude; }
    public Float getLongitude(){ return this.mLongitude; }
    public String getImageSmallUrl() { return imageSmallUrl; }
    public Double getFullSize() { return fullSize; }
    public Boolean getIsLoaded() { return isLoaded; }
    public String getImagePath() { return this.imagePath; }

    public String getSizeReadable() {
        Double kb_size =  this.fullSize / 1024;
        Double mb_size = kb_size / 1024;

        if (mb_size > 0) {
            return Math.round(mb_size * 100.0) / 100.0 + " Mb";
        } else {
            return Math.round(kb_size * 100.0) / 100.0 + " Kb";
        }
    }

    @Override
    public String getPath() {
        if (this.isLoaded && this.imagePath != null) {
            return this.imagePath;
        } else {
            return "";
        }
    }
}
