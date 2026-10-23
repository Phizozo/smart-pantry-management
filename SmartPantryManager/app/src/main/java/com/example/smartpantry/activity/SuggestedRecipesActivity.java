package com.example.smartpantry.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapter.RecipeAdapter;
import com.example.smartpantry.db.PantryDao;
import com.example.smartpantry.db.RecipeDao;
import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.util.IngredientMatcher;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * Runs the strict-matching rule (Section 2.3) against the current pantry.
 * Tab 1 = strict matches only. Tab 2 = "Almost There" (stretch goal), kept
 * clearly separate so it can never leak into the main list.
 */
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecipeDao recipeDao;
    private PantryDao pantryDao;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private List<Recipe> strictMatches;
    private List<Recipe> almostThereMatches;
    private boolean showingAlmostThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        recipeDao = new RecipeDao(this);
        pantryDao = new PantryDao(this);

        recyclerView = findViewById(R.id.recycler_recipes);
        emptyView = findViewById(R.id.text_empty_recipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        TabLayout tabs = findViewById(R.id.tab_layout);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                showingAlmostThere = tab.getPosition() == 1;
                render();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recompute every time: the pantry may have changed since last shown.
        runMatching();
        render();
    }

    private void runMatching() {
        List<PantryItem> pantry = pantryDao.getAllItems();
        List<Recipe> all = recipeDao.getAllRecipes();
        strictMatches = IngredientMatcher.filterStrictMatches(all, pantry);
        almostThereMatches = IngredientMatcher.filterAlmostThere(all, pantry);
    }

    private void render() {
        if (strictMatches == null) return;
        List<Recipe> current = showingAlmostThere ? almostThereMatches : strictMatches;
        recyclerView.setAdapter(new RecipeAdapter(current, this, showingAlmostThere));

        boolean empty = current.isEmpty();
        emptyView.setText(showingAlmostThere ? R.string.empty_almost_message : R.string.empty_recipes_message);
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRecipeClicked(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
