# Smart Pantry Manager

An Android app (Java) that helps reduce food waste. The user records the ingredients they
have at home, and the app suggests only the recipes they can cook **right now** using
those ingredients - no shopping trip required.

Built for Mobile App Development 700 - Practical Assignment.

## Features
- Pantry management: add, edit, delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list (RecyclerView + custom adapter) bound to the SQLite database
- 18 pre-loaded recipes, seeded on first run
- **Strict matching**: a recipe is suggested only if *every* ingredient is in the pantry in at least the required quantity
- Robust matching: case-insensitive, singular/plural ("tomato"/"tomatoes"), unit conversion (kg/g, l/ml, cups, tbsp, tsp, oz, lb)
- Recipe detail screen (ingredients + method)
- "Almost There" tab (stretch goal): recipes missing exactly one ingredient, kept separate from strict matches
- Settings screen (expiring-alert toggle, unit preference), persisted in the database
- Empty-state messages when the pantry is empty or no recipes match
- Bottom navigation bar, input validation, no Maps/GPS/location features

## Database choice: SQLite (SQLiteOpenHelper)
SQLite runs locally on-device with no network or backend, so the app works offline and data
genuinely persists after the app is closed. It is covered in the module's persistent-data
chapter, and the data (a few small related tables) fits a relational model well:
`pantry_items`, `recipes`, `recipe_ingredients` (FK to recipes), `settings`.

## Project structure
```
app/src/main/java/com/example/smartpantry/
  activity/  PantryListActivity, AddEditIngredientActivity, SuggestedRecipesActivity,
             RecipeDetailActivity, SettingsActivity
  adapter/   PantryAdapter, RecipeAdapter
  db/        PantryDbHelper, PantryContract, PantryDao, RecipeDao, SettingsDao, RecipeSeedData
  model/     PantryItem, Recipe, RecipeIngredient
  util/      IngredientMatcher (core strict-matching logic), UnitConverter
app/src/test/  IngredientMatcherTest (JUnit tests of the matching rule)
```

## Setup / run instructions
1. Install Android Studio (Hedgehog or newer) with Android SDK 34.
2. Clone: `git clone <your-repo-url>` and choose **File > Open** on the project folder.
3. Let Gradle sync (Android Studio will generate the Gradle wrapper jar if missing).
4. Select an emulator (API 24+) or a USB-debugging device and press **Run**.
5. Run the logic tests: right-click `IngredientMatcherTest` > Run, or `./gradlew test`.

## How strict matching works
`IngredientMatcher.missingIngredients()` normalises each name, converts quantities to a base
unit (grams / millilitres), and lists every recipe ingredient that is absent, in an
incompatible unit, or in insufficient quantity. A recipe is suggested only when that list is empty.
