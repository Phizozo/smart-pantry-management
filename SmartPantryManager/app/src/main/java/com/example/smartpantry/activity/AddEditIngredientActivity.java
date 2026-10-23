package com.example.smartpantry.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantry.R;
import com.example.smartpantry.db.PantryDao;
import com.example.smartpantry.model.PantryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * Create + Update (and Delete when editing) for one pantry item, with
 * input validation (Section 3.1). Edit mode is triggered by an Intent extra.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private static final String[] COMMON_UNITS = {"g", "kg", "ml", "l", "tsp", "tbsp", "cup", "oz", "lb"};

    private PantryDao pantryDao;
    private long editingItemId = -1L;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private AutoCompleteTextView editUnit;
    private TextInputEditText editExpiry;
    private MaterialButton deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        pantryDao = new PantryDao(this);

        layoutName = findViewById(R.id.layout_name);
        layoutQuantity = findViewById(R.id.layout_quantity);
        editName = findViewById(R.id.edit_name);
        editQuantity = findViewById(R.id.edit_quantity);
        editUnit = findViewById(R.id.edit_unit);
        editExpiry = findViewById(R.id.edit_expiry);
        deleteButton = findViewById(R.id.button_delete);

        editUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, COMMON_UNITS));
        editUnit.setThreshold(0);
        editUnit.setOnClickListener(v -> editUnit.showDropDown());
        editExpiry.setOnClickListener(v -> showDatePicker());

        editingItemId = getIntent().getLongExtra(PantryListActivity.EXTRA_ITEM_ID, -1L);
        if (editingItemId != -1L) populateForEdit(editingItemId);

        MaterialButton save = findViewById(R.id.button_save);
        save.setOnClickListener(v -> attemptSave());
        deleteButton.setOnClickListener(v -> {
            pantryDao.deleteItem(editingItemId);
            finish();
        });
    }

    private void populateForEdit(long id) {
        PantryItem item = pantryDao.getItemById(id);
        if (item == null) return;
        editName.setText(item.getName());
        double q = item.getQuantity();
        editQuantity.setText(q == Math.floor(q) ? String.format(Locale.US, "%.0f", q) : String.valueOf(q));
        editUnit.setText(item.getUnit(), false);
        editExpiry.setText(item.getExpiryDate());
        deleteButton.setVisibility(View.VISIBLE);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) ->
                editExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private static String textOf(android.widget.TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }

    /** Validate, then create or update. */
    private void attemptSave() {
        layoutName.setError(null);
        layoutQuantity.setError(null);

        String name = textOf(editName);
        String quantityText = textOf(editQuantity);
        boolean valid = true;

        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            valid = false;
        }

        double quantity = 0;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            quantity = 0;
        }
        if (quantity <= 0) {
            layoutQuantity.setError(getString(R.string.error_quantity_invalid));
            valid = false;
        }
        if (!valid) return;

        PantryItem item = new PantryItem();
        item.setId(editingItemId != -1L ? editingItemId : 0);
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(textOf(editUnit).toLowerCase(Locale.ROOT));
        item.setExpiryDate(textOf(editExpiry));

        if (editingItemId != -1L) pantryDao.updateItem(item);
        else pantryDao.insertItem(item);
        finish();
    }
}
