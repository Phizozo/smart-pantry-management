package com.example.smartpantry.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantry.db.PantryContract.RecipeEntry;
import com.example.smartpantry.db.PantryContract.RecipeIngredientEntry;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/** Read-only access to the seeded recipe collection. */
public class RecipeDao {

    private final PantryDbHelper dbHelper;

    public RecipeDao(Context context) {
        this.dbHelper = new PantryDbHelper(context.getApplicationContext());
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RecipeEntry.TABLE_NAME, null, null, null, null, null,
                RecipeEntry.COL_NAME + " ASC");
        try {
            while (c.moveToNext()) recipes.add(fromCursor(db, c));
        } finally {
            c.close();
        }
        return recipes;
    }

    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(RecipeEntry.TABLE_NAME, null, RecipeEntry.COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        try {
            return c.moveToFirst() ? fromCursor(db, c) : null;
        } finally {
            c.close();
        }
    }

    private Recipe fromCursor(SQLiteDatabase db, Cursor c) {
        Recipe r = new Recipe();
        r.setId(c.getLong(c.getColumnIndexOrThrow(RecipeEntry.COL_ID)));
        r.setName(c.getString(c.getColumnIndexOrThrow(RecipeEntry.COL_NAME)));
        r.setSteps(c.getString(c.getColumnIndexOrThrow(RecipeEntry.COL_STEPS)));
        r.setIngredients(loadIngredients(db, r.getId()));
        return r;
    }

    private List<RecipeIngredient> loadIngredients(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor c = db.query(RecipeIngredientEntry.TABLE_NAME, null,
                RecipeIngredientEntry.COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)},
                null, null, null);
        try {
            while (c.moveToNext()) {
                RecipeIngredient ing = new RecipeIngredient();
                ing.setId(c.getLong(c.getColumnIndexOrThrow(RecipeIngredientEntry.COL_ID)));
                ing.setRecipeId(recipeId);
                ing.setName(c.getString(c.getColumnIndexOrThrow(RecipeIngredientEntry.COL_NAME)));
                ing.setQuantity(c.getDouble(c.getColumnIndexOrThrow(RecipeIngredientEntry.COL_QUANTITY)));
                ing.setUnit(c.getString(c.getColumnIndexOrThrow(RecipeIngredientEntry.COL_UNIT)));
                list.add(ing);
            }
        } finally {
            c.close();
        }
        return list;
    }
}
