package com.example.ecopath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class MainActivity extends AppCompatActivity implements HasAndroidInjector,
        NavigationView.OnNavigationItemSelectedListener {
    private AppBarConfiguration appBarConfiguration;
    public NavController navController;
    public DrawerLayout drawer;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.main_activity);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.mapFragment, R.id.categoriesListFragment, R.id.categoryFragment)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawer.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {
            case R.id.mapFragment:
                navController.navigate(R.id.mapFragment);
                break;
            case R.id.infoFragment:
                navController.navigate(R.id.infoFragment);
                break;
        }
        return true;

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    public void openInfoFragment(View view) {
        drawer.closeDrawers();

        String title = new String();
        String url = new String();

        switch (view.getTag().toString()) {
            case "privacy_policy":
                title = getString(R.string.privacy_policy);
                url = getString(R.string.privacy_policy_url);
                break;
            case "customer_agreement":
                title = getString(R.string.customer_agreement);
                url = getString(R.string.customer_agreement_url);
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("url", url);

        Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.infoFragment, bundle);
    }
}
