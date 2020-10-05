package com.example.ecopath.ui.category;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.ecopath.repository.CategoryRepository;
import com.example.ecopath.util.AbsentLiveData;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Resource;

import java.util.List;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<String> mapPointId = new MutableLiveData<>();
    private LiveData<Resource<List<CategoryWithImages>>> allCategories;
    private final MutableLiveData<CategoryWithImages> selectedCategory = new MutableLiveData<>();

    @Inject
    CategoryViewModel(CategoryRepository categoryRepository) {
        allCategories = Transformations.switchMap(mapPointId, mapPointId -> {
            if (mapPointId == null || mapPointId.trim().length() == 0) {
                return AbsentLiveData.create();
            } else {
                return categoryRepository.getAllCategories(Integer.valueOf(mapPointId));
            }
        });
    }

    public void setMapPointId(@NonNull String originalMapPointId) {
        mapPointId.setValue(originalMapPointId);
    }

    public LiveData<Resource<List<CategoryWithImages>>> getAllCategories() { return allCategories; }

    public void select(CategoryWithImages category) { selectedCategory.setValue(category); }

    public  LiveData<CategoryWithImages> getSelected() { return selectedCategory; }
}
