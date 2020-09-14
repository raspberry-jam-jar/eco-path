package com.example.ecopath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.ecopath.vo.MapPoint;
import com.example.ecopath.ui.map.MapPointViewModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Inject
    MapPointViewModel mapPointViewModel;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_maps);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.main_toolbar_title);
        myToolbar.setSubtitle(R.string.main_toolbar_subtitle);
        setSupportActionBar(myToolbar);

//         Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
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
            }

        });

//        categoryViewModel.setMapPointId("1");
//        categoryViewModel.getAllCategories().observe( this, categories -> {
//            if (!categories.status.name().equals("LOADING")) {
//                new Toast(this);
//            }
//        });
    }
}
