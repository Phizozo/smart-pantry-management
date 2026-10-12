package com.example.smartpantry.utils;

import com.example.smartpantry.models.Ingredient;
import com.example.smartpantry.models.Recipe;
import com.example.smartpantry.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the strict-matching rule:
 * a recipe is only suggested when EVERY required ingredient is present
 * in the pantry in at least the required quantity.
 *
 * Also produces an "almost there" list (recipes missing exactly one
 * ingredient) for optional bonus credit - kept strictly separate.
 */
public final class RecipeMatcher {

    /** Aggregate of pantry stock keyed by normalised name. */
    private static class PantryStock {
        Map<String, double[]> byName = new HashMap<>(); // {totalBaseValue, category}
    }

    private RecipeMatcher() {}

    public static List<Recipe> findStrictMatches(
            List<Recipe> recipes, List<Ingredient> pantry) {

        PantryStock stock = buildStock(pantry);
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipeIsFullyCovered(recipe, stock)) {
                matches.add(recipe);
            }
        }
        return matches;
    }

    public static List<Recipe> findAlmostThere(
            List<Recipe> recipes, List<Ingredient> pantry) {

        PantryStock stock = buildStock(pantry);
        List<Recipe> almost = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (!recipeIsFullyCovered(recipe, stock)
                    && countMissing(recipe, stock) == 1) {
                almost.add(recipe);
            }
        }
        return almost;
    }

    private static PantryStock buildStock(List<Ingredient> pantry) {
        PantryStock stock = new PantryStock();
        for (Ingredient ing : pantry) {
            String key = IngredientNormalizer.normalizeName(ing.getName());
            double[] base = IngredientNormalizer.toBase(ing.getQuantity(), ing.getUnit());
            double[] existing = stock.byName.get(key);
            if (existing == null) {
                stock.byName.put(key, new double[]{base[0], base[1]});
            } else {
                existing[0] += base[0];
            }
        }
        return stock;
    }

    private static boolean recipeIsFullyCovered(Recipe recipe, PantryStock stock) {
        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (!ingredientCovered(ri, stock)) return false;
        }
        return true;
    }

    private static int countMissing(Recipe recipe, PantryStock stock) {
        int missing = 0;
        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (!ingredientCovered(ri, stock)) missing++;
        }
        return missing;
    }

    private static boolean ingredientCovered(RecipeIngredient ri, PantryStock stock) {
        String key = IngredientNormalizer.normalizeName(ri.getIngredientName());
        double[] available = stock.byName.get(key);
        if (available == null) return false;
        return IngredientNormalizer.isQuantitySufficient(
                available[0], baseUnitForCategory(available[1]),
                ri.getQuantity(), ri.getUnit());
    }

    private static String baseUnitForCategory(double categoryCode) {
        if (categoryCode == 1) return "g";
        if (categoryCode == 2) return "ml";
        if (categoryCode == 3) return "ml";
        return "whole";
    }
}
