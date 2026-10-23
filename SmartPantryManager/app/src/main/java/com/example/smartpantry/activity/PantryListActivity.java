package com.example.smartpantry.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantry.R;
import com.example.smartpantry.adapter.PantryAdapter;
import com.example.smartpantry.db.PantryDao;
import com.example.smartpantry.db.SettingsDao;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Launcher screen: lists every pantry item from SQLite in a RecyclerView and
 * is the hub for navigating to Add/Edit, Suggested Recipes and Settings.
 */
public class PantryListActivity extends AppCompatActivity implements PantryAdapter.OnItemActionListener {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    private static final long EXPIRY_WARNING_MS = 3L * 24 * 60 * 60 * 1000;

    private PantryDao pantryDao;
    private SettingsDao settingsDao;
    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        pantryDao = new PantryDao(this);
        settingsDao = new SettingsDao(this);

        recyclerView = findViewById(R.id.recycler_pantry);
        emptyView = findViewById(R.id.text_empty_pantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PantryAdapter(pantryDao.getAllItems(), this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.button_add_ingredient).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
        findViewById(R.id.button_view_recipes).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_recipes) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return true; // nav_pantry: already here
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.getMenu().findItem(R.id.nav_pantry).setChecked(true);
        // Reload on every return so add/edit/delete from other screens show up.
        refreshList();
        showExpiryWarningIfEnabled();
    }

    private void refreshList() {
        List<PantryItem> items = pantryDao.getAllItems();
        adapter.updateItems(items);
        boolean empty = items.isEmpty();
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    /** Uses the Settings toggle: warn about items expiring within 3 days (or already expired). */
    private void showExpiryWarningIfEnabled() {
        if (!"true".equals(settingsDao.getValue("expiring_alerts_enabled", "true"))) return;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);
        long cutoff = System.currentTimeMillis() + EXPIRY_WARNING_MS;
        int count = 0;
        for (PantryItem item : pantryDao.getAllItems()) {
            String expiry = item.getExpiryDate();
            if (expiry == null || expiry.isEmpty()) continue;
            try {
                Date date = format.parse(expiry);
                if (date != null && date.getTime() <= cutoff) count++;
            } catch (ParseException ignored) {
                // malformed date: skip
            }
        }
        if (count > 0) {
            Snackbar.make(recyclerView, getResources().getQuantityString(
                    R.plurals.expiring_soon_banner, count, count), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClicked(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClicked(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_message, item.getName()))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    pantryDao.deleteItem(item.getId());
                    refreshList();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
