package com.teplica.ecopath.ui.map;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.teplica.ecopath.R;
import com.teplica.ecopath.binding.FragmentDataBindingComponent;
import com.teplica.ecopath.databinding.DownloadsFragmentBinding;
import com.teplica.ecopath.di.Injectable;
import com.teplica.ecopath.ui.common.DetectConnection;
import com.teplica.ecopath.vo.MapPoint;
import com.google.android.material.navigation.NavigationView;

import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static androidx.work.WorkInfo.State.SUCCEEDED;

public class MapPointDownloadsFragment extends Fragment implements Injectable {
    Activity activity;
    private Toolbar appToolbar;

    DownloadsListAdapter downloadedAdapter;
    DownloadsListAdapter toBeDownloadedAdapter;
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    DownloadsFragmentBinding binding;

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
        binding = DataBindingUtil
                .inflate(inflater, R.layout.downloads_fragment, container, false,
                        dataBindingComponent);

        downloadedAdapter = new DownloadsListAdapter(dataBindingComponent);
        downloadedAdapter.setCallback(downloadCallback);
        toBeDownloadedAdapter = new DownloadsListAdapter(dataBindingComponent);
        toBeDownloadedAdapter.setCallback(downloadCallback);

        binding.loadedList.setAdapter(downloadedAdapter);
        binding.toBeLoadedList.setAdapter(toBeDownloadedAdapter);

        View viewRoot = binding.getRoot();
        appToolbar = (Toolbar) viewRoot.findViewById(R.id.app_toolbar);
        appToolbar.setTitle(R.string.downloads_toolbar_title);
        appToolbar.setSubtitle("");

        setUpToolbar();

        return viewRoot;
    }

    private void setUpToolbar() {
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        AppCompatActivity mainActivity = ((AppCompatActivity) getActivity());
        mainActivity.setSupportActionBar(appToolbar);

        DrawerLayout drawer = activity.findViewById(R.id.drawer_layout);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration
                .Builder(R.id.mapFragment, R.id.categoriesListFragment, R.id.categoryFragment, R.id.mapPointDownloadsFragment)
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

        if (!DetectConnection.checkInternetConnection(requireActivity())) {
            Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        mapPointViewModel = ViewModelProviders
                .of(requireActivity(), viewModelFactory)
                .get(MapPointViewModel.class);
        mapPointViewModel.getLoadedMapPoints().observe(getViewLifecycleOwner(), mapPointList -> {
            downloadedAdapter.setMapPointsList(mapPointList);
            binding.setLoadedSize(mapPointList.size());
        });
        mapPointViewModel.getToBeLoadedMapPoint().observe(getViewLifecycleOwner(), mapPointList -> {
            toBeDownloadedAdapter.setMapPointsList(mapPointList);
            binding.setToBeLoadedSize(mapPointList.size());
        });
    }

    private final DownloadCallback downloadCallback = new DownloadCallback() {
        @Override
        public void onClick(MapPoint mapPoint) {
            if (mapPoint.getIsLoaded()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.delete_dialog_message) + "«" + mapPoint.getName() + "»?");

                builder.setPositiveButton(R.string.delete_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UUID workerId = mapPointViewModel.deleteMapPoint(mapPoint);

                        mapPointViewModel.getOutputWorkInfo(workerId).observe(getViewLifecycleOwner(), workInfo -> {
                            if (workInfo != null && !workInfo.getState().isFinished()) {
                                System.out.println("deleting");
                            } else if (workInfo != null && workInfo.getState() == SUCCEEDED) {
                                mapPoint.setIsLoaded(false);
                                mapPoint.setIsLoading(false);
                                mapPointViewModel.updateIsLoaded(mapPoint);
                                Toast.makeText(getContext(), "Удалено!", Toast.LENGTH_SHORT).show();
                            } else {
                                mapPoint.setIsLoading(false);
                                mapPointViewModel.updateIsLoaded(mapPoint);
                                Toast.makeText(getContext(), "Не получилось удалить...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {

                UUID workerId = mapPointViewModel.downloadMapPoint(mapPoint);

                mapPointViewModel.getOutputWorkInfo(workerId).observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null && !workInfo.getState().isFinished()) {
                        System.out.println("loading");
                    } else if (workInfo != null && workInfo.getState() == SUCCEEDED) {
                        mapPoint.setIsLoaded(true);
                        mapPoint.setIsLoading(false);
                        mapPointViewModel.updateIsLoaded(mapPoint);
                        Toast.makeText(getContext(), "Загружено!", Toast.LENGTH_SHORT).show();
                    } else {
                        mapPoint.setIsLoading(false);
                        mapPointViewModel.updateIsLoaded(mapPoint);
                        Toast.makeText(getContext(), "Не получилось загрузить...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}
