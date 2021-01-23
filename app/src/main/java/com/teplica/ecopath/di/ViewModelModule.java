package com.teplica.ecopath.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.teplica.ecopath.ui.category.CategoryViewModel;
import com.teplica.ecopath.ui.image.ImageViewModel;
import com.teplica.ecopath.ui.map.MapPointViewModel;
import com.teplica.ecopath.viewmodel.EcoPathViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MapPointViewModel.class)
    abstract ViewModel bindMapPointViewModel(MapPointViewModel mapPointViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel.class)
    abstract ViewModel bindCategoryViewModel(CategoryViewModel categoryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ImageViewModel.class)
    abstract ViewModel bindImageViewModel(ImageViewModel imageViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(EcoPathViewModelFactory factory);
}
