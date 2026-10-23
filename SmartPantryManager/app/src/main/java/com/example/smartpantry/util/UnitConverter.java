package com.example.smartpantry.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts common cooking units into one base unit per category
 * (mass -> grams, volume -> millilitres) so 0.5 kg and 500 g compare equal.
 * Anything else (blank, "clove", "pcs") is a COUNT unit and is only ever
 * compared with the identical unit string.
 */
public final class UnitConverter {

    public enum Category { MASS, VOLUME, COUNT }

    private static final Map<String, Double> MASS_TO_GRAMS = new HashMap<>();
    private static final Map<String, Double> VOLUME_TO_ML = new HashMap<>();

    static {
        MASS_TO_GRAMS.put("g", 1.0);
        MASS_TO_GRAMS.put("kg", 1000.0);
        MASS_TO_GRAMS.put("oz", 28.3495);
        MASS_TO_GRAMS.put("lb", 453.592);

        VOLUME_TO_ML.put("ml", 1.0);
        VOLUME_TO_ML.put("l", 1000.0);
        VOLUME_TO_ML.put("tsp", 4.92892);
        VOLUME_TO_ML.put("tbsp", 14.7868);
        VOLUME_TO_ML.put("cup", 236.588);
        VOLUME_TO_ML.put("fl_oz", 29.5735);
    }

    private UnitConverter() { }

    /** Spelling variants mapped to the canonical unit ("" = plain count). */
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        String[][] pairs = {
                {"gram", "g"}, {"grams", "g"}, {"gr", "g"},
                {"kilogram", "kg"}, {"kilograms", "kg"}, {"kgs", "kg"},
                {"ounce", "oz"}, {"ounces", "oz"},
                {"pound", "lb"}, {"pounds", "lb"}, {"lbs", "lb"},
                {"milliliter", "ml"}, {"milliliters", "ml"}, {"millilitre", "ml"}, {"millilitres", "ml"},
                {"liter", "l"}, {"liters", "l"}, {"litre", "l"}, {"litres", "l"},
                {"teaspoon", "tsp"}, {"teaspoons", "tsp"},
                {"tablespoon", "tbsp"}, {"tablespoons", "tbsp"},
                {"cups", "cup"},
                {"pc", ""}, {"pcs", ""}, {"piece", ""}, {"pieces", ""}, {"whole", ""},
                {"unit", ""}, {"units", ""}
        };
        for (String[] p : pairs) ALIASES.put(p[0], p[1]);
    }

    /** Trim, lowercase and resolve aliases, e.g. "Grams" -> "g", "pcs" -> "". */
    public static String normalizeUnit(String unit) {
        if (unit == null) return "";
        String u = unit.trim().toLowerCase();
        String alias = ALIASES.get(u);
        return alias != null ? alias : u;
    }

    private static String clean(String unit) {
        return normalizeUnit(unit);
    }

    public static Category categoryOf(String unit) {
        String u = clean(unit);
        if (MASS_TO_GRAMS.containsKey(u)) return Category.MASS;
        if (VOLUME_TO_ML.containsKey(u)) return Category.VOLUME;
        return Category.COUNT;
    }

    /** Converts quantity+unit into the category base unit (COUNT is unchanged). */
    public static double toBaseUnit(double quantity, String unit) {
        String u = clean(unit);
        switch (categoryOf(u)) {
            case MASS:   return quantity * MASS_TO_GRAMS.get(u);
            case VOLUME: return quantity * VOLUME_TO_ML.get(u);
            default:     return quantity;
        }
    }

    /** True if two units can be meaningfully compared. */
    public static boolean comparable(String unitA, String unitB) {
        Category a = categoryOf(unitA);
        if (a != categoryOf(unitB)) return false;
        return a != Category.COUNT || clean(unitA).equals(clean(unitB));
    }
}
