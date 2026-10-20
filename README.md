# Smart Pantry Manager

An Android application (Java) that helps users reduce food waste by tracking pantry ingredients and suggesting recipes they can cook with what they already have.

## Features

- **Pantry CRUD** — add, edit, and delete ingredients (name, quantity, unit, expiry date)
- **Strict recipe matching** — a recipe is only suggested when *every* required ingredient is in the pantry in sufficient quantity
- **Almost There bonus list** — recipes missing exactly one ingredient (clearly separated)
- **18 pre-seeded recipes** stored in SQLite
- **Expiry alerts** — optional dialog warning when items are approaching expiry
- **Settings** — toggle alerts, units preference, expiry threshold
- **Persistent storage** — SQLite database survives app restarts

## Screens

| Screen | Activity | Purpose |
|---|---|---|
| Pantry List | `MainActivity` | RecyclerView of all ingredients |
| Add / Edit | `AddEditIngredientActivity` | Input form with validation |
| Suggested Recipes | `SuggestedRecipesActivity` | Strict matches + Almost There |
| Recipe Detail | `RecipeDetailActivity` | Full ingredients and method |
| Settings | `SettingsActivity` | User preferences (SharedPreferences) |

## Tech Stack

- **Language:** Java
- **IDE:** Android Studio (compileSdk 34, minSdk 26)
- **Database:** SQLite via `SQLiteOpenHelper`
- **UI:** Material Components, ConstraintLayout, RecyclerView
- **Navigation:** BottomNavigationView + explicit Intents

## Project Structure

```
app/src/main/java/com/example/smartpantry/
├── models/          # Ingredient, Recipe, RecipeIngredient (POJOs)
├── db/              # PantryDbHelper (schema + seed), PantryRepository (CRUD)
├── adapters/        # IngredientAdapter, RecipeAdapter
├── utils/           # IngredientNormalizer, RecipeMatcher (strict-matching logic)
└── *.java           # 5 Activities
```

## Building

1. Open the project in Android Studio
2. Let Gradle sync complete
3. Run on an emulator or device (API 26+)

## Assignment

Built for **Mobile App Development 700 — Practical Assignment**.  
See `REPORT.md` for the full written report.
