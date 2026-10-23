package com.example.smartpantry.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;
import com.example.smartpantry.db.RecipeDao;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

/** Full ingredient list and method for one recipe; id arrives via Intent extra. */
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1L);
        Recipe recipe = new RecipeDao(this).getRecipeById(recipeId);

        TextView nameView = findViewById(R.id.text_recipe_name);
        TextView ingredientsView = findViewById(R.id.text_ingredients);
        TextView stepsView = findViewById(R.id.text_steps);

        if (recipe == null) {
            nameView.setText(R.string.title_recipe_detail);
            return;
        }
        nameView.setText(recipe.getName());
        ingredientsView.setText(buildIngredientText(recipe));
        stepsView.setText(recipe.getSteps());
    }

    private String buildIngredientText(Recipe recipe) {
        StringBuilder sb = new StringBuilder();
        for (RecipeIngredient ing : recipe.getIngredients()) {
            if (sb.length() > 0) sb.append("\n");
            double q = ing.getQuantity();
            sb.append("\u2022 ").append(q == Math.floor(q) ? String.valueOf((long) q) : String.valueOf(q));
            if (ing.getUnit() != null && !ing.getUnit().isEmpty()) sb.append(" ").append(ing.getUnit());
            sb.append(" ").append(ing.getName());
        }
        return sb.toString();
    }
}
