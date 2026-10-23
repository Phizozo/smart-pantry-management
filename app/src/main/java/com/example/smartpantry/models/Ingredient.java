package com.example.smartpantry.models;

import java.io.Serializable;

/**
 * Represents a single item in the user's pantry (e.g. "tomato", 500, "g").
 *
 * Implements {@link Serializable} so the object can be passed between
 * Activities via Intents using putExtra / getSerializableExtra.
 *
 * @author Smart Pantry Manager
 */
public class Ingredient implements Serializable {

    /** Primary key from the pantry table; -1 when not yet saved. */
    private int id;

    /** Human-readable ingredient name entered by the user. */
    private String name;

    /** How much of the ingredient the user has (must be > 0). */
    private double quantity;

    /** Measurement unit (whole, g, kg, ml, l, tsp, tbsp, cup, slice, clove, pinch). */
    private String unit;

    /** Optional expiry date in ISO format yyyy-MM-dd; empty string if unknown. */
    private String expiryDate;

    /** No-args constructor required for some serialisation paths. */
    public Ingredient() {}

    /**
     * Full constructor used when loading from the database.
     *
     * @param id          database primary key
     * @param name        ingredient name
     * @param quantity    amount on hand
     * @param unit        measurement unit
     * @param expiryDate  ISO date string or empty
     */
    public Ingredient(int id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    /**
     * Convenience constructor for new (unsaved) ingredients.
     * The id is set to -1 until the row is inserted and the key is assigned.
     */
    public Ingredient(String name, double quantity, String unit, String expiryDate) {
        this(-1, name, quantity, unit, expiryDate);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
