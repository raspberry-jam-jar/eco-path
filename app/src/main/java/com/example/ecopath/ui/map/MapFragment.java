package com.example.ecopath.ui.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ecopath.R;
import com.example.ecopath.di.Injectable;
import com.example.ecopath.ui.common.DetectConnection;
import com.example.ecopath.vo.MapPoint;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MapFragment extends Fragment implements Injectable, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    Activity activity;
    private Toolbar appToolbar;
    private ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    MapPointViewModel mapPointViewModel;

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(getActivity());
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        appToolbar = (Toolbar) rootView.findViewById(R.id.app_toolbar);
        appToolbar.setTitle(R.string.main_toolbar_title);
        appToolbar.setSubtitle(R.string.main_toolbar_subtitle);

        setUpToolbar();

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!DetectConnection.checkInternetConnection(requireActivity())) {
            Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setUpToolbar() {
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        AppCompatActivity mainActivity = ((AppCompatActivity) getActivity());
        mainActivity.setSupportActionBar(appToolbar);

        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.mapFragment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = NavHostFragment.findNavController(this);
        NavigationUI.setupActionBarWithNavController(
                mainActivity, navController, appBarConfiguration
        );
        NavigationUI.setupWithNavController(navigationView,navController);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBar.setVisibility(View.GONE);
        mMap = googleMap;

        mapPointViewModel.getAllMapPoints().observe(this, mapPoints -> {
            if (!mapPoints.status.name().equals("LOADING")) {
                for (MapPoint mapPoint : mapPoints.data) {
                    LatLng pointCoordinates = new LatLng(
                            mapPoint.getLatitude(), mapPoint.getLongitude()
                    );
                    Marker mapPointMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(pointCoordinates)
                                    .title(mapPoint.getName().toUpperCase())
                    );
                    mapPointMarker.setTag(mapPoint.getId());
                    mapPointMarker.showInfoWindow();
                }
                mMap.setOnInfoWindowClickListener(this);
            }

        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String categoryName =  marker.getTitle().toLowerCase();
        categoryName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1);

        Bundle bundle = new Bundle();
        bundle.putString("main_category_name", categoryName);
        bundle.putString("map_point_id", marker.getTag().toString());

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.categoriesListFragment, bundle);

    }
}
