package com.example.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper subclass that creates and upgrades the Smart Pantry database.

 * Database schema (3 tables):
 * <ul>
 *   <li><b>pantry</b> – user-entered ingredients (id, name, quantity, unit, expiry_date)</li>
 *   <li><b>recipes</b> – 18 pre-seeded recipes (id, name, steps)</li>
 *   <li><b>recipe_ingredients</b> – many-to-one link (id, recipe_id, ingredient_name,
 *       quantity, unit) with a foreign key to recipes</li>
 * </ul>
 *
 * Uses the singleton pattern so every Activity shares one database connection.
 * Seed data is inserted inside {@link #onCreate}, which only runs the first
 * time the app is installed – guaranteeing data persists across sessions.
 *
 * @author Smart Pantry Manager
 */
public class PantryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // ---- pantry table columns ----
    public static final String TABLE_PANTRY = "pantry";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // ---- recipes table columns ----
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_STEPS = "steps";

    // ---- recipe_ingredients table columns ----
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    private static PantryDbHelper instance;

    public static synchronized PantryDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PantryDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private PantryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

     // Added CRUD for pantry items
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " REAL NOT NULL DEFAULT 0, "
                + COL_UNIT + " TEXT NOT NULL, "
                + COL_EXPIRY + " TEXT DEFAULT '')");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RECIPE_NAME + " TEXT NOT NULL, "
                + COL_RECIPE_STEPS + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + COL_RI_NAME + " TEXT NOT NULL, "
                + COL_RI_QUANTITY + " REAL NOT NULL, "
                + COL_RI_UNIT + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_RI_RECIPE_ID + ") REFERENCES "
                + TABLE_RECIPES + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        insertRecipe(db, "Vegetable Stir Fry",
                "1. Heat oil in a wok or large pan over high heat.\n" +
                "2. Add chopped vegetables and stir-fry for 4-5 minutes.\n" +
                "3. Add soy sauce and garlic, toss to combine.\n" +
                "4. Serve hot over rice or noodles.",
                new Object[][]{
                        {"vegetable oil", 2.0, "tbsp"},
                        {"onion", 1.0, "whole"},
                        {"carrot", 1.0, "whole"},
                        {"broccoli", 1.0, "whole"},
                        {"soy sauce", 2.0, "tbsp"},
                        {"garlic", 2.0, "clove"}
                });

        insertRecipe(db, "Omelette",
                "1. Crack eggs into a bowl and whisk with salt and pepper.\n" +
                "2. Melt butter in a non-stick pan over medium heat.\n" +
                "3. Pour in egg mixture and cook until nearly set.\n" +
                "4. Fold in half and slide onto a plate.",
                new Object[][]{
                        {"egg", 3.0, "whole"},
                        {"butter", 1.0, "tbsp"},
                        {"salt", 1.0, "pinch"},
                        {"black pepper", 1.0, "pinch"}
                });

        insertRecipe(db, "Tomato Pasta",
                "1. Cook pasta in salted boiling water until al dente.\n" +
                "2. Heat olive oil in a pan, sauté garlic for 30 seconds.\n" +
                "3. Add chopped tomatoes and simmer for 10 minutes.\n" +
                "4. Toss pasta with sauce, season and serve.",
                new Object[][]{
                        {"pasta", 200.0, "g"},
                        {"tomato", 4.0, "whole"},
                        {"garlic", 3.0, "clove"},
                        {"olive oil", 2.0, "tbsp"},
                        {"salt", 1.0, "pinch"}
                });

        insertRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter one side of each bread slice.\n" +
                "2. Place cheese between the unbuttered sides.\n" +
                "3. Grill in a pan over medium heat until golden on both sides.\n" +
                "4. Slice diagonally and serve.",
                new Object[][]{
                        {"bread", 2.0, "slice"},
                        {"cheese", 50.0, "g"},
                        {"butter", 1.0, "tbsp"}
                });

        insertRecipe(db, "Banana Smoothie",
                "1. Peel and slice the banana.\n" +
                "2. Add banana, milk and yoghurt to a blender.\n" +
                "3. Blend on high for 30 seconds until smooth.\n" +
                "4. Pour into a glass and enjoy immediately.",
                new Object[][]{
                        {"banana", 1.0, "whole"},
                        {"milk", 250.0, "ml"},
                        {"yoghurt", 100.0, "g"}
                });

        insertRecipe(db, "Garlic Bread",
                "1. Preheat oven to 180C.\n" +
                "2. Mix softened butter with minced garlic and salt.\n" +
                "3. Spread garlic butter over the bread slices.\n" +
                "4. Bake for 10 minutes until crispy.",
                new Object[][]{
                        {"bread", 4.0, "slice"},
                        {"butter", 50.0, "g"},
                        {"garlic", 4.0, "clove"},
                        {"salt", 1.0, "pinch"}
                });

        insertRecipe(db, "Scrambled Eggs",
                "1. Crack eggs into a bowl, add milk and whisk.\n" +
                "2. Melt butter in a pan over low heat.\n" +
                "3. Pour in eggs and stir gently and continuously.\n" +
                "4. Cook until soft curds form, season and serve.",
                new Object[][]{
                        {"egg", 3.0, "whole"},
                        {"milk", 30.0, "ml"},
                        {"butter", 1.0, "tbsp"},
                        {"salt", 1.0, "pinch"}
                });

        insertRecipe(db, "Rice Bowl",
                "1. Rinse rice and cook with water according to package.\n" +
                "2. Chop vegetables into small pieces.\n" +
                "3. Serve rice topped with fresh vegetables.\n" +
                "4. Drizzle with soy sauce before eating.",
                new Object[][]{
                        {"rice", 200.0, "g"},
                        {"carrot", 1.0, "whole"},
                        {"cucumber", 1.0, "whole"},
                        {"soy sauce", 1.0, "tbsp"}
                });

        insertRecipe(db, "Potato Soup",
                "1. Peel and dice potatoes into small cubes.\n" +
                "2. Boil potatoes in salted water until tender, about 15 minutes.\n" +
                "3. Drain most of the water, mash potatoes roughly.\n" +
                "4. Stir in milk and butter, season with salt and pepper.",
                new Object[][]{
                        {"potato", 4.0, "whole"},
                        {"milk", 200.0, "ml"},
                        {"butter", 2.0, "tbsp"},
                        {"onion", 1.0, "whole"},
                        {"salt", 1.0, "pinch"}
                });

        insertRecipe(db, "Fruit Salad",
                "1. Wash and peel fruits as needed.\n" +
                "2. Chop banana, apple and strawberries into bite-size pieces.\n" +
                "3. Combine all fruit in a large bowl.\n" +
                "4. Toss gently and serve chilled.",
                new Object[][]{
                        {"banana", 1.0, "whole"},
                        {"apple", 1.0, "whole"},
                        {"strawberry", 150.0, "g"}
                });

        insertRecipe(db, "Chicken Salad",
                "1. Cook chicken in a pan until done, then slice.\n" +
                "2. Tear lettuce into a bowl.\n" +
                "3. Add sliced chicken, chopped tomato and grated carrot.\n" +
                "4. Drizzle with olive oil and toss to combine.",
                new Object[][]{
                        {"chicken breast", 200.0, "g"},
                        {"lettuce", 1.0, "whole"},
                        {"tomato", 2.0, "whole"},
                        {"carrot", 1.0, "whole"},
                        {"olive oil", 1.0, "tbsp"}
                });

        insertRecipe(db, "Pancakes",
                "1. Mix flour, egg and milk into a smooth batter.\n" +
                "2. Add sugar and a pinch of salt, stir to combine.\n" +
                "3. Heat butter in a pan and pour small ladles of batter.\n" +
                "4. Flip when bubbles form, cook until golden on both sides.",
                new Object[][]{
                        {"flour", 200.0, "g"},
                        {"egg", 2.0, "whole"},
                        {"milk", 300.0, "ml"},
                        {"sugar", 2.0, "tbsp"},
                        {"butter", 1.0, "tbsp"},
                        {"salt", 1.0, "pinch"}
                });

        insertRecipe(db, "Bean Chilli",
                "1. Drain and rinse kidney beans.\n" +
                "2. Sauté onion, garlic and chopped tomato in oil.\n" +
                "3. Add kidney beans and chilli powder, simmer 15 minutes.\n" +
                "4. Serve with rice or bread.",
                new Object[][]{
                        {"kidney bean", 400.0, "g"},
                        {"onion", 1.0, "whole"},
                        {"tomato", 3.0, "whole"},
                        {"garlic", 2.0, "clove"},
                        {"chilli powder", 1.0, "tsp"},
                        {"vegetable oil", 1.0, "tbsp"}
                });

        insertRecipe(db, "Cucumber Sandwiches",
                "1. Thinly slice the cucumber.\n" +
                "2. Spread butter on each bread slice.\n" +
                "3. Layer cucumber slices on buttered bread, season with salt.\n" +
                "4. Top with second slice, cut into triangles.",
                new Object[][]{
                        {"bread", 4.0, "slice"},
                        {"cucumber", 1.0, "whole"},
                        {"butter", 1.0, "tbsp"},
                        {"salt", 1.0, "pinch"}
                });

        insertRecipe(db, "Tomato Soup",
                "1. Chop tomatoes and onion roughly.\n" +
                "2. Sauté onion in butter until soft, add garlic.\n" +
                "3. Add tomatoes and 500ml water, simmer 20 minutes.\n" +
                "4. Season with salt and pepper, serve warm.",
                new Object[][]{
                        {"tomato", 6.0, "whole"},
                        {"onion", 1.0, "whole"},
                        {"garlic", 2.0, "clove"},
                        {"butter", 1.0, "tbsp"},
                        {"salt", 1.0, "pinch"},
                        {"black pepper", 1.0, "pinch"}
                });

        insertRecipe(db, "Fried Rice",
                "1. Cook rice ahead and let it cool.\n" +
                "2. Heat oil in a wok, scramble the egg and set aside.\n" +
                "3. Stir-fry onion and carrots, add rice and soy sauce.\n" +
                "4. Return egg to the pan, toss everything together and serve.",
                new Object[][]{
                        {"rice", 300.0, "g"},
                        {"egg", 2.0, "whole"},
                        {"carrot", 1.0, "whole"},
                        {"onion", 1.0, "whole"},
                        {"soy sauce", 2.0, "tbsp"},
                        {"vegetable oil", 2.0, "tbsp"}
                });

        insertRecipe(db, "Baked Potato",
                "1. Preheat oven to 200C.\n" +
                "2. Wash potatoes and pierce several times with a fork.\n" +
                "3. Rub with oil and salt, bake for 45-60 minutes until soft.\n" +
                "4. Split open and top with butter.",
                new Object[][]{
                        {"potato", 2.0, "whole"},
                        {"vegetable oil", 1.0, "tbsp"},
                        {"butter", 1.0, "tbsp"},
                        {"salt", 1.0, "pinch"}
                });

        insertRecipe(db, "Yoghurt Parfait",
                "1. Layer yoghurt in the bottom of a glass.\n" +
                "2. Add a layer of chopped strawberries.\n" +
                "3. Repeat layers until the glass is full.\n" +
                "4. Top with banana slices and serve.",
                new Object[][]{
                        {"yoghurt", 200.0, "g"},
                        {"strawberry", 150.0, "g"},
                        {"banana", 1.0, "whole"}
                });
    }

    private void insertRecipe(SQLiteDatabase db, String name, String steps, Object[][] ingredients) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, values);

        for (Object[] ing : ingredients) {
            ContentValues iv = new ContentValues();
            iv.put(COL_RI_RECIPE_ID, recipeId);
            iv.put(COL_RI_NAME, (String) ing[0]);
            iv.put(COL_RI_QUANTITY, (double) ing[1]);
            iv.put(COL_RI_UNIT, (String) ing[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, iv);
        }
    }
}
