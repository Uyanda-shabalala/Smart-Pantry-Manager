package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Persistent ingredient CRUD. Call these methods on a background thread. */
public final class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;
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
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL CHECK(length(trim(name)) > 0), "
                + "quantity REAL NOT NULL CHECK(quantity > 0 AND quantity <= 1000000), "
                + "unit TEXT NOT NULL CHECK(length(trim(unit)) > 0), "
                + "expiry TEXT NOT NULL DEFAULT '')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add explicit, data-preserving migrations when incrementing DATABASE_VERSION.
        throw new IllegalStateException("Missing database migration: " + oldVersion + " to " + newVersion);
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
