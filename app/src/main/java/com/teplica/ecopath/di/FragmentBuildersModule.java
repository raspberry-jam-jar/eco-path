package com.teplica.ecopath.di;

import com.teplica.ecopath.ui.category.CategoriesListFragment;
import com.teplica.ecopath.ui.category.CategoryFragment;
import com.teplica.ecopath.ui.image.ImageFragment;
import com.teplica.ecopath.ui.map.MapFragment;
import com.teplica.ecopath.ui.map.MapPointDownloadsFragment;

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
