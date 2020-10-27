package com.example.ecopath.binding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.ecopath.BuildConfig;

import javax.inject.Inject;

import static android.text.Html.fromHtml;

public class FragmentBindingAdapters {
    final Fragment fragment;

    @Inject
    public FragmentBindingAdapters(Fragment fragment) { this.fragment = fragment; }

    @BindingAdapter("imageUrl")
    public void bindImage(ImageView imageView, String url) {
        if (url != null){
            Glide.with(fragment)
                    .load(BuildConfig.SERVER_URL + url)
                    .placeholder(new ColorDrawable(Color.LTGRAY))
                    .into(imageView);
        } else {
            imageView.setBackgroundColor(Color.LTGRAY);
            System.out.println("Empty url attribute");
        }
    }

    @BindingAdapter("htmlText")
    public void bindHtmlText(TextView textView, String htmlText) {
        textView.setText(fromHtml(htmlText));
    }
}
