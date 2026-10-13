# Smart Pantry Manager

A Java Android app UI for Mobile App Development 700. The sage-green and cream interface helps users keep track of ingredients and browse recipe previews.

## Current milestone: UI and navigation

- Five XML screens: Pantry List, Add/Edit Ingredient, Suggested Recipes, Recipe Detail, and Settings.
- Java Activities with explicit Intents and ingredient/recipe identifiers.
- RecyclerView with a custom card adapter.
- Add, edit and confirmed delete operations on **in-memory sample data only**.
- Required name, positive finite quantity and valid optional ISO date validation.
- Recipe cards are explicitly labelled fixtures, not eligible recipe results. Switch off sample previews to inspect the empty-state design.
- Session-only settings controls; no alerts are scheduled.
- No Kotlin application source. Existing `.gradle.kts` files are build configuration, not Kotlin app implementation.

**Not implemented:** SQLite persistence, strict matching, the required 15–20 database recipes, real expiry notifications, or a completed assignment submission. Process death resets sample data and preferences. Rotation preserves the ingredient form through Android view state.

## Open and run

1. Open this folder in Android Studio (the folder containing `settings.gradle.kts`).
2. Allow Gradle sync. The project retains the original AGP 9.3.3, Gradle 9.5.0, SDK 37 and Java 11 source settings; the existing Gradle daemon configuration requests JDK 25.
3. Ensure Android SDK 37 is installed. Select Android Studio's suitable Gradle JDK or let its configured toolchain resolve.
4. Choose an emulator or phone running Android 9/API 28 or later and run `app`.
5. Use the Pantry, Recipes and Settings bottom navigation. Tap an ingredient to edit it or a recipe to see its method.

Checks: `gradlew.bat :app:assembleDebug :app:lintDebug --no-configuration-cache`.

## Understand the code

`BaseActivity` inflates the shared toolbar, preview banner, screen container and bottom navigation. It applies system-bar and keyboard insets. `PantryActivity.onResume()` rebuilds the displayed list after returning from the editor. `IngredientActivity` validates input before updating `PreviewStore`. Intent extras pass identifiers, not entire objects. `CardAdapter` binds each row to the shared XML card and its click action. `RecipeSamples` is deliberately separate from future matching logic.

Layouts are under `app/src/main/res/layout`; colours, strings and the shared theme are under `res/values`. Form and detail screens scroll, text uses sp, controls have generous touch targets, and fields have visible labels. Light colours are intentionally retained in system dark mode for this milestone.

## Next stages of the assignment

1. **SQLite persistence:** introduce `SQLiteOpenHelper`, schema versioning and a repository layer. Store ingredients, recipes and recipe requirements in separate tables with stable IDs and foreign keys. Replace `PreviewStore` with queries; prove create/read/update/delete survives force-stop and relaunch. SQLite is the chosen next milestone because it works offline and directly demonstrates the module's local database concepts.
2. **Recipe dataset:** seed 15–20 complete recipes once, transactionally. Store ingredient amounts, canonical units and ordered preparation steps. Do not reseed deleted user data at every launch.
3. **Deterministic matching:** require every recipe ingredient and sufficient quantity. Normalize names with a small explicit alias map (e.g. tomatoes → tomato), convert compatible units (kg/g, L/ml), aggregate duplicate pantry amounts, and reject missing, insufficient or incompatible units. Do not assume water, salt or oil is available. Keep partial matches out of the main suggestions.
4. **Verification:** test missing-one-ingredient, insufficient amount, exact amount, plural aliases, unit conversion, duplicates, incompatible units and empty pantry. Also test database reopening, rotation, keyboard visibility, large fonts and narrow screens.
5. **GitHub:** publish only after owner approval. Continue genuine incremental commits throughout later work; the assignment requires at least ten meaningful commits over development. These local UI commits are one milestone, not a substitute for that timeline.
6. **Evidence:** capture real running-app screenshots for each screen and CRUD/error state. Record a narrated 5–7 minute video covering Git history, app features and persistence, strict-match changes, three code concepts and the SQLite choice.
7. **Report and submission:** include the specified cover, contents, introduction, screen-flow and data diagrams, captioned real screenshots, 3–5 explained code excerpts, actual challenges, reflection and Harvard references. Use the assignment's formatting and filenames. Package the project, report and actual video in one ZIP under 50 MB, excluding build caches. Check the submission portal for the due date.

An LLM is not needed for the assessed matching rule. If considered later as an optional recipe-authoring aid, generated recipes must be checked and stored as structured data; deterministic quantity validation must remain the authority for eligibility. Confirm any AI-use disclosure requirements with your lecturer and understand the code you submit.

## Manual UI checks

- Add an ingredient; return to the pantry and verify its amount and unit.
- Edit it, rotate while editing, then save; confirm only that item changed.
- Try an empty name, zero quantity and `2026-02-30`; expect field errors.
- Cancel deletion, then confirm deletion. Delete every item to see the empty pantry.
- Open all three sample recipe cards and use toolbar/system Back.
- Toggle sample recipe previews off and on; the empty display remains labelled as a preview.
- Change session settings; navigate away and back.
- Force-stop and reopen: sample data resets, as explicitly stated by the UI.

No remote repository was created or published during this milestone.
