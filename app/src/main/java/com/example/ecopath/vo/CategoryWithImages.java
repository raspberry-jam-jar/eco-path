package com.example.ecopath.vo;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CategoryWithImages {
    @Embedded
    public Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "categoryId",
            entity = Image.class
    )
    public List<Image> imagesList;

    public void setCategory(Category category) { this.category = category; }
    public void setImagesList(List<Image> imagesList) { this.imagesList = imagesList; }
}
