package com.example.smartpantry.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a recipe stored in the database (e.g. "Tomato Pasta").
 *
 * Each recipe owns a list of {@link RecipeIngredient} objects that define
 * everything the user must already have in their pantry to cook it.
 * The recipe is seeded into SQLite on first run and is read-only at runtime.
 *
 * @author Smart Pantry Manager
 */
public class Recipe implements Serializable {

    /** Primary key from the recipes table. */
    private int id;

    /** Display name of the recipe. */
    private String name;

    /** Multi-line preparation method stored as a single string. */
    private String steps;

    /** Every ingredient this recipe requires, loaded via a JOIN on recipe_ingredients. */
    private List<RecipeIngredient> ingredients;

    public Recipe() {
        this.ingredients = new ArrayList<>();
    }

    /**
     * Constructs a recipe without its ingredient list (filled in later).
     *
     * @param id    database primary key
     * @param name  recipe display name
     * @param steps preparation method text
     */
    public Recipe(int id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.ingredients = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }

    /** Appends a single required ingredient to this recipe. */
    public void addIngredient(RecipeIngredient ri) { this.ingredients.add(ri); }
}
