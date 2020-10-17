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

import com.google.android.material.navigation.NavigationView;

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
}
