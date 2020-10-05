package com.example.ecopath.ui.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.ecopath.MainActivity;
import com.example.ecopath.R;
import com.example.ecopath.binding.FragmentDataBindingComponent;
import com.example.ecopath.databinding.CategoriesListFragmentBinding;
import com.example.ecopath.di.Injectable;
import com.example.ecopath.vo.CategoryWithImages;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CategoriesListFragment extends Fragment implements Injectable {
    private Toolbar appToolbar;
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    CategoriesListFragmentBinding binding;
    CategoriesListAdapter adapter;
    String mainCategoryName;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CategoryViewModel categoryViewModel;

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(getActivity());
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.categories_list_fragment, container, false,
                        dataBindingComponent);

        adapter = new CategoriesListAdapter(dataBindingComponent);
        adapter.setCallback(categoryClickCallback);
        binding.categoriesList.setAdapter(adapter);

        View viewRoot = binding.getRoot();
        assert getArguments() != null;
        mainCategoryName = getArguments().getString("main_category_name");

        appToolbar = (Toolbar) viewRoot.findViewById(R.id.include_toolbar);
        appToolbar.setTitle(mainCategoryName);
        appToolbar.setSubtitle("");

        return viewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(appToolbar);
        categoryViewModel = ViewModelProviders
                .of(requireActivity(), viewModelFactory)
                .get(CategoryViewModel.class);
        categoryViewModel.setMapPointId(getArguments().getString("map_point_id"));
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), resource -> {
            if (!resource.status.name().equals("LOADING")) {
                adapter.setCategoriesList(resource.data);
            }
        });

    }

    private final CategoryClickCallback categoryClickCallback = new CategoryClickCallback() {
        @Override
        public void onClick(CategoryWithImages category) {
            categoryViewModel.select(category);

            Bundle bundle = new Bundle();
            bundle.putString("main_category_name", mainCategoryName);

            CategoryFragment categoryFragment = new CategoryFragment();
            categoryFragment.setArguments(bundle);

            FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.container, categoryFragment)
                    .commitAllowingStateLoss();
        }
    };
}