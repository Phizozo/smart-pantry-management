package com.example.smartpantry.util;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The assignment core rule (Section 2.3): a recipe is "suggested" ONLY if
 * every ingredient it needs is in the pantry in at least the required
 * quantity. No partial matches. Robust to case, whitespace, simple
 * singular/plural names, and unit differences (kg vs g, cup vs ml).
 */
public final class IngredientMatcher {

    private IngredientMatcher() { }

    /** Lowercase, trim, collapse spaces, strip a simple plural. */
    public static String normalizeName(String rawName) {
        if (rawName == null) return "";
        String name = rawName.trim().toLowerCase().replaceAll("\\s+", " ");
        return singularize(name);
    }

    private static String singularize(String name) {
        if (name.endsWith("leaves")) {                      // bay leaves -> bay leaf
            return name.substring(0, name.length() - 6) + "leaf";
        }
        if (name.endsWith("loaves")) {                      // loaves -> loaf
            return name.substring(0, name.length() - 6) + "loaf";
        }
        if (name.endsWith("ies") && name.length() > 3) {    // berries -> berry
            return name.substring(0, name.length() - 3) + "y";
        }
        if (name.endsWith("oes") && name.length() > 3) {    // tomatoes -> tomato
            return name.substring(0, name.length() - 2);
        }
        if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 1) { // eggs -> egg, cloves -> clove
            return name.substring(0, name.length() - 1);
        }
        return name;
    }

    /** Key = normalised name + unit category/unit, value = total in base units. */
    private static String pantryKey(String name, String unit) {
        UnitConverter.Category cat = UnitConverter.categoryOf(unit);
        String unitPart = cat == UnitConverter.Category.COUNT
                ? UnitConverter.normalizeUnit(unit)
                : cat.name();
        return normalizeName(name) + "|" + unitPart;
    }

    private static Map<String, Double> buildPantryIndex(List<PantryItem> pantryItems) {
        Map<String, Double> index = new HashMap<>();
        for (PantryItem item : pantryItems) {
            double base = UnitConverter.toBaseUnit(item.getQuantity(), item.getUnit());
            Double existing = index.get(pantryKey(item.getName(), item.getUnit()));
            index.put(pantryKey(item.getName(), item.getUnit()), (existing == null ? 0 : existing) + base);
        }
        return index;
    }

    /** True only if the pantry fully covers the recipe (strict match). */
    public static boolean pantryCoversRecipe(Recipe recipe, List<PantryItem> pantryItems) {
        return missingIngredients(recipe, pantryItems).isEmpty();
    }

    /**
     * Names of recipe ingredients that are absent, in an incompatible unit,
     * or present in an insufficient quantity. Empty list = recipe qualifies.
     */
    public static List<String> missingIngredients(Recipe recipe, List<PantryItem> pantryItems) {
        Map<String, Double> index = buildPantryIndex(pantryItems);
        List<String> missing = new ArrayList<>();
        for (RecipeIngredient required : recipe.getIngredients()) {
            Double available = index.get(pantryKey(required.getName(), required.getUnit()));
            double needed = UnitConverter.toBaseUnit(required.getQuantity(), required.getUnit());
            // small epsilon so 0.5 kg vs 500 g never fails on floating-point noise
            if (available == null || available + 1e-9 < needed) {
                missing.add(required.getName());
            }
        }
        return missing;
    }

    /** Main suggestions list: strict matches only. */
    public static List<Recipe> filterStrictMatches(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (pantryCoversRecipe(recipe, pantryItems)) result.add(recipe);
        }
        return result;
    }

    /** Stretch goal: recipes missing exactly one ingredient (kept separate). */
    public static List<Recipe> filterAlmostThere(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (missingIngredients(recipe, pantryItems).size() == 1) result.add(recipe);
        }
        return result;
    }
}
