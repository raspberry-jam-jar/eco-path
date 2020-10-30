package com.example.ecopath.ui.image;

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

import com.example.ecopath.R;
import com.example.ecopath.binding.FragmentDataBindingComponent;
import com.example.ecopath.databinding.ImageFragmentBinding;
import com.example.ecopath.di.Injectable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class ImageFragment  extends Fragment implements Injectable {
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    ImageFragmentBinding binding;

    private ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ImageViewModel imageViewModel;

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

        imageViewModel.getSelected().observe(getViewLifecycleOwner(), image -> {
            progressBar.setVisibility(View.GONE);
            binding.setImage(image);
        });
    }
}
