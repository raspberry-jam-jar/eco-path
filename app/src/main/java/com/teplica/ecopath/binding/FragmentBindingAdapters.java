package com.teplica.ecopath.binding;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.teplica.ecopath.BuildConfig;

import java.io.File;

import javax.inject.Inject;

import static android.text.Html.fromHtml;

public class FragmentBindingAdapters {
    final Fragment fragment;

    @Inject
    public FragmentBindingAdapters(Fragment fragment) { this.fragment = fragment; }

    @BindingAdapter(value={"app:imageSrc", "app:imageSize"})
    public void bindLocalImage(ImageView imageView, BaseModel instance, String imageSize) {
        if (instance == null) {
            return;
        }

        String path = instance.getPath(imageSize);
        File imgFile = new File(path);

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(myBitmap);
        } else {
            String imageUrl = imageSize.equals("small") ? instance.getImageSmallUrl() : instance.getImageBigUrl();

            if (imageUrl != null) {
                Glide.with(fragment)
                        .load(BuildConfig.SERVER_URL + imageUrl)
                        .placeholder(new ColorDrawable(Color.LTGRAY))
                        .into(imageView);
            } else {
                imageView.setImageDrawable(new ColorDrawable(Color.LTGRAY));
            }
        }
    }

    @BindingAdapter("htmlText")
    public void bindHtmlText(TextView textView, String htmlText) {
        textView.setText(fromHtml(htmlText));
    }
}
