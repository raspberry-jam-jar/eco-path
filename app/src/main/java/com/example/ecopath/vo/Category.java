package com.example.ecopath.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ecopath.binding.BaseModel;

@Entity()
public class Category implements BaseModel {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private Integer id;
    @NonNull
    private String name;
    private String preview;
    private String description;

    private String imageSmallUrl;
    private String imageBigUrl;
    private String imagePath;
    // TODO create index
    @NonNull
    private Integer pointId;

    private String audioUrl;
    private String audioPath;

    public Category(@NonNull Integer id, @NonNull String name, @NonNull Integer pointId,
                    String preview, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.preview = preview;
        this.pointId = pointId;
    }

    @NonNull
    public Integer getId() { return id; }
    @NonNull
    public String getName() { return name; }
    public String getPreview() { return preview; }
    public String getDescription() { return description; }
    public Integer getPointId() { return pointId; }
    public String getImagePath() { return imagePath; }
    @Override
    public String getPath() {
        if (imagePath == null){
            return "";
        }
        return imagePath;
    }

    public String getImageSmallUrl() { return imageSmallUrl; }
    public String getImageBigUrl() { return imageBigUrl; }
    public String getAudioUrl() { return audioUrl; }
    public String getAudioPath() { return audioPath; }

    public void setImageSmallUrl(String imageSmallUrl) {
        this.imageSmallUrl = imageSmallUrl;
    }

    public void setImageBigUrl(String imageBigUrl) {
        this.imageBigUrl = imageBigUrl;
    }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public void setAudioPath(String audioPath) { this.audioPath = audioPath; }

    public Boolean hasAudioUrl() { return this.audioUrl != null; }
}
