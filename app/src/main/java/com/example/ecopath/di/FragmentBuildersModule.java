package com.example.ecopath.di;

import com.example.ecopath.ui.category.CategoriesListFragment;
import com.example.ecopath.ui.category.CategoryFragment;
import com.example.ecopath.ui.image.ImageFragment;
import com.example.ecopath.ui.map.MapFragment;
import com.example.ecopath.ui.map.MapPointDownloadsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract MapFragment contributeMapFragment();

    @ContributesAndroidInjector
    abstract CategoriesListFragment contributeCategoriesListFragment();

    @ContributesAndroidInjector
    abstract CategoryFragment contributeCategoryFragment();

    @ContributesAndroidInjector
    abstract ImageFragment contributeImageFragment();

    @ContributesAndroidInjector
    abstract MapPointDownloadsFragment contributeMapPointDownloadsFragment();
}
