package com.example.ecopath.ui.map;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecopath.R;
import com.example.ecopath.databinding.DownloadItemBinding;
import com.example.ecopath.ui.category.CategoryClickCallback;
import com.example.ecopath.vo.MapPoint;

import java.util.List;

public class DownloadsListAdapter extends
        RecyclerView.Adapter<DownloadsListAdapter.DownloadsItemViewHolder> {

    private final DataBindingComponent dataBindingComponent;
    private List<MapPoint> mapPointsList;
    private DownloadCallback downloadCallback;

    public DownloadsListAdapter(DataBindingComponent dataBindingComponent) {
        this.dataBindingComponent = dataBindingComponent;
    }

    @NonNull
    @Override
    public DownloadsItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.download_item,
                        parent, false, dataBindingComponent);
        binding.setDownloadCallback(downloadCallback);
        return new DownloadsItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadsItemViewHolder holder, int position) {
        holder.binding.setMapPoint(mapPointsList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        if (mapPointsList != null)
            return mapPointsList.size();
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

    public void setMapPointsList(List<MapPoint> mapPointsList) {
        this.mapPointsList = mapPointsList;
        notifyDataSetChanged();
    }

    public void setCallback(@Nullable DownloadCallback downloadCallback) {
        this.downloadCallback = downloadCallback;
        setHasStableIds(true);
    }

    static class DownloadsItemViewHolder extends RecyclerView.ViewHolder {
        final DownloadItemBinding binding;

        public DownloadsItemViewHolder(DownloadItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
