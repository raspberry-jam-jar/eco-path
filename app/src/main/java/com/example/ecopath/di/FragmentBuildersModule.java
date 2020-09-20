package com.example.ecopath.di;

import com.example.ecopath.ui.category.CategoriesListFragment;
import com.example.ecopath.ui.map.MapFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract MapFragment contributeMapFragment();

    @ContributesAndroidInjector
    abstract CategoriesListFragment contributeCategoriesListFragment();
}
