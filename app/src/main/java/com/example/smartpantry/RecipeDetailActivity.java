package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.db.PantryRepository;
import com.example.smartpantry.models.Recipe;
import com.example.smartpantry.models.RecipeIngredient;

/**
 * Displays the full details of a single recipe.
 *
 * The recipe ID is received via an Intent extra; the recipe (including its
 * ingredient list) is loaded from SQLite in {@code onCreate()}. The screen
 * shows the recipe name, a bulleted ingredient list with quantities and
 * units, and the step-by-step preparation method.
 *
 * @author Smart Pantry Manager
 */
public class RecipeDetailActivity extends AppCompatActivity {

    /** Extra key carrying the integer recipe ID from the calling Activity. */
    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        PantryRepository repository = new PantryRepository(this);
        Recipe recipe = repository.getRecipeById(recipeId);

        if (recipe == null) {
            finish();
            return;
        }

        setTitle(recipe.getName());

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvIngredients = findViewById(R.id.tvDetailIngredients);
        TextView tvSteps = findViewById(R.id.tvDetailSteps);

        tvName.setText(recipe.getName());

        StringBuilder ingText = new StringBuilder();
        for (RecipeIngredient ri : recipe.getIngredients()) {
            ingText.append("\u2022 ")
                   .append(ri.getIngredientName())
                   .append(" \u2013 ")
                   .append(ri.getQuantity())
                   .append(" ")
                   .append(ri.getUnit())
                   .append("\n");
        }
        tvIngredients.setText(ingText.toString().trim());
        tvSteps.setText(recipe.getSteps());
    }
}
