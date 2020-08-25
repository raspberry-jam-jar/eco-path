package com.example.ecopath.di;

import com.example.ecopath.MapsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MapsActivityModule {
    @ContributesAndroidInjector()
    abstract MapsActivity contributeMapsActivity();
}
