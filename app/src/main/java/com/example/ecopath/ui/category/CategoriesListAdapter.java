package com.example.ecopath.ui.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecopath.R;
import com.example.ecopath.databinding.CategoryItemBinding;
import com.example.ecopath.vo.CategoryWithImages;

import java.util.List;

public class CategoriesListAdapter extends
        RecyclerView.Adapter<CategoriesListAdapter.CategoryItemViewHolder> {

    private final DataBindingComponent dataBindingComponent;
    private List<CategoryWithImages> categoryWithImagesList;
    private CategoryClickCallback categoryClickCallback;

    public CategoriesListAdapter(DataBindingComponent dataBindingComponent) {
        this.dataBindingComponent = dataBindingComponent;
    }

    @NonNull
    @Override
    public CategoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.category_item,
                        parent, false, dataBindingComponent);
        binding.setCategoryClickCallback(categoryClickCallback);
        return new CategoryItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItemViewHolder holder, int position) {
        holder.binding.setCategoryWithImages(categoryWithImagesList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (categoryWithImagesList != null)
            return categoryWithImagesList.size();
        else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setCategoriesList(List<CategoryWithImages> categoriesList) {
        this.categoryWithImagesList = categoriesList;
        notifyDataSetChanged();
    }

    public void setCallback(@Nullable CategoryClickCallback categoryClickCallback) {
        this.categoryClickCallback = categoryClickCallback;
        setHasStableIds(true);
    }

    static class CategoryItemViewHolder extends RecyclerView.ViewHolder {
        final CategoryItemBinding binding;

        public CategoryItemViewHolder(CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
