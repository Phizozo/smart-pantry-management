package com.example.smartpantry.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;
import com.example.smartpantry.db.SettingsDao;
import com.google.android.material.switchmaterial.SwitchMaterial;

/** Settings screen: expiring-soon alerts toggle and unit-system preference, persisted in SQLite. */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        SettingsDao settings = new SettingsDao(this);
        SwitchMaterial alerts = findViewById(R.id.switch_expiring_alerts);
        SwitchMaterial units = findViewById(R.id.switch_unit_system);

        alerts.setChecked("true".equals(settings.getValue("expiring_alerts_enabled", "true")));
        units.setChecked("imperial".equals(settings.getValue("preferred_unit_system", "metric")));

        alerts.setOnCheckedChangeListener((b, checked) ->
                settings.setValue("expiring_alerts_enabled", String.valueOf(checked)));
        units.setOnCheckedChangeListener((b, checked) ->
                settings.setValue("preferred_unit_system", checked ? "imperial" : "metric"));
    }
}
