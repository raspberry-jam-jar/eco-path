package com.example.ecopath.ui.category;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ecopath.R;
import com.example.ecopath.binding.FragmentDataBindingComponent;
import com.example.ecopath.databinding.CategoriesListFragmentBinding;
import com.example.ecopath.di.Injectable;
import com.example.ecopath.vo.CategoryWithImages;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CategoriesListFragment extends Fragment implements Injectable {
    private Toolbar appToolbar;
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    CategoriesListFragmentBinding binding;
    CategoriesListAdapter adapter;
    String mainCategoryName;

    Activity activity;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CategoryViewModel categoryViewModel;

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(getActivity());
        super.onAttach(activity);

        this.activity = activity;
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

        setUpToolbar();

        return viewRoot;
    }

    private void setUpToolbar() {
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        AppCompatActivity mainActivity = ((AppCompatActivity) getActivity());
        mainActivity.setSupportActionBar(appToolbar);

        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.mapFragment, R.id.categoriesListFragment, R.id.categoryFragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupActionBarWithNavController(
                mainActivity, navController, appBarConfiguration
        );
        NavigationUI.setupWithNavController(navigationView,navController);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.categoryFragment, bundle);
        }
    };
}
