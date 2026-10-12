package com.example.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;

/**
 * Settings screen for user preferences.
 *
 * Exposes three controls, all persisted with SharedPreferences:
 * <ul>
 *   <li><b>Expiring-item alerts</b> — checkbox toggle</li>
 *   <li><b>Units preference</b> — Spinner (Metric / Imperial)</li>
 *   <li><b>Expiry warning threshold</b> — Spinner (1–7 days)</li>
 * </ul>
 *
 * Preferences are saved automatically on every change (no Save button needed),
 * which follows modern Android UX conventions.
 *
 * @author Smart Pantry Manager
 */
public class SettingsActivity extends AppCompatActivity {

    private CheckBox cbExpiringAlerts;
    private Spinner spUnits, spExpiryDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        cbExpiringAlerts = findViewById(R.id.cbExpiringAlerts);
        spUnits = findViewById(R.id.spUnitPreference);
        spExpiryDays = findViewById(R.id.spExpiryDays);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Metric (g, ml)", "Imperial (oz, cups)"});
        spUnits.setAdapter(unitAdapter);

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"1", "2", "3", "5", "7"});
        spExpiryDays.setAdapter(daysAdapter);

        loadPrefs();

        cbExpiringAlerts.setOnCheckedChangeListener((btn, checked) -> savePrefs());
        spUnits.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                                       int position, long id) { savePrefs(); }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        spExpiryDays.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                                       int position, long id) { savePrefs(); }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_settings);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                finish();
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                return true;
            }
            return false;
        });
    }

    private void loadPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cbExpiringAlerts.setChecked(prefs.getBoolean("expiring_alerts", true));

        String units = prefs.getString("unit_pref", "Metric (g, ml)");
        for (int i = 0; i < spUnits.getCount(); i++) {
            if (spUnits.getItemAtPosition(i).toString().equals(units)) {
                spUnits.setSelection(i);
                break;
            }
        }

        String days = prefs.getString("expiry_days", "3");
        for (int i = 0; i < spExpiryDays.getCount(); i++) {
            if (spExpiryDays.getItemAtPosition(i).toString().equals(days)) {
                spExpiryDays.setSelection(i);
                break;
            }
        }
    }

    private void savePrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putBoolean("expiring_alerts", cbExpiringAlerts.isChecked())
                .putString("unit_pref", spUnits.getSelectedItem().toString())
                .putString("expiry_days", spExpiryDays.getSelectedItem().toString())
                .apply();
    }
}
