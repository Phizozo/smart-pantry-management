package com.example.smartpantry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantry.db.PantryRepository;
import com.example.smartpantry.models.Ingredient;

/**
 * Form screen for creating or editing a pantry ingredient.
 *
 * Operates in two modes determined by the Intent extra:
 * <ul>
 *   <li><b>Add mode</b> — launched from the FAB; saves a new row via
 *       {@code repository.addIngredient()}</li>
 *   <li><b>Edit mode</b> — launched by tapping an existing item; the
 *       ingredient is pre-populated and saved via {@code updateIngredient()}</li>
 * </ul>
 *
 * Input validation is performed on every field before the record is written:
 * name must not be empty, quantity must be a positive number, and the expiry
 * date (if provided) must match the ISO yyyy-MM-dd format.
 *
 * @author Smart Pantry Manager
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    /** Supported measurement units shown in the unit Spinner. */
    private static final String[] UNITS = {
            "whole", "g", "kg", "ml", "l", "tsp", "tbsp", "cup", "slice", "clove", "pinch"
    };

    private EditText etName, etQuantity, etExpiry;
    private Spinner spUnit;
    private Button btnSave;
    private PantryRepository repository;
    private Ingredient editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        repository = new PantryRepository(this);

        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etIngredientQuantity);
        etExpiry = findViewById(R.id.etIngredientExpiry);
        spUnit = findViewById(R.id.spUnit);
        btnSave = findViewById(R.id.btnSaveIngredient);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, UNITS);
        spUnit.setAdapter(unitAdapter);

        editing = (Ingredient) getIntent().getSerializableExtra(MainActivity.EXTRA_INGREDIENT);
        if (editing != null) {
            setTitle("Edit Ingredient");
            etName.setText(editing.getName());
            etQuantity.setText(String.valueOf(editing.getQuantity()));
            etExpiry.setText(editing.getExpiryDate());
            selectUnit(editing.getUnit());
        } else {
            setTitle("Add Ingredient");
        }

        btnSave.setOnClickListener(v -> save());

        findViewById(R.id.btnPickDate).setOnClickListener(v -> showDatePicker());
    }

    private void selectUnit(String unit) {
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equalsIgnoreCase(unit)) {
                spUnit.setSelection(i);
                return;
            }
        }
    }

    private void showDatePicker() {
        long now = System.currentTimeMillis();
        new android.app.DatePickerDialog(this, (view, year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            etExpiry.setText(date);
        }, java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
                java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
                java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH))
                .show();
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = spUnit.getSelectedItem().toString();
        String expiry = etExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            etName.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number (e.g. 2 or 0.5)");
            etQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than zero");
            etQuantity.requestFocus();
            return;
        }

        if (!expiry.isEmpty() && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            etExpiry.setError("Use format yyyy-MM-dd or leave blank");
            etExpiry.requestFocus();
            return;
        }

        Ingredient ingredient = editing != null ? editing : new Ingredient();
        ingredient.setName(name);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unit);
        ingredient.setExpiryDate(expiry);

        if (editing != null) {
            repository.updateIngredient(ingredient);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        } else {
            repository.addIngredient(ingredient);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        }
        setResult(RESULT_OK);
        finish();
    }
}
