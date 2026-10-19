package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.adapters.IngredientAdapter;
import com.example.smartpantry.db.PantryRepository;
import com.example.smartpantry.models.Ingredient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Home screen of the Smart Pantry Manager.
 * Displays all pantry ingredients in a RecyclerView sorted alphabetically.
 * Users can:
 * <ul>
 *   <li>Tap the FAB to add a new ingredient</li>
 *   <li>Tap an item to edit it</li>
 *   <li>Long-press an item to delete it</li>
 * </ul>
 *
 * On resume the Activity refreshes the list from SQLite and (if enabled
 * in Settings) shows a dialog warning about items expiring soon.
 *
 * @author Smart Pantry Manager
 */

//Smart Pantry - CRUD implementation added
public class MainActivity extends AppCompatActivity
        implements IngredientAdapter.OnItemClickListener {

    /** Extra key used to pass an Ingredient object to AddEditIngredientActivity. */
    public static final String EXTRA_INGREDIENT = "extra_ingredient";

    private PantryRepository repository;
    private IngredientAdapter adapter;
    private TextView tvEmpty;
    private final DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) refresh();
            });

    @Override
    // Smart Pantry - CRUD implementation added
    //Checking expiry dates on app start
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new PantryRepository(this);

        tvEmpty = findViewById(R.id.tvEmptyPantry);
        RecyclerView recyclerView = findViewById(R.id.recyclerIngredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditIngredientActivity.class);
            editLauncher.launch(intent);
        });

        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        checkExpiringSoon();
    }

    private void refresh() {
        List<Ingredient> ingredients = repository.getAllIngredients();
        adapter.setItems(ingredients);
        tvEmpty.setVisibility(ingredients.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void checkExpiringSoon() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean alertsOn = prefs.getBoolean("expiring_alerts", true);
        if (!alertsOn) return;

        int days = 3;
        try {
            days = Integer.parseInt(prefs.getString("expiry_days", "3"));
        } catch (NumberFormatException ignored) {}

        LocalDate threshold = LocalDate.now().plusDays(days);
        StringBuilder warning = new StringBuilder();
        for (Ingredient ing : repository.getAllIngredients()) {
            if (ing.getExpiryDate() == null || ing.getExpiryDate().isEmpty()) continue;
            try {
                LocalDate exp = LocalDate.parse(ing.getExpiryDate(), iso);
                if (!exp.isAfter(LocalDate.now()) && !exp.isBefore(threshold)) {
                    warning.append("- ").append(ing.getName())
                            .append(" expires ").append(ing.getExpiryDate()).append("\n");
                }
            } catch (Exception ignored) {}
        }

        if (warning.length() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Items expiring soon")
                    .setMessage(warning.toString().trim())
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    public void onItemClick(Ingredient ingredient) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(EXTRA_INGREDIENT, ingredient);
        editLauncher.launch(intent);
    }

    @Override
    public void onItemLongClick(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete " + ingredient.getName() + "?")
                .setMessage("Remove this ingredient from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.deleteIngredient(ingredient.getId());
                    refresh();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
