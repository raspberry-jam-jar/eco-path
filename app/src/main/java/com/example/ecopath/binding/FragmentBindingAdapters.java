package com.example.ecopath.binding;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.ecopath.BuildConfig;

import java.io.File;

import javax.inject.Inject;

import static android.text.Html.fromHtml;

public class FragmentBindingAdapters {
    final Fragment fragment;

    @Inject
    public FragmentBindingAdapters(Fragment fragment) { this.fragment = fragment; }

    @BindingAdapter("imageSrc")
    public void bindLocalImage(ImageView imageView, BaseModel instance) {
        File imgFile = new File(instance.getPath());

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(instance.getPath());
            imageView.setImageBitmap(myBitmap);

        } else if (instance.getImageSmallUrl() != null) {
            System.out.println("Image is not loaded");
            Glide.with(fragment)
                .load(BuildConfig.SERVER_URL + instance.getImageSmallUrl())
                .placeholder(new ColorDrawable(Color.LTGRAY))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Remove the Drawable provided in onResourceReady from any Views and ensure
                        // no references to it remain.
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        imageView.setImageDrawable(new ColorDrawable(Color.LTGRAY));
                    }
                });

        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.LTGRAY));
        }
    }

    @BindingAdapter("htmlText")
    public void bindHtmlText(TextView textView, String htmlText) {
        textView.setText(fromHtml(htmlText));
    }
}
