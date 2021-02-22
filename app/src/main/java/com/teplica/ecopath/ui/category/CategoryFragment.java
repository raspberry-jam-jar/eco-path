package com.teplica.ecopath.ui.category;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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

import com.teplica.ecopath.BuildConfig;
import com.teplica.ecopath.R;
import com.teplica.ecopath.binding.FragmentDataBindingComponent;
import com.teplica.ecopath.databinding.CategoryFragmentBinding;
import com.teplica.ecopath.di.Injectable;
import com.teplica.ecopath.ui.common.DetectConnection;
import com.teplica.ecopath.ui.image.ImageClickCallback;
import com.teplica.ecopath.ui.image.ImageViewModel;
import com.teplica.ecopath.ui.image.ImagesListAdapter;
import com.teplica.ecopath.vo.Image;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CategoryFragment extends Fragment implements Injectable, Runnable {
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    CategoryFragmentBinding binding;
    ImagesListAdapter adapter;

    MediaPlayer mediaPlayer = new MediaPlayer();
    SeekBar seekBar;
    boolean wasPlaying = false;
    ImageButton fab;
    TextView seekBarHint;

    Activity activity;
    private Toolbar appToolbar;
    private ProgressBar progressBar;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CategoryViewModel categoryViewModel;
    private ImageViewModel imageViewModel;

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
                .inflate(inflater, R.layout.category_fragment, container, false,
                        dataBindingComponent);

        View viewRoot = binding.getRoot();
        adapter = new ImagesListAdapter(dataBindingComponent);
        adapter.setCallback(imageClickCallback);
        binding.imagesList.setAdapter(adapter);

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);

        binding.imagesList.setLayoutManager(layoutManager);

        fab = (ImageButton) viewRoot.findViewById(R.id.fab);
        seekBar = (SeekBar) viewRoot.findViewById(R.id.seekbar);
        seekBarHint = (TextView)  viewRoot.findViewById(R.id.seekBarHint);

        appToolbar = (Toolbar) viewRoot.findViewById(R.id.toolbar);

        setUpToolbar();

        progressBar = (ProgressBar) viewRoot.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        return  binding.getRoot();
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
        imageViewModel = ViewModelProviders
                .of(requireActivity(), viewModelFactory)
                .get(ImageViewModel.class);

        categoryViewModel.getSelected().observe(getViewLifecycleOwner(), category -> {
            assert getArguments() != null;

            binding.setCategoryWithImages(category);
            binding.setMainCategoryName(getArguments().getString("main_category_name"));

            Integer categoryId = category.category.getId();
            imageViewModel.setCategoryId(String.valueOf(categoryId));

            if (!DetectConnection.checkInternetConnection(requireActivity())) {
                progressBar.setVisibility(View.GONE);
                imageViewModel.loadFromDb().observe(getViewLifecycleOwner(), images -> {
                    adapter.setImagesList(images);

                    Boolean categoryHasNotLoadedAudio =
                            category.category.getAudioUrl() != null &&
                                    category.category.getAudioPath() == null;
                    Boolean categoryHasNotLoadedGalleryImages = images == null || images.isEmpty();

                    if (categoryHasNotLoadedGalleryImages || categoryHasNotLoadedAudio) {
                        Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                imageViewModel.getAllImages().observe(getViewLifecycleOwner(), resource -> {
                    if (!resource.status.name().equals("LOADING")) {
                        progressBar.setVisibility(View.GONE);
                        adapter.setImagesList(resource.data);
                    } else if (resource.status.name().equals("ERROR")) {
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { playSong(); }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int time = (int) Math.ceil(progress / 1000f);

                int minutes = time / 60;
                int seconds = time % 60;

                String secondsText;
                String minutesText;

                if (seconds < 10)
                    secondsText = "0" + seconds;
                else
                    secondsText = "" + seconds;

                if (minutes < 10)
                    minutesText = "0" + minutes;
                else
                    minutesText = "" + minutes;

                seekBarHint.setText(minutesText + ":" + secondsText);

                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    fab.setImageDrawable(
                            ContextCompat
                                    .getDrawable(getActivity(), android.R.drawable.ic_media_play)
                    );
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    public void playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                wasPlaying = true;
                fab.setImageDrawable(
                        ContextCompat.getDrawable(getContext(), android.R.drawable.ic_media_play)
                );
            }

            if (!wasPlaying) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                fab.setImageDrawable(ContextCompat
                        .getDrawable(getContext(), android.R.drawable.ic_media_pause)
                );

                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );

                if (!DetectConnection.checkInternetConnection(requireActivity())) {
                    if (binding.getCategoryWithImages().category.getAudioPath() == null) {
                        Toast.makeText(
                                getContext(), R.string.no_connection, Toast.LENGTH_SHORT
                        ).show();
                        clearMediaPlayer();
                        wasPlaying = true;
                        fab.setImageDrawable(
                            ContextCompat
                                .getDrawable(getContext(), android.R.drawable.ic_media_play)
                        );
                    } else {
                        mediaPlayer.setDataSource(
                                getContext(),
                                Uri.parse(binding.getCategoryWithImages().category.getAudioPath())
                        );
                    }
                } else {
                    mediaPlayer.setDataSource(BuildConfig.SERVER_URL +
                            binding.getCategoryWithImages().category.getAudioUrl());
                }

                mediaPlayer.prepare();
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();

                if (seekBar.getProgress() > 0) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }

                new Thread(this).start();
            }
            wasPlaying = false;
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void run() {

        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();

        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition);

        }
    }

    private void clearMediaPlayer() {
        if (mediaPlayer != null ) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        clearMediaPlayer();
    }

    private final ImageClickCallback imageClickCallback = new ImageClickCallback() {
        @Override
        public void onClick(Image image) {
            imageViewModel.setStartPosition(adapter.getImagePosition(image));
            imageViewModel.setCount(adapter.getItemCount());

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.galleryFragment);
        }
    };
}
