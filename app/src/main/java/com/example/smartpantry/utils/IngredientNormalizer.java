package com.example.smartpantry.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Normalises ingredient names and units so that simple real-world
 * differences ("tomato" vs "tomatoes", "g" vs "gram") still match.
 */
public final class IngredientNormalizer {

    private static final Map<String, String> ALIASES = new HashMap<>();
    static {
        ALIASES.put("tomatoes", "tomato");
        ALIASES.put("potatoes", "potato");
        ALIASES.put("carrots", "carrot");
        ALIASES.put("onions", "onion");
        ALIASES.put("eggs", "egg");
        ALIASES.put("cloves", "clove");
        ALIASES.put("slices", "slice");
        ALIASES.put("bananas", "banana");
        ALIASES.put("apples", "apple");
        ALIASES.put("strawberries", "strawberry");
        ALIASES.put("beans", "kidney bean");
        ALIASES.put("kidney beans", "kidney bean");
        ALIASES.put("chicken breasts", "chicken breast");
        ALIASES.put("chicken", "chicken breast");
        ALIASES.put("broccolis", "broccoli");
        ALIASES.put("cucumbers", "cucumber");
        ALIASES.put("lettuces", "lettuce");
        ALIASES.put("pasta sauces", "pasta");
        ALIASES.put("chillis", "chilli powder");
        ALIASES.put("chili powder", "chilli powder");
        ALIASES.put("yoghurts", "yoghurt");
        ALIASES.put("yogurt", "yoghurt");
        ALIASES.put("yogurts", "yoghurt");
        ALIASES.put("flours", "flour");
        ALIASES.put("milks", "milk");
        ALIASES.put("breads", "bread");
        ALIASES.put("butters", "butter");
        ALIASES.put("rices", "rice");
        ALIASES.put("salts", "salt");
        ALIASES.put("peppers", "black pepper");
        ALIASES.put("white pepper", "black pepper");
        ALIASES.put("vegetable oils", "vegetable oil");
        ALIASES.put("olive oils", "olive oil");
        ALIASES.put("cooking oil", "vegetable oil");
        ALIASES.put("oil", "vegetable oil");
        ALIASES.put("soy sauces", "soy sauce");
        ALIASES.put("soya sauce", "soy sauce");
        ALIASES.put("sugar", "sugar");
        ALIASES.put("sugars", "sugar");
    }

    private static final Map<String, String> UNIT_ALIASES = new HashMap<>();
    static {
        UNIT_ALIASES.put("g", "g");
        UNIT_ALIASES.put("gram", "g");
        UNIT_ALIASES.put("grams", "g");
        UNIT_ALIASES.put("kg", "kg");
        UNIT_ALIASES.put("kilo", "kg");
        UNIT_ALIASES.put("kilos", "kg");
        UNIT_ALIASES.put("kilogram", "kg");
        UNIT_ALIASES.put("kilograms", "kg");
        UNIT_ALIASES.put("ml", "ml");
        UNIT_ALIASES.put("millilitre", "ml");
        UNIT_ALIASES.put("millilitres", "ml");
        UNIT_ALIASES.put("milliliter", "ml");
        UNIT_ALIASES.put("milliliters", "ml");
        UNIT_ALIASES.put("l", "l");
        UNIT_ALIASES.put("litre", "l");
        UNIT_ALIASES.put("litres", "l");
        UNIT_ALIASES.put("liter", "l");
        UNIT_ALIASES.put("liters", "l");
        UNIT_ALIASES.put("tsp", "tsp");
        UNIT_ALIASES.put("teaspoon", "tsp");
        UNIT_ALIASES.put("teaspoons", "tsp");
        UNIT_ALIASES.put("tbsp", "tbsp");
        UNIT_ALIASES.put("tablespoon", "tbsp");
        UNIT_ALIASES.put("tablespoons", "tbsp");
        UNIT_ALIASES.put("cup", "cup");
        UNIT_ALIASES.put("cups", "cup");
        UNIT_ALIASES.put("clove", "clove");
        UNIT_ALIASES.put("cloves", "clove");
        UNIT_ALIASES.put("slice", "slice");
        UNIT_ALIASES.put("slices", "slice");
        UNIT_ALIASES.put("whole", "whole");
        UNIT_ALIASES.put("pinch", "pinch");
        UNIT_ALIASES.put("pinches", "pinch");
    }

    private IngredientNormalizer() {}

    /** Returns a canonical name for fuzzy-but-safe comparison. */
    public static String normalizeName(String raw) {
        if (raw == null) return "";
        String key = raw.trim().toLowerCase(Locale.ROOT);
        if (ALIASES.containsKey(key)) return ALIASES.get(key);
        if (key.endsWith("es") && ALIASES.containsKey(key.substring(0, key.length() - 2))) {
            return ALIASES.get(key.substring(0, key.length() - 2));
        }
        if (key.endsWith("s") && ALIASES.containsKey(key.substring(0, key.length() - 1))) {
            return ALIASES.get(key.substring(0, key.length() - 1));
        }
        return key;
    }

    /** Returns a canonical unit string. */
    public static String normalizeUnit(String raw) {
        if (raw == null) return "";
        String key = raw.trim().toLowerCase(Locale.ROOT);
        return UNIT_ALIASES.getOrDefault(key, key);
    }

    /**
     * Converts a quantity into a comparable base unit.
     * Weight base = g, volume base = ml, count base = whole.
     * Returns a double[]{valueInBaseUnit, categoryCode}
     * category: 0=count, 1=weight, 2=volume, 3=small-count (tsp/tbsp/cup/pinch)
     */
    public static double[] toBase(double quantity, String unit) {
        String u = normalizeUnit(unit);
        switch (u) {
            case "kg":   return new double[]{quantity * 1000.0, 1};
            case "g":    return new double[]{quantity, 1};
            case "l":    return new double[]{quantity * 1000.0, 2};
            case "ml":   return new double[]{quantity, 2};
            case "tsp":  return new double[]{quantity * 5.0, 3};
            case "tbsp": return new double[]{quantity * 15.0, 3};
            case "cup":  return new double[]{quantity * 240.0, 3};
            case "pinch":return new double[]{quantity, 3};
            default:     return new double[]{quantity, 0};
        }
    }

    /**
     * True when the pantry quantity (in pantryUnit) is sufficient
     * to cover the recipe requirement (in recipeUnit).
     */
    public static boolean isQuantitySufficient(
            double pantryQty, String pantryUnit,
            double recipeQty, String recipeUnit) {

        double[] pantry = toBase(pantryQty, pantryUnit);
        double[] recipe = toBase(recipeQty, recipeUnit);

        if (pantry[1] == recipe[1]) {
            return pantry[0] + 1e-9 >= recipe[0];
        }
        // Cross-category: count ingredients are compared by "at least one" presence
        if (pantry[1] == 0 || recipe[1] == 0) {
            return pantry[0] + 1e-9 >= recipe[0];
        }
        // Weight vs volume (or small-count vs weight) cannot be proven
        // sufficient, so treat as not sufficient (strict rule).
        return false;
    }
}
