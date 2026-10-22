package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Persistent pantry and recipe storage. Call these methods on a background thread. */
public final class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE = "ingredients";
    private static final String[] COLUMNS = {"_id", "name", "quantity", "unit", "expiry"};

    public DBHelper(Context context) {
        this(context, DATABASE_NAME);
    }

    // Allows instrumented tests to use a separate database.
    DBHelper(Context context, String databaseName) {
        super(context.getApplicationContext(), databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL CHECK(length(trim(name)) > 0), "
                + "quantity REAL NOT NULL CHECK(quantity > 0 AND quantity <= 1000000), "
                + "unit TEXT NOT NULL CHECK(length(trim(unit)) > 0), "
                + "expiry TEXT NOT NULL DEFAULT '')");
        createRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            createRecipes(db);
        } else {
            throw new IllegalStateException("Missing database migration: " + oldVersion + " to " + newVersion);
        }
    }

    // SQLiteOpenHelper runs creation/upgrades in a transaction, including all seed inserts.
    private static void createRecipes(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE recipes (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE CHECK(length(trim(name)) > 0), "
                + "minutes INTEGER NOT NULL CHECK(minutes > 0), "
                + "servings INTEGER NOT NULL CHECK(servings > 0))");
        db.execSQL("CREATE TABLE recipe_ingredients (recipe_id INTEGER NOT NULL "
                + "REFERENCES recipes(_id) ON DELETE CASCADE, position INTEGER NOT NULL CHECK(position > 0), "
                + "name TEXT NOT NULL CHECK(length(trim(name)) > 0), "
                + "quantity REAL NOT NULL CHECK(quantity > 0), "
                + "unit TEXT NOT NULL CHECK(unit IN ('g', 'ml', 'pcs')), "
                + "PRIMARY KEY(recipe_id, position))");
        db.execSQL("CREATE TABLE recipe_steps (recipe_id INTEGER NOT NULL "
                + "REFERENCES recipes(_id) ON DELETE CASCADE, position INTEGER NOT NULL CHECK(position > 0), "
                + "instruction TEXT NOT NULL CHECK(length(trim(instruction)) > 0), "
                + "PRIMARY KEY(recipe_id, position))");
        RecipeSeeds.insertInto(db);
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("recipes", null, null, null,
                null, null, "_id ASC")) {
            while (cursor.moveToNext()) recipes.add(readRecipe(cursor));
        }
        return recipes;
    }

    /** Returns null for an unknown recipe ID. */
    public Recipe getRecipe(long id) {
        try (Cursor cursor = getReadableDatabase().query("recipes", null, "_id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readRecipe(cursor) : null;
        }
    }

    private Recipe readRecipe(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        List<Recipe.Requirement> requirements = new ArrayList<>();
        List<String> steps = new ArrayList<>();
        String[] args = {String.valueOf(id)};
        try (Cursor items = getReadableDatabase().query("recipe_ingredients",
                new String[]{"name", "quantity", "unit"}, "recipe_id = ?", args,
                null, null, "position ASC")) {
            while (items.moveToNext()) requirements.add(new Recipe.Requirement(
                    items.getString(0), items.getDouble(1), items.getString(2)));
        }
        try (Cursor items = getReadableDatabase().query("recipe_steps",
                new String[]{"instruction"}, "recipe_id = ?", args, null, null, "position ASC")) {
            while (items.moveToNext()) steps.add(items.getString(0));
        }
        return new Recipe(id, cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getInt(cursor.getColumnIndexOrThrow("minutes")),
                cursor.getInt(cursor.getColumnIndexOrThrow("servings")), requirements, steps);
    }

    /** Create an ingredient and return its generated ID; throws if saving fails. */
    public long addIngredient(String name, double quantity, String unit, String expiry) {
        return getWritableDatabase().insertOrThrow(TABLE, null, values(name, quantity, unit, expiry));
    }

    /** Read one ingredient, or null when its ID does not exist. */
    public Ingredient getIngredient(long id) {
        try (Cursor cursor = getReadableDatabase().query(TABLE, COLUMNS, "_id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readIngredient(cursor) : null;
        }
    }

    /** Read all ingredients in insertion order. An empty pantry returns an empty list. */
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE, COLUMNS, null, null,
                null, null, "_id ASC")) {
            while (cursor.moveToNext()) ingredients.add(readIngredient(cursor));
        }
        return ingredients;
    }

    /** Update an existing ingredient; returns false if the ID no longer exists. */
    public boolean updateIngredient(long id, String name, double quantity, String unit, String expiry) {
        return getWritableDatabase().update(TABLE, values(name, quantity, unit, expiry),
                "_id = ?", new String[]{String.valueOf(id)}) == 1;
    }

    /** Delete one ingredient; returns false if the ID no longer exists. */
    public boolean deleteIngredient(long id) {
        return getWritableDatabase().delete(TABLE, "_id = ?",
                new String[]{String.valueOf(id)}) == 1;
    }

    private static ContentValues values(String name, double quantity, String unit, String expiry) {
        if (name == null || name.trim().isEmpty() || name.trim().length() > 80)
            throw new IllegalArgumentException("Ingredient name must contain 1 to 80 characters");
        if (!Double.isFinite(quantity) || quantity <= 0 || quantity > 1000000)
            throw new IllegalArgumentException("Quantity must be positive and at most 1000000");
        if (unit == null || unit.trim().isEmpty())
            throw new IllegalArgumentException("Unit is required");
        String date = expiry == null ? "" : expiry.trim();
        if (!date.isEmpty()) {
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}"))
                throw new IllegalArgumentException("Expiry must use YYYY-MM-DD");
            LocalDate.parse(date);
        }
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiry", date);
        return values;
    }

    private static Ingredient readIngredient(Cursor cursor) {
        return new Ingredient(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2),
                cursor.getString(3), cursor.getString(4));
    }
}
