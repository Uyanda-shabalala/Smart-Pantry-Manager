# Smart Pantry Manager

A Java Android app UI for Mobile App Development 700. The sage-green and cream interface helps users keep track of ingredients and browse recipe previews.

## Current UI and navigation

- Five XML screens: Pantry List, Add/Edit Ingredient, Suggested Recipes, Recipe Detail, and Settings.
- Java Activities with explicit Intents and ingredient/recipe identifiers.
- RecyclerView with a custom card adapter.
- Add, edit and confirmed delete operations on **in-memory sample data only**.
- Required name, positive finite quantity and valid optional ISO date validation.
- Recipe cards are explicitly labelled fixtures, not eligible recipe results. Switch off sample previews to inspect the empty-state design.
- Session-only settings controls; no alerts are scheduled.

## Open and run

1. Open this folder in Android Studio (the folder containing `settings.gradle.kts`).
2. Allow Gradle sync. The project retains the original AGP 9.3.3, Gradle 9.5.0, SDK 37 and Java 11 source settings; the existing Gradle daemon configuration requests JDK 25.
3. Ensure Android SDK 37 is installed. Select Android Studio's suitable Gradle JDK or let its configured toolchain resolve.
4. Choose an emulator or phone running Android 9/API 28 or later and run `app`.
5. Use the Pantry, Recipes and Settings bottom navigation. Tap an ingredient to edit it or a recipe to see its method.

