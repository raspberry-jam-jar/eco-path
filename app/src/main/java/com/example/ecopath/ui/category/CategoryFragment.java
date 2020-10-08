package com.example.ecopath.ui.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.ecopath.R;
import com.example.ecopath.binding.FragmentDataBindingComponent;
import com.example.ecopath.databinding.CategoryFragmentBinding;
import com.example.ecopath.di.Injectable;
import com.example.ecopath.ui.image.ImageClickCallback;
import com.example.ecopath.ui.image.ImageFragment;
import com.example.ecopath.ui.image.ImageViewModel;
import com.example.ecopath.ui.image.ImagesListAdapter;
import com.example.ecopath.vo.CategoryWithImages;
import com.example.ecopath.vo.Image;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CategoryFragment extends Fragment implements Injectable {
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    CategoryFragmentBinding binding;
    ImagesListAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CategoryViewModel categoryViewModel;
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
                .inflate(inflater, R.layout.category_fragment, container, false,
                        dataBindingComponent);

        adapter = new ImagesListAdapter(dataBindingComponent);
        adapter.setCallback(imageClickCallback);
        binding.imagesList.setAdapter(adapter);

        return  binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        categoryViewModel = ViewModelProviders
                .of(requireActivity(), viewModelFactory)
                .get(CategoryViewModel.class);
        imageViewModel = ViewModelProviders
                .of(requireActivity(), viewModelFactory)
                .get(ImageViewModel.class);

        categoryViewModel.getSelected().observe(getViewLifecycleOwner(), category -> {
            assert getArguments() != null;

            binding.setCategoryWithImages(category);
            binding.setMainCategoryName(getArguments().getString("main_category_name"));

            Integer categoryId = category.category.getId();
            imageViewModel.setCategoryId(String.valueOf(categoryId));
            imageViewModel.getAllImages().observe(getViewLifecycleOwner(), resource -> {
                if (!resource.status.name().equals("LOADING")) {
                    adapter.setImagesList(resource.data);
                }
            });

        });

    }
    private final ImageClickCallback imageClickCallback = new ImageClickCallback() {
        @Override
        public void onClick(Image image) {
            imageViewModel.select(image);

            ImageFragment imageFragment = new ImageFragment();

            FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.container, imageFragment)
                    .commitAllowingStateLoss();
        }
    };
}
