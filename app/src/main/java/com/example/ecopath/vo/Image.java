package com.example.ecopath.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Image {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer id;
    @ColumnInfo(defaultValue = "0")
    private Boolean isMain;
    @NonNull
    private Integer categoryId;
    private String imageSmallUrl;
    private String imageBigUrl;

    public Image(Integer id, @NonNull Integer categoryId, String imageSmallUrl, String imageBigUrl,
                 Boolean isMain) {
        this.id = id;
        this.categoryId = categoryId;
        this.imageBigUrl = imageBigUrl;
        this.imageSmallUrl = imageSmallUrl;
        this.isMain = isMain;
    }

    @NonNull
    public Integer getId() { return id; }
    @NonNull
    public Integer getCategoryId() { return categoryId; }
    public String getImageBigUrl() { return imageBigUrl; }
    public String getImageSmallUrl() { return imageSmallUrl; }
    public Boolean getIsMain() { return isMain; }

    public void setId(Integer id) { this.id = id; }
    public void setCategoryId(@NonNull Integer categoryId) { this.categoryId = categoryId; }
    public void setImageBigUrl(String imageBigUrl) { this.imageBigUrl = imageBigUrl; }
    public void setImageSmallUrl(String imageSmallUrl) { this.imageSmallUrl = imageSmallUrl; }
    public void setIsMain(Boolean isMain) { this.isMain = isMain; }
}
