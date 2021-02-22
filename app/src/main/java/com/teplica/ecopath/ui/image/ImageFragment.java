package com.teplica.ecopath.ui.image;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.teplica.ecopath.R;
import com.teplica.ecopath.binding.FragmentDataBindingComponent;
import com.teplica.ecopath.databinding.ImageFragmentBinding;
import com.teplica.ecopath.di.Injectable;
import com.teplica.ecopath.ui.common.DetectConnection;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class ImageFragment  extends Fragment implements Injectable {
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    ImageFragmentBinding binding;

    private ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ImageViewModel imageViewModel;

    public static ImageFragment newInstance(int page) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(getActivity());
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.image_fragment, container, false,
                        dataBindingComponent);

        View rootView = binding.getRoot();

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        return  rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageViewModel = ViewModelProviders
                .of(requireActivity(), viewModelFactory)
                .get(ImageViewModel.class);

        int pageNumber = getArguments() != null ? getArguments().getInt("page") : 0;

        if (!DetectConnection.checkInternetConnection(requireActivity())) {
            imageViewModel.loadFromDb().observe(getViewLifecycleOwner(), images -> {
                progressBar.setVisibility(View.GONE);
                if (images != null && images.size() > pageNumber) {
                    binding.setImage(images.get(pageNumber));
                }
            });
        } else {
            imageViewModel.getAllImages().observe(getViewLifecycleOwner(), resource -> {
                if (resource.status.name().equals("SUCCESS")) {
                    progressBar.setVisibility(View.GONE);
                    if (resource.data != null && resource.data.size() > pageNumber) {
                        binding.setImage(resource.data.get(pageNumber));
                    }
                }
            });
        }
    }
}
