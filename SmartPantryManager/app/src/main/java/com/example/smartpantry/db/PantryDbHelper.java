package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantry.db.PantryContract.PantryEntry;
import com.example.smartpantry.db.PantryContract.RecipeEntry;
import com.example.smartpantry.db.PantryContract.RecipeIngredientEntry;
import com.example.smartpantry.db.PantryContract.SettingsEntry;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

/** Creates the schema on first run and seeds the 18 recipes (Section 2.2). */
public class PantryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public PantryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + PantryEntry.TABLE_NAME + " (" +
                PantryEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PantryEntry.COL_NAME + " TEXT NOT NULL, " +
                PantryEntry.COL_QUANTITY + " REAL NOT NULL, " +
                PantryEntry.COL_UNIT + " TEXT, " +
                PantryEntry.COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + RecipeEntry.TABLE_NAME + " (" +
                RecipeEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipeEntry.COL_NAME + " TEXT NOT NULL, " +
                RecipeEntry.COL_STEPS + " TEXT)");

        db.execSQL("CREATE TABLE " + RecipeIngredientEntry.TABLE_NAME + " (" +
                RecipeIngredientEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipeIngredientEntry.COL_RECIPE_ID + " INTEGER NOT NULL, " +
                RecipeIngredientEntry.COL_NAME + " TEXT NOT NULL, " +
                RecipeIngredientEntry.COL_QUANTITY + " REAL NOT NULL, " +
                RecipeIngredientEntry.COL_UNIT + " TEXT, " +
                "FOREIGN KEY(" + RecipeIngredientEntry.COL_RECIPE_ID + ") REFERENCES " +
                RecipeEntry.TABLE_NAME + "(" + RecipeEntry.COL_ID + "))");

        db.execSQL("CREATE TABLE " + SettingsEntry.TABLE_NAME + " (" +
                SettingsEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SettingsEntry.COL_KEY + " TEXT UNIQUE NOT NULL, " +
                SettingsEntry.COL_VALUE + " TEXT)");

        seedRecipes(db);
        putSetting(db, "expiring_alerts_enabled", "true");
        putSetting(db, "preferred_unit_system", "metric");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RecipeIngredientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PantryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SettingsEntry.TABLE_NAME);
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        for (Recipe recipe : RecipeSeedData.buildSeedRecipes()) {
            ContentValues rv = new ContentValues();
            rv.put(RecipeEntry.COL_NAME, recipe.getName());
            rv.put(RecipeEntry.COL_STEPS, recipe.getSteps());
            long recipeId = db.insert(RecipeEntry.TABLE_NAME, null, rv);

            for (RecipeIngredient ing : recipe.getIngredients()) {
                ContentValues iv = new ContentValues();
                iv.put(RecipeIngredientEntry.COL_RECIPE_ID, recipeId);
                iv.put(RecipeIngredientEntry.COL_NAME, ing.getName());
                iv.put(RecipeIngredientEntry.COL_QUANTITY, ing.getQuantity());
                iv.put(RecipeIngredientEntry.COL_UNIT, ing.getUnit());
                db.insert(RecipeIngredientEntry.TABLE_NAME, null, iv);
            }
        }
    }

    private void putSetting(SQLiteDatabase db, String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(SettingsEntry.COL_KEY, key);
        cv.put(SettingsEntry.COL_VALUE, value);
        db.insert(SettingsEntry.TABLE_NAME, null, cv);
    }
}
