package com.teplica.ecopath.ui.image;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class GalleryAdapter extends FragmentStatePagerAdapter {
    private int imagesListCount;

    public GalleryAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return this.imagesListCount;
    }

    public void setImagesListCount(int count) {
        this.imagesListCount = count;
        notifyDataSetChanged();
    }
}
