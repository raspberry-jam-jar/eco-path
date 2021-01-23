package com.teplica.ecopath.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.teplica.ecopath.binding.BaseModel;

@Entity()
public class Image implements BaseModel {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer id;
    private Integer categoryId;
    private String imageSmallUrl;
    private String imageBigUrl;
    private String imagePath;

    public Image(Integer id, String imageSmallUrl, String imageBigUrl) {
        this.id = id;
        this.imageBigUrl = imageBigUrl;
        this.imageSmallUrl = imageSmallUrl;
    }

    @NonNull
    public Integer getId() { return id; }
    public Integer getCategoryId() { return categoryId; }
    public String getImageBigUrl() { return imageBigUrl; }
    public String getImageSmallUrl() { return imageSmallUrl; }
    public String getImagePath() { return imagePath; }

    public void setId(Integer id) { this.id = id; }
    public void setCategoryId(@NonNull Integer categoryId) { this.categoryId = categoryId; }
    public void setImageBigUrl(String imageBigUrl) { this.imageBigUrl = imageBigUrl; }
    public void setImageSmallUrl(String imageSmallUrl) { this.imageSmallUrl = imageSmallUrl; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String getPath(String imageSize) {
        if (imagePath == null){
            return "";
        }
        return imagePath;
    }
}
