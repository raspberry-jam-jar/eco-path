package com.teplica.ecopath.ui.category;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import androidx.recyclerview.widget.GridLayoutManager;

import com.teplica.ecopath.R;
import com.teplica.ecopath.binding.FragmentDataBindingComponent;
import com.teplica.ecopath.databinding.CategoriesListFragmentBinding;
import com.teplica.ecopath.di.Injectable;
import com.teplica.ecopath.ui.common.DetectConnection;
import com.teplica.ecopath.vo.CategoryWithImages;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CategoriesListFragment extends Fragment implements Injectable {
    private Toolbar appToolbar;
    private ProgressBar progressBar;
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

        progressBar = (ProgressBar) viewRoot.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

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

        if (!DetectConnection.checkInternetConnection(requireActivity())) {
            categoryViewModel.loadFromDb().observe(getViewLifecycleOwner(), categories -> {
                if (categories !=null && !categories.isEmpty()) {
                    adapter.setCategoriesList(categories);
                    setSpanCount(categories.size());
                } else {
                    Toast.makeText(
                        getContext(), R.string.no_connection, Toast.LENGTH_SHORT
                    ).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        } else {
            categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), resource -> {
                if (!resource.status.name().equals("LOADING")) {
                    progressBar.setVisibility(View.GONE);
                    adapter.setCategoriesList(resource.data);
                    setSpanCount(resource.data.size());
                } else if (resource.status.name().equals("ERROR")) {
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setSpanCount(int itemsCount){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int spanCount = (int) Math.floor(
                (width-getResources().getDimension(R.dimen.categories_list_padding)*2) /
                        (getResources().getDimension(R.dimen.category_card_width)+
                                getResources().getDimension(R.dimen.category_card_margin)*2)
        );

        if (spanCount > itemsCount) {
            spanCount = itemsCount;
        } else if (spanCount == 0) {
            spanCount = 1;
        }

        ((GridLayoutManager) binding.categoriesList.getLayoutManager()).setSpanCount(spanCount);
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
