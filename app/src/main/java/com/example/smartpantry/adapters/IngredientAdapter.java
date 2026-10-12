package com.example.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter that displays pantry ingredients in a scrollable list.
 *
 * Uses the ViewHolder pattern for efficient recycling of list item views.
 * Click and long-click events are delegated to the hosting Activity through
 * the {@link OnItemClickListener} interface, keeping this adapter reusable.
 *
 * @author Smart Pantry Manager
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    /**
     * Callback interface for user interaction with list items.
     * onItemClick → open the edit screen; onItemLongClick → show delete dialog.
     */
    public interface OnItemClickListener {
        void onItemClick(Ingredient ingredient);
        void onItemLongClick(Ingredient ingredient);
    }

    private List<Ingredient> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public IngredientAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Ingredient> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = items.get(position);
        holder.tvName.setText(ingredient.getName());
        holder.tvQuantity.setText(ingredient.getQuantity() + " " + ingredient.getUnit());
        if (ingredient.getExpiryDate() != null && !ingredient.getExpiryDate().isEmpty()) {
            holder.tvExpiry.setText("Expires: " + ingredient.getExpiryDate());
            holder.tvExpiry.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(ingredient));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(ingredient);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvIngredientQuantity);
            tvExpiry = itemView.findViewById(R.id.tvIngredientExpiry);
        }
    }
}
