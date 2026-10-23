package com.example.smartpantry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartpantry.model.PantryItem;
import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Local JVM tests for the strict-matching rule (Section 2.3). */
public class IngredientMatcherTest {

    private Recipe recipe(Object... triples) {
        Recipe r = new Recipe();
        r.setName("Test");
        for (int i = 0; i < triples.length; i += 3) {
            r.addIngredient(new RecipeIngredient(0, 0, (String) triples[i], (Double) triples[i + 1], (String) triples[i + 2]));
        }
        return r;
    }

    private PantryItem item(String name, double qty, String unit) {
        return new PantryItem(0, name, qty, unit, "");
    }

    @Test
    public void allIngredientsPresent_matches() {
        Recipe r = recipe("pasta", 200.0, "g", "garlic", 2.0, "");
        List<PantryItem> pantry = Arrays.asList(item("Pasta", 500, "g"), item("Garlic", 3, ""));
        assertTrue(IngredientMatcher.pantryCoversRecipe(r, pantry));
    }

    @Test
    public void oneIngredientMissing_excluded() {
        Recipe r = recipe("eggs", 2.0, "", "milk", 30.0, "ml", "bread", 2.0, "", "butter", 10.0, "g", "salt", 1.0, "g");
        List<PantryItem> pantry = Arrays.asList(item("eggs", 6, ""), item("milk", 1000, "ml"),
                item("bread", 4, ""), item("butter", 100, "g"));
        assertFalse(IngredientMatcher.pantryCoversRecipe(r, pantry));
        assertEquals(1, IngredientMatcher.missingIngredients(r, pantry).size());
    }

    @Test
    public void pluralAndSingularNamesMatch() {
        Recipe r = recipe("tomato", 3.0, "");
        assertTrue(IngredientMatcher.pantryCoversRecipe(r, Arrays.asList(item("Tomatoes", 4, ""))));
        Recipe r2 = recipe("eggs", 2.0, "");
        assertTrue(IngredientMatcher.pantryCoversRecipe(r2, Arrays.asList(item("egg", 2, ""))));
    }

    @Test
    public void unitConversionKgVsGrams() {
        Recipe r = recipe("flour", 150.0, "g");
        assertTrue(IngredientMatcher.pantryCoversRecipe(r, Arrays.asList(item("flour", 1, "kg"))));
        assertFalse(IngredientMatcher.pantryCoversRecipe(r, Arrays.asList(item("flour", 0.1, "kg"))));
    }

    @Test
    public void insufficientQuantity_excluded() {
        Recipe r = recipe("rice", 300.0, "g");
        assertFalse(IngredientMatcher.pantryCoversRecipe(r, Arrays.asList(item("rice", 200, "g"))));
    }

    @Test
    public void emptyPantry_matchesNothing() {
        Recipe r = recipe("rice", 300.0, "g");
        assertTrue(IngredientMatcher.filterStrictMatches(Arrays.asList(r), new ArrayList<PantryItem>()).isEmpty());
    }

    @Test
    public void almostThere_onlyExactlyOneMissing() {
        Recipe r = recipe("rice", 300.0, "g", "onion", 1.0, "");
        List<PantryItem> pantry = Arrays.asList(item("rice", 500, "g"));
        assertEquals(1, IngredientMatcher.filterAlmostThere(Arrays.asList(r), pantry).size());
        assertEquals(0, IngredientMatcher.filterStrictMatches(Arrays.asList(r), pantry).size());
    }
}
