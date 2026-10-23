package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.PantryItem;

import java.util.List;
import java.util.Locale;

/** Binds PantryItem records to the pantry RecyclerView (tap = edit, button = delete). */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnItemActionListener {
        void onItemClicked(PantryItem item);
        void onDeleteClicked(PantryItem item);
    }

    private List<PantryItem> items;
    private final OnItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());

        String unit = item.getUnit() != null && !item.getUnit().isEmpty() ? " " + item.getUnit() : "";
        holder.quantity.setText(formatQuantity(item.getQuantity()) + unit);

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText(holder.itemView.getContext()
                    .getString(R.string.label_expires, item.getExpiryDate()));
        } else {
            holder.expiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClicked(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClicked(item));
    }

    private String formatQuantity(double q) {
        return q == Math.floor(q) ? String.format(Locale.getDefault(), "%.0f", q)
                                  : String.format(Locale.getDefault(), "%.2f", q);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        final TextView name, quantity, expiry;
        final ImageButton deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_item_name);
            quantity = itemView.findViewById(R.id.text_item_quantity);
            expiry = itemView.findViewById(R.id.text_item_expiry);
            deleteButton = itemView.findViewById(R.id.button_delete_item);
        }
    }
}
