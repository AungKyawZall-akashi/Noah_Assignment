package com.example.assignment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.assignment.R;
import com.example.assignment.models.PackageItem;
import java.util.List;
import java.util.Locale;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageViewHolder> {

    public interface OnPackageClickListener {
        void onPackageClick(PackageItem item);
    }

    private final List<PackageItem> items;
    private final OnPackageClickListener listener;

    public PackageAdapter(List<PackageItem> items, OnPackageClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_package, parent, false);
        return new PackageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
        PackageItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());
        holder.tvPrice.setText(String.format(Locale.US, "$%.2f", item.getPriceUsd()));
        holder.itemView.setOnClickListener(v -> listener.onPackageClick(item));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDescription;
        TextView tvPrice;

        PackageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPackageName);
            tvDescription = itemView.findViewById(R.id.tvPackageDescription);
            tvPrice = itemView.findViewById(R.id.tvPackagePrice);
        }
    }
}
