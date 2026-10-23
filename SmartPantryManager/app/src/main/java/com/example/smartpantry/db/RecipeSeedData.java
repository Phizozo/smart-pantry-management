package com.example.smartpantry.db;

import com.example.smartpantry.model.Recipe;
import com.example.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/** 18 seed recipes (Section 2.2 requires 15-20), loaded on first run. */
final class RecipeSeedData {

    private RecipeSeedData() { }

    /** Ingredients are passed as (name, quantity, unit) triples. */
    private static Recipe recipe(String name, String steps, Object... triples) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setSteps(steps);
        for (int i = 0; i < triples.length; i += 3) {
            recipe.addIngredient(new RecipeIngredient(0, 0,
                    (String) triples[i], (Double) triples[i + 1], (String) triples[i + 2]));
        }
        return recipe;
    }

    static List<Recipe> buildSeedRecipes() {
        List<Recipe> r = new ArrayList<>();

        r.add(recipe("Scrambled Eggs on Toast",
                "1. Whisk eggs with a splash of milk.\n2. Melt butter in a pan and scramble the eggs on low heat.\n3. Toast the bread.\n4. Serve the eggs on the toast.",
                "eggs", 2.0, "", "milk", 30.0, "ml", "bread", 2.0, "", "butter", 10.0, "g"));

        r.add(recipe("Tomato Pasta",
                "1. Boil the pasta until al dente.\n2. Saute garlic in olive oil.\n3. Add chopped tomatoes and simmer 10 minutes.\n4. Toss the pasta through the sauce.",
                "pasta", 200.0, "g", "tomato", 3.0, "", "garlic", 2.0, "", "olive oil", 15.0, "ml"));

        r.add(recipe("Grilled Cheese Sandwich",
                "1. Butter one side of each bread slice.\n2. Put cheese between the unbuttered sides.\n3. Grill on medium heat until golden on both sides.",
                "bread", 2.0, "", "cheese", 60.0, "g", "butter", 10.0, "g"));

        r.add(recipe("Vegetable Stir Fry",
                "1. Heat oil in a wok.\n2. Stir-fry onion, carrot and bell pepper for 5 minutes.\n3. Add soy sauce and cook 2 more minutes.",
                "onion", 1.0, "", "carrot", 2.0, "", "bell pepper", 1.0, "", "soy sauce", 30.0, "ml", "oil", 15.0, "ml"));

        r.add(recipe("Chicken Rice Bowl",
                "1. Cook the rice.\n2. Season and pan-fry the chicken breast until cooked through.\n3. Slice the chicken and serve over the rice.",
                "rice", 200.0, "g", "chicken breast", 250.0, "g", "salt", 5.0, "g"));

        r.add(recipe("Simple Omelette",
                "1. Whisk eggs with salt.\n2. Pour into a hot, buttered pan.\n3. Add grated cheese, fold in half once set, and serve.",
                "eggs", 3.0, "", "cheese", 40.0, "g", "butter", 10.0, "g", "salt", 2.0, "g"));

        r.add(recipe("Potato Salad",
                "1. Boil the potatoes until tender, cool and cube them.\n2. Mix with mayonnaise, chopped onion and salt.",
                "potato", 500.0, "g", "mayonnaise", 100.0, "g", "onion", 1.0, "", "salt", 3.0, "g"));

        r.add(recipe("Fried Rice",
                "1. Scramble the eggs in oil, set aside.\n2. Stir-fry the rice with soy sauce and onion.\n3. Fold the eggs back in and serve.",
                "rice", 300.0, "g", "eggs", 2.0, "", "soy sauce", 30.0, "ml", "onion", 1.0, "", "oil", 15.0, "ml"));

        r.add(recipe("Banana Pancakes",
                "1. Mash the banana and mix with flour, milk and egg into a batter.\n2. Pour rounds onto a hot buttered pan, cook until bubbles form, then flip.",
                "banana", 2.0, "", "flour", 150.0, "g", "milk", 200.0, "ml", "eggs", 1.0, "", "butter", 10.0, "g"));

        r.add(recipe("Garlic Butter Mushrooms",
                "1. Melt butter in a pan.\n2. Add sliced mushrooms and minced garlic.\n3. Cook until golden, season and serve.",
                "mushroom", 250.0, "g", "garlic", 3.0, "", "butter", 20.0, "g", "salt", 2.0, "g"));

        r.add(recipe("Cheesy Baked Potatoes",
                "1. Bake the potatoes until soft.\n2. Cut open and top with butter and cheese.\n3. Return to the oven until the cheese melts.",
                "potato", 4.0, "", "cheese", 100.0, "g", "butter", 20.0, "g"));

        r.add(recipe("Carrot and Ginger Soup",
                "1. Saute onion and ginger in a pot.\n2. Add carrots and stock, simmer until soft.\n3. Blend until smooth and season.",
                "carrot", 500.0, "g", "onion", 1.0, "", "ginger", 20.0, "g", "vegetable stock", 500.0, "ml"));

        r.add(recipe("Classic PB&J Sandwich",
                "1. Spread peanut butter on one slice of bread.\n2. Spread jam on the other.\n3. Press together and slice in half.",
                "bread", 2.0, "", "peanut butter", 30.0, "g", "jam", 30.0, "g"));

        r.add(recipe("Egg Fried Noodles",
                "1. Cook the noodles and drain.\n2. Scramble the eggs in a hot pan.\n3. Add noodles and soy sauce, toss and serve.",
                "noodles", 200.0, "g", "eggs", 2.0, "", "soy sauce", 20.0, "ml", "oil", 10.0, "ml"));

        r.add(recipe("Tomato and Onion Salad",
                "1. Slice the tomatoes and onion thinly.\n2. Toss with olive oil and salt.",
                "tomato", 3.0, "", "onion", 1.0, "", "olive oil", 15.0, "ml", "salt", 2.0, "g"));

        r.add(recipe("Rice and Beans",
                "1. Cook the rice.\n2. Warm the beans with onion and garlic in a pan.\n3. Serve the beans over the rice.",
                "rice", 200.0, "g", "beans", 250.0, "g", "onion", 1.0, "", "garlic", 2.0, ""));

        r.add(recipe("Creamy Mushroom Pasta",
                "1. Cook the pasta.\n2. Saute mushrooms and garlic in butter.\n3. Stir in milk and simmer until slightly thick.\n4. Toss through the pasta.",
                "pasta", 200.0, "g", "mushroom", 200.0, "g", "garlic", 2.0, "", "butter", 15.0, "g", "milk", 100.0, "ml"));

        r.add(recipe("Banana Smoothie",
                "1. Add banana, milk and peanut butter to a blender.\n2. Blend until smooth and serve chilled.",
                "banana", 2.0, "", "milk", 250.0, "ml", "peanut butter", 15.0, "g"));

        return r;
    }
}
