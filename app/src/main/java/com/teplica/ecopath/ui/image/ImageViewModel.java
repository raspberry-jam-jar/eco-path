package com.teplica.ecopath.ui.image;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.teplica.ecopath.repository.ImageRepository;
import com.teplica.ecopath.util.AbsentLiveData;
import com.teplica.ecopath.vo.Image;
import com.teplica.ecopath.vo.Resource;

import java.util.List;

import javax.inject.Inject;

public class ImageViewModel extends ViewModel {
    private LiveData<Resource<List<Image>>> allImages;
    private final MutableLiveData<String> categoryId = new MutableLiveData<>();
    private final MutableLiveData<Image> selectedImage = new MutableLiveData<>();
    private final ImageRepository imageRepository;

    @Inject
    ImageViewModel(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
        allImages = Transformations.switchMap(categoryId, categoryId -> {
            if (categoryId == null || categoryId.trim().length() == 0) {
                return AbsentLiveData.create();
            } else {
                return imageRepository.getAllImages(Integer.valueOf(categoryId));
            }
        });
    }

    public LiveData<List<Image>> loadFromDb() {
        return this.imageRepository.loadFromDb(Integer.valueOf(this.categoryId.getValue()));
    }

    public void setCategoryId(@NonNull String originalCategoryId) {
        categoryId.setValue(originalCategoryId);
    }

    public LiveData<Resource<List<Image>>> getAllImages() { return allImages; }

    public void select(Image image) { selectedImage.setValue(image); }

    public  LiveData<Image> getSelected() { return selectedImage; }
}
