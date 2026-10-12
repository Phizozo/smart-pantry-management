package com.example.smartpantry.models;

import java.io.Serializable;

/**
 * A single ingredient line belonging to a recipe.
 *
 * Stored in the recipe_ingredients table as part of a one-to-many
 * relationship with {@link Recipe}. Each row records the ingredient name,
 * the quantity required, and the unit of measurement.
 *
 * @author Smart Pantry Manager
 */
public class RecipeIngredient implements Serializable {

    /** Primary key from the recipe_ingredients table. */
    private int id;

    /** Foreign key referencing the parent recipe's id. */
    private int recipeId;

    /** Canonical ingredient name as written in the seed data. */
    private String ingredientName;

    /** Quantity required by the recipe (e.g. 200). */
    private double quantity;

    /** Unit of the required quantity (e.g. "g", "whole", "tbsp"). */
    private String unit;

    public RecipeIngredient() {}

    /**
     * Constructs a recipe ingredient line (id is assigned by SQLite on insert).
     *
     * @param recipeId      owning recipe foreign key
     * @param ingredientName name of the ingredient
     * @param quantity      required amount
     * @param unit          required unit
     */
    public RecipeIngredient(int recipeId, String ingredientName, double quantity, String unit) {
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
