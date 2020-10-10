package com.example.ecopath.ui.category;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.ecopath.R;
import com.example.ecopath.binding.FragmentDataBindingComponent;
import com.example.ecopath.databinding.CategoryFragmentBinding;
import com.example.ecopath.di.Injectable;
import com.example.ecopath.ui.image.ImageClickCallback;
import com.example.ecopath.ui.image.ImageFragment;
import com.example.ecopath.ui.image.ImageViewModel;
import com.example.ecopath.ui.image.ImagesListAdapter;
import com.example.ecopath.vo.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class CategoryFragment extends Fragment implements Injectable, Runnable {
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    CategoryFragmentBinding binding;
    ImagesListAdapter adapter;

    MediaPlayer mediaPlayer = new MediaPlayer();
    SeekBar seekBar;
    boolean wasPlaying = false;
    FloatingActionButton fab;
    TextView seekBarHint;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CategoryViewModel categoryViewModel;
    private ImageViewModel imageViewModel;

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(getActivity());
        super.onAttach(activity);
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

        fab = (FloatingActionButton) viewRoot.findViewById(R.id.fab);
        seekBar = (SeekBar) viewRoot.findViewById(R.id.seekbar);
        seekBarHint = (TextView)  viewRoot.findViewById(R.id.seekBarHint);

        return  binding.getRoot();
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
            imageViewModel.getAllImages().observe(getViewLifecycleOwner(), resource -> {
                if (!resource.status.name().equals("LOADING")) {
                    adapter.setImagesList(resource.data);
                }
            });

        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10)
                    seekBarHint.setText("0:0" + x);
                else
                    seekBarHint.setText("0:" + x);

                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                int labelWidth = seekBarHint.getWidth();
                seekBarHint.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth / 2));

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
                seekBar.setProgress(0);
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

                mediaPlayer.setDataSource("" +
                        binding.getCategoryWithImages().category.getAudioUrl());

                mediaPlayer.prepare();
                mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
                new Thread(this).start();

            }

            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();

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
            imageViewModel.select(image);

            ImageFragment imageFragment = new ImageFragment();

            FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.container, imageFragment)
                    .commitAllowingStateLoss();
        }
    };
}
