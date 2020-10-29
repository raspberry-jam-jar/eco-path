package com.example.ecopath.ui.info;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ecopath.R;
import com.example.ecopath.ui.common.DetectConnection;
import com.google.android.material.navigation.NavigationView;

public class InfoFragment extends Fragment {
    Activity activity;
    private Toolbar appToolbar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_view_fragment, container, false);

        String title;
        String url;

        try{
            title = getArguments().getString("title");
            url = getArguments().getString("url");
        } catch (NullPointerException e) {
            title = getString(R.string.about_project);
            url = getString(R.string.about_project_url);
        }

        appToolbar = (Toolbar) rootView.findViewById(R.id.app_toolbar);
        appToolbar.setTitle(title);
        appToolbar.setSubtitle("");

        setUpToolbar();

        if (!DetectConnection.checkInternetConnection(requireActivity())) {
            Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        } else {
            WebView myWebView = (WebView) rootView.findViewById(R.id.web_view);
            myWebView.loadUrl(url);
        }

        return rootView;
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
}
