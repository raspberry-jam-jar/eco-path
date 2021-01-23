package com.teplica.ecopath.ui.category;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.teplica.ecopath.repository.CategoryRepository;
import com.teplica.ecopath.util.AbsentLiveData;
import com.teplica.ecopath.vo.CategoryWithImages;
import com.teplica.ecopath.vo.Resource;

import java.util.List;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {
    CategoryRepository categoryRepository;
    private final MutableLiveData<String> mapPointId = new MutableLiveData<>();
    private LiveData<Resource<List<CategoryWithImages>>> allCategories;
    private final MutableLiveData<CategoryWithImages> selectedCategory = new MutableLiveData<>();

    @Inject
    CategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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

    public LiveData<List<CategoryWithImages>> loadFromDb() {
        return this.categoryRepository.loadFromDb(Integer.valueOf(this.mapPointId.getValue()));
    }
}
