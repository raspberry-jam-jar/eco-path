package com.teplica.ecopath.ui.image;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.teplica.ecopath.R;
import com.teplica.ecopath.databinding.ImageItemBinding;
import com.teplica.ecopath.vo.Image;

import java.util.List;

public class ImagesListAdapter extends RecyclerView.Adapter<ImagesListAdapter.ImageItemViewHolder> {
    private final DataBindingComponent dataBindingComponent;
    private List<Image> imagesList;
    private ImageClickCallback imageClickCallback;

    public ImagesListAdapter(DataBindingComponent dataBindingComponent) {
        this.dataBindingComponent = dataBindingComponent;
    }

    @NonNull
    @Override
    public ImagesListAdapter.ImageItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.image_item,
                        parent, false, dataBindingComponent);
        binding.setImageClickCallback(imageClickCallback);
        return new ImagesListAdapter.ImageItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesListAdapter.ImageItemViewHolder holder, int position) {
        holder.binding.setImage(imagesList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (imagesList != null)
            return imagesList.size();
        else return 0;
    }

    public int getImagePosition(Image image) {
        return this.imagesList.indexOf(image);
    }

    public void setImagesList(List<Image> imagesList) {
        this.imagesList = imagesList;
        notifyDataSetChanged();
    }
    public void setCallback(@Nullable ImageClickCallback imageClickCallback) {
        this.imageClickCallback = imageClickCallback;
        setHasStableIds(true);
    }

    static class ImageItemViewHolder extends RecyclerView.ViewHolder {
        final ImageItemBinding binding;

        public ImageItemViewHolder(ImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
