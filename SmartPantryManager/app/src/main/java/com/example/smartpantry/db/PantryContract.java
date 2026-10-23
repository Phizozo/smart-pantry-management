package com.example.smartpantry.db;

/** Table and column name constants. */
public final class PantryContract {

    private PantryContract() { }

    public static final class PantryEntry {
        public static final String TABLE_NAME = "pantry_items";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "name";
        public static final String COL_QUANTITY = "quantity";
        public static final String COL_UNIT = "unit";
        public static final String COL_EXPIRY = "expiry_date";
    }

    public static final class RecipeEntry {
        public static final String TABLE_NAME = "recipes";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "name";
        public static final String COL_STEPS = "steps";
    }

    public static final class RecipeIngredientEntry {
        public static final String TABLE_NAME = "recipe_ingredients";
        public static final String COL_ID = "_id";
        public static final String COL_RECIPE_ID = "recipe_id";
        public static final String COL_NAME = "name";
        public static final String COL_QUANTITY = "quantity";
        public static final String COL_UNIT = "unit";
    }

    public static final class SettingsEntry {
        public static final String TABLE_NAME = "settings";
        public static final String COL_ID = "_id";
        public static final String COL_KEY = "pref_key";
        public static final String COL_VALUE = "pref_value";
    }
}
