package com.example.ecopath.binding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

public class FragmentBindingAdapters {
    final Fragment fragment;

    @Inject
    public FragmentBindingAdapters(Fragment fragment) { this.fragment = fragment; }

    @BindingAdapter("imageUrl")
    public void bindImage(ImageView imageView, String url) {
        if (url != null){
            Glide.with(fragment)
                    .load("https://tropa.tonchik-tm.ru" + url)
                    .placeholder(new ColorDrawable(Color.LTGRAY))
                    .into(imageView);
        }
        imageView.setBackgroundColor(Color.LTGRAY);
        System.out.println("Empty url attribute");
    }
}
