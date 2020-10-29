package com.example.ecopath.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Category {
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
    // TODO create index
    @NonNull
    private Integer pointId;

    private String audioUrl;

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
    public String getImageSmallUrl() { return imageSmallUrl; }
    public String getImageBigUrl() { return imageBigUrl; }
    public String getAudioUrl() { return audioUrl; }

    public void setImageSmallUrl(String imageSmallUrl) {
        this.imageSmallUrl = imageSmallUrl;
    }

    public void setImageBigUrl(String imageBigUrl) {
        this.imageBigUrl = imageBigUrl;
    }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public Boolean hasAudioUrl() { return this.audioUrl != null; }
}
