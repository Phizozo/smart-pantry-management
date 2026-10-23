package com.example.smartpantry.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.models.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying recipes in the Suggested Recipes screen.
 *
 * Used for both the strict-match list and the "Almost There" list — the same
 * adapter class serves both RecyclerViews, with the hosting Activity deciding
 * which recipe set to load.
 *
 * @author Smart Pantry Manager
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    /** Callback fired when the user taps a recipe card. */
    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    private List<Recipe> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public RecipeAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Recipe> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = items.get(position);
        holder.tvName.setText(recipe.getName());
        int count = recipe.getIngredients().size();
        holder.tvMeta.setText(count + (count == 1 ? " ingredient" : " ingredients"));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(recipe));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMeta;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvMeta = itemView.findViewById(R.id.tvRecipeMeta);
        }
    }
}
