package com.example.ecopath.ui.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecopath.MainActivity;
import com.example.ecopath.R;
import com.example.ecopath.di.Injectable;
import com.example.ecopath.vo.MapPoint;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MapFragment extends Fragment implements Injectable, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Toolbar appToolbar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    MapPointViewModel mapPointViewModel;

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(getActivity());
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        appToolbar = (Toolbar) rootView.findViewById(R.id.app_toolbar);
        appToolbar.setTitle(R.string.main_toolbar_title);
        appToolbar.setSubtitle(R.string.main_toolbar_subtitle);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(appToolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
                    mapPointMarker.showInfoWindow();
                }
                mMap.setOnInfoWindowClickListener(this);

            }

        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "Info window clicked: " + marker.getTitle(),
                Toast.LENGTH_SHORT).show();
    }
}
