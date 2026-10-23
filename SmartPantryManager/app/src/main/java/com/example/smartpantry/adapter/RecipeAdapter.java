package com.example.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.model.Recipe;

import java.util.List;

/** Binds Recipe records to the Suggested Recipes RecyclerView. */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClicked(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;
    private final boolean showAlmostThereBadge;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener, boolean showAlmostThereBadge) {
        this.recipes = recipes;
        this.listener = listener;
        this.showAlmostThereBadge = showAlmostThereBadge;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        int count = recipe.getIngredients().size();
        holder.name.setText(recipe.getName());
        holder.ingredientCount.setText(holder.itemView.getResources()
                .getQuantityString(R.plurals.ingredient_count, count, count));
        holder.badge.setVisibility(showAlmostThereBadge ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> listener.onRecipeClicked(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name, ingredientCount, badge;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_recipe_name);
            ingredientCount = itemView.findViewById(R.id.text_ingredient_count);
            badge = itemView.findViewById(R.id.text_almost_there_badge);
        }
    }
}
