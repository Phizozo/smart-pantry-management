package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smartpantry.models.Ingredient;
import com.example.smartpantry.models.Recipe;
import com.example.smartpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class that encapsulates all SQLite read/write operations.
 *
 * Provides a clean CRUD API for the pantry table and read-only access
 * to the seeded recipe tables. Activities call this class instead of
 * writing raw SQL, which keeps database logic in one place and makes
 * the Activities easier to read and maintain.
 *
 * @author Smart Pantry Manager
 */
public class PantryRepository {

    /** Shared database helper instance (singleton). */
    private final PantryDbHelper dbHelper;

    /**
     * Creates a repository bound to the shared database helper.
     *
     * @param context any valid Context; the application context is used internally
     */
    public PantryRepository(Context context) {
        this.dbHelper = PantryDbHelper.getInstance(context);
    }

    // ---------- PANTRY CRUD ----------

    public long addIngredient(Ingredient ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PantryDbHelper.COL_NAME, ingredient.getName());
        values.put(PantryDbHelper.COL_QUANTITY, ingredient.getQuantity());
        values.put(PantryDbHelper.COL_UNIT, ingredient.getUnit());
        values.put(PantryDbHelper.COL_EXPIRY, ingredient.getExpiryDate());
        return db.insert(PantryDbHelper.TABLE_PANTRY, null, values);
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(PantryDbHelper.TABLE_PANTRY, null, null, null, null, null,
                PantryDbHelper.COL_NAME + " COLLATE NOCASE ASC");
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToIngredient(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Ingredient getIngredientById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(PantryDbHelper.TABLE_PANTRY, null,
                PantryDbHelper.COL_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);
        Ingredient ingredient = null;
        if (cursor.moveToFirst()) {
            ingredient = cursorToIngredient(cursor);
        }
        cursor.close();
        return ingredient;
    }

    public int updateIngredient(Ingredient ingredient) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PantryDbHelper.COL_NAME, ingredient.getName());
        values.put(PantryDbHelper.COL_QUANTITY, ingredient.getQuantity());
        values.put(PantryDbHelper.COL_UNIT, ingredient.getUnit());
        values.put(PantryDbHelper.COL_EXPIRY, ingredient.getExpiryDate());
        return db.update(PantryDbHelper.TABLE_PANTRY, values,
                PantryDbHelper.COL_ID + "=?", new String[]{String.valueOf(ingredient.getId())});
    }

    public int deleteIngredient(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(PantryDbHelper.TABLE_PANTRY,
                PantryDbHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    private Ingredient cursorToIngredient(Cursor cursor) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_ID)));
        ingredient.setName(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_NAME)));
        ingredient.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_QUANTITY)));
        ingredient.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_UNIT)));
        ingredient.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_EXPIRY)));
        return ingredient;
    }

    // ---------- RECIPE READS ----------

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(PantryDbHelper.TABLE_RECIPES, null, null, null, null, null,
                PantryDbHelper.COL_RECIPE_NAME + " COLLATE NOCASE ASC");
        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RECIPE_ID)));
                recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RECIPE_NAME)));
                recipe.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RECIPE_STEPS)));
                recipe.setIngredients(getIngredientsForRecipe(db, recipe.getId()));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipes;
    }

    public Recipe getRecipeById(int recipeId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Recipe recipe = null;
        Cursor cursor = db.query(PantryDbHelper.TABLE_RECIPES, null,
                PantryDbHelper.COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            recipe = new Recipe();
            recipe.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RECIPE_ID)));
            recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RECIPE_NAME)));
            recipe.setSteps(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RECIPE_STEPS)));
            recipe.setIngredients(getIngredientsForRecipe(db, recipeId));
        }
        cursor.close();
        return recipe;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(SQLiteDatabase db, int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor cursor = db.query(PantryDbHelper.TABLE_RECIPE_INGREDIENTS, null,
                PantryDbHelper.COL_RI_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                RecipeIngredient ri = new RecipeIngredient();
                ri.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RI_ID)));
                ri.setRecipeId(cursor.getInt(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RI_RECIPE_ID)));
                ri.setIngredientName(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RI_NAME)));
                ri.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RI_QUANTITY)));
                ri.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(PantryDbHelper.COL_RI_UNIT)));
                list.add(ri);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
