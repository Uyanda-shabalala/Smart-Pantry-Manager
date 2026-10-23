# Smart Pantry Manager

Smart Pantry Manager is a Java Android app for Mobile App Development 700. The user adds what they have in the pantry , then the app checks which recipes they can make with those ingredients and quantities.

## What is implemented

- Add, view, edit and delete pantry ingredients in SQLite. Each item has a name, quantity, unit and optional expiry date.
- Five screens: Pantry, Add/Edit Ingredient, Suggested Recipes, Recipe Detail and Settings.
- Fifteen database recipes with quantities and ordered preparation steps, seeded when the recipe schema is installed.
- Strict recipe matching. Every ingredient must be present in enough quantity before the recipe is shown.
- Recipe suggestions reload when the user returns to the screen, so pantry changes are checked again.
- Name matching ignores case and extra spaces, and handles common plurals. Original names are kept for display.
- Compatible units are converted before matching: kg to g and L to ml. Pieces, mass and volume stay separate.
- Duplicate pantry entries are added together. Repeated recipe requirements are also added together so the same stock is not used twice.
- Settings are saved with SharedPreferences. Choose pantry amounts in g/ml or their original units, and turn the in-app expiry summary on or off.
- The expiry summary counts pantry entries due today or within two days. It is hidden when disabled or when the count is zero.

The expiry summary appears inside the pantry; it does not schedule phone notifications. Expired entries are not included in that summary. Expiry does not currently change recipe eligibility. Plural rules are practical heuristics and do not correct spelling mistakes or understand every English ingredient name.

The old decorative search bar, filter chips, account rows and sample-preview switch have been removed. Search and category filtering are not implemented.

## Database choice and design

SQLite was chosen because this app needs local storage and can work offline. SQLiteOpenHelper also lets the project demonstrate database creation, upgrades and CRUD directly without setting up a server.

The database is `smart_pantry.db`, schema version 2:

| Table | Stores |
| --- | --- |
| ingredients | Pantry ID, name, quantity, original unit and optional ISO expiry date |
| recipes | Recipe ID, name, cooking time and servings |
| recipe_ingredients | Recipe ID, ingredient position, name, quantity and unit |
| recipe_steps | Recipe ID, step position and instruction |

Recipe child tables reference `recipes`, with cascading deletion. First installation and the version-1 upgrade seed the recipe tables inside SQLiteOpenHelper's transaction. The upgrade preserves pantry records. Reopening the database does not reseed it. Settings are stored separately in SharedPreferences.

There are no Maps, GPS, payment or cloud-service features.

## Open and run

1. Open the folder containing `settings.gradle.kts` in Android Studio.
2. Install Android SDK 37 and allow Gradle sync. This project uses AGP 9.3.3 and Gradle 9.5.0. The daemon configuration requests JDK 25; application source compatibility is Java 11.
3. Select an emulator or phone running Android 9 / API 28 or later.
4. Run the `app` configuration. Use the bottom navigation for Pantry, Recipes and Settings.
5. Add a pantry item, tap its card to edit it, or use Delete and confirm the dialog.

A fresh pantry is empty. For a simple matching example, add tomato 3 pcs and olive oil 15 ml. Simple tomato salad should qualify. Reduce tomato to 2 pcs and return to Recipes; that salad should disappear. These are manual verification steps, not a claim that they have been executed on the current app.

## How the main code fits together

- `DBHelper`: database schema, migration, recipe loading and pantry CRUD.
- `RecipeSeeds`: the initial fifteen recipes and their ingredients and steps.
- `RecipeMatcher`: name normalization and the all-ingredients quantity check. Decimal arithmetic avoids binary rounding shortages when adding amounts.
- `IngredientUnits`: compatible-unit conversion and pantry amount formatting.
- `RecipesActivity`: reloads data on resume, checks eligibility on a background executor and displays qualifying cards.
- `PantryActivity`: refreshes on resume, shows database items and the optional expiry summary.
- `IngredientActivity`: validates input and saves or deletes through the database executor.
- `AppSettings` and `SettingsActivity`: saved preferences and their controls.
- `ExpirySummary`: counts dates from today through two days ahead.
- `BaseActivity` and `CardAdapter`: shared navigation, window insets, card binding and click actions.

Pantry items and recipes come from SQLite. Settings come from SharedPreferences; the old in-memory prototype classes have been removed.

## Verification

Run from the project folder in PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug
```

Gradle may need network access for missing distributions or dependencies. Automated test sources and their dependencies have been removed for now. Use build/lint checks and manual checks on an emulator or phone; a successful build does not prove runtime behaviour.

Before submitting, check pantry CRUD, persistence after reopening, recipe changes after pantry edits, saved settings and screen navigation on a device. The existing screenshots are from earlier milestones and do not prove the current app works on a device.

## Known issues still to check

- Bottom navigation can keep the destination tab selected on the Activity being left. When that Activity is reused, the wrong tab may be highlighted and a repeat tap may be ignored. The shared navigation code still needs a correction and device check.
- Saving an ingredient and rotating while the write is in progress may leave a new editor without the pending save result. This needs a device check and retained save state to prevent a possible duplicate save.
- Current device persistence, saved settings, navigation and layout checks are not complete. A successful build does not prove these interactions work.

These are findings from code inspection. They have not been reproduced on a current device.

## Repository and submission

Repository: https://github.com/Uyanda-shabalala/Smart-Pantry-Manager

Make sure the marker can access the final implementation branch. The report, narrated video, current screenshots and submission ZIP must be checked separately; this README is not the written assignment report.
