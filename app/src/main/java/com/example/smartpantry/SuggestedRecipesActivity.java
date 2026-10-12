package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapters.RecipeAdapter;
import com.example.smartpantry.db.PantryRepository;
import com.example.smartpantry.models.Recipe;
import com.example.smartpantry.utils.RecipeMatcher;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Suggested Recipes screen — the core feature of the app.
 *
 * Loads all seeded recipes and the current pantry, then runs
 * {@link RecipeMatcher#findStrictMatches} to determine which recipes
 * the user can cook right now (every ingredient present in sufficient
 * quantity).
 *
 * A bonus "Almost There" section is displayed below the strict matches,
 * showing recipes missing exactly one ingredient. When zero recipes match,
 * a friendly empty-state message is shown instead of a blank screen.
 *
 * @author Smart Pantry Manager
 */
public class SuggestedRecipesActivity extends AppCompatActivity
        implements RecipeAdapter.OnItemClickListener {

    private PantryRepository repository;
    private RecipeAdapter strictAdapter;
    private RecipeAdapter almostAdapter;
    private TextView tvNoMatches, tvAlmostHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        repository = new PantryRepository(this);

        tvNoMatches = findViewById(R.id.tvNoMatches);
        tvAlmostHeader = findViewById(R.id.tvAlmostHeader);

        RecyclerView recyclerStrict = findViewById(R.id.recyclerStrictMatches);
        recyclerStrict.setLayoutManager(new LinearLayoutManager(this));
        strictAdapter = new RecipeAdapter(this);
        recyclerStrict.setAdapter(strictAdapter);

        RecyclerView recyclerAlmost = findViewById(R.id.recyclerAlmostThere);
        recyclerAlmost.setLayoutManager(new LinearLayoutManager(this));
        almostAdapter = new RecipeAdapter(this);
        recyclerAlmost.setAdapter(almostAdapter);

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMatches();
    }

    private void loadMatches() {
        List<Recipe> allRecipes = repository.getAllRecipes();
        List<com.example.smartpantry.models.Ingredient> pantry = repository.getAllIngredients();

        List<Recipe> strict = RecipeMatcher.findStrictMatches(allRecipes, pantry);
        List<Recipe> almost = RecipeMatcher.findAlmostThere(allRecipes, pantry);

        strictAdapter.setItems(strict);
        almostAdapter.setItems(almost);

        tvNoMatches.setVisibility(strict.isEmpty() ? View.VISIBLE : View.GONE);
        tvAlmostHeader.setVisibility(almost.isEmpty() ? View.GONE : View.VISIBLE);
        findViewById(R.id.recyclerAlmostThere).setVisibility(
                almost.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_recipes);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                finish();
                return true;
            } else if (id == R.id.nav_recipes) {
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    public void onItemClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
