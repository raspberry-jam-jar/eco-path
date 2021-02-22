package com.teplica.ecopath.ui.image;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.teplica.ecopath.R;
import com.teplica.ecopath.di.Injectable;
import com.teplica.ecopath.ui.common.DetectConnection;

import javax.inject.Inject;

public class GalleryFragment extends Fragment implements Injectable {
    private GalleryAdapter adapter;
    private ImageViewModel imageViewModel;
    ViewPager viewPager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.gallery_fragment, container, false);
        viewPager = rootView.findViewById(R.id.gallery_view_pager);

        adapter = new GalleryAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageViewModel = ViewModelProviders
                .of(requireActivity(), viewModelFactory)
                .get(ImageViewModel.class);

        adapter.setImagesListCount(imageViewModel.getCount());
        viewPager.setCurrentItem(imageViewModel.getStartPosition(), false);
    }
}
