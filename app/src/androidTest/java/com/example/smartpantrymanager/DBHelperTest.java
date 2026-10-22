package com.example.smartpantrymanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class DBHelperTest {
    private static final String TEST_DATABASE = "pantry_crud_test.db";
    private Context context;
    private DBHelper helper;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(TEST_DATABASE);
        helper = new DBHelper(context, TEST_DATABASE);
    }

    @After
    public void tearDown() {
        helper.close();
        context.deleteDatabase(TEST_DATABASE);
    }

    @Test
    public void crudPersistsAcrossReopeningAndOnlyChangesSelectedRow() {
        assertTrue(helper.getAllIngredients().isEmpty());
        long first = helper.addIngredient("Farmer's tomatoes", 4, "pcs", "2027-01-15");
        long second = helper.addIngredient("Pasta", 500, "g", "");
        assertTrue(first > 0);
        assertNotEquals(first, second);
        helper.close();
        helper = new DBHelper(context, TEST_DATABASE);
        assertEquals(2, helper.getAllIngredients().size());
        assertEquals("Farmer's tomatoes", helper.getIngredient(first).name);
        assertEquals("2027-01-15", helper.getIngredient(first).expiry);
        assertTrue(helper.updateIngredient(first, "Tomatoes", 2.5, "kg", ""));
        helper.close();
        helper = new DBHelper(context, TEST_DATABASE);
        Ingredient updated = helper.getIngredient(first);
        assertEquals("Tomatoes", updated.name);
        assertEquals(2.5, updated.quantity, 0.0);
        assertEquals("kg", updated.unit);
        assertEquals("", updated.expiry);
        assertEquals("Pasta", helper.getIngredient(second).name);
        assertTrue(helper.deleteIngredient(first));
        assertFalse(helper.deleteIngredient(first));
        assertFalse(helper.updateIngredient(first, "Missing", 1, "pcs", ""));
        helper.close();
        helper = new DBHelper(context, TEST_DATABASE);
        assertNull(helper.getIngredient(first));
        assertEquals(1, helper.getAllIngredients().size());
        assertTrue(helper.deleteIngredient(second));
        helper.close();
        helper = new DBHelper(context, TEST_DATABASE);
        assertTrue(helper.getAllIngredients().isEmpty());
    }

    @Test
    public void recipesAreCompleteAndSeededOnlyOnce() {
        assertEquals(15, helper.getAllRecipes().size());
        for (Recipe recipe : helper.getAllRecipes()) {
            assertTrue(recipe.minutes > 0);
            assertTrue(recipe.servings > 0);
            assertFalse(recipe.ingredients.isEmpty());
            assertFalse(recipe.steps.isEmpty());
            for (Recipe.Requirement ingredient : recipe.ingredients) {
                assertFalse(ingredient.name.isEmpty());
                assertTrue(ingredient.quantity > 0);
                assertTrue(java.util.Arrays.asList("g", "ml", "pcs").contains(ingredient.unit));
            }
            assertEquals(recipe.methodText(), helper.getRecipe(recipe.id).methodText());
        }
        Recipe pasta = helper.getAllRecipes().get(0);
        assertEquals("Tomato & olive oil pasta", pasta.name);
        assertEquals("pasta", pasta.ingredients.get(0).name);
        assertEquals(200, pasta.ingredients.get(0).quantity, 0);
        assertEquals(3, pasta.steps.size());
        assertNull(helper.getRecipe(-1));
        helper.close();
        helper = new DBHelper(context, TEST_DATABASE);
        assertEquals(15, helper.getAllRecipes().size());
        assertEquals(pasta.ingredientsText(), helper.getRecipe(pasta.id).ingredientsText());
        // Deletions must not be undone by reopening, and child rows must cascade.
        helper.getWritableDatabase().delete("recipes", "_id = ?", new String[]{String.valueOf(pasta.id)});
        try (Cursor cursor = helper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(pasta.id)})) {
            assertTrue(cursor.moveToFirst());
            assertEquals(0, cursor.getInt(0));
        }
        helper.close();
        helper = new DBHelper(context, TEST_DATABASE);
        assertEquals(14, helper.getAllRecipes().size());
        assertNull(helper.getRecipe(pasta.id));
    }

    @Test
    public void versionOneUpgradePreservesPantryAndSeedsRecipes() {
        helper.close();
        context.deleteDatabase(TEST_DATABASE);
        try (SQLiteDatabase old = context.openOrCreateDatabase(TEST_DATABASE, 0, null)) {
            old.execSQL("CREATE TABLE ingredients (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT NOT NULL DEFAULT '')");
            old.execSQL("INSERT INTO ingredients (_id, name, quantity, unit, expiry) "
                    + "VALUES (42, 'Pasta', 500, 'g', '2027-01-15')");
            old.setVersion(1);
        }
        helper = new DBHelper(context, TEST_DATABASE);
        assertEquals(15, helper.getAllRecipes().size());
        assertEquals(2, helper.getReadableDatabase().getVersion());
        Ingredient pantry = helper.getIngredient(42);
        assertEquals("Pasta", pantry.name);
        assertEquals(500, pantry.quantity, 0);
        assertEquals("g", pantry.unit);
        assertEquals("2027-01-15", pantry.expiry);
        assertEquals(1, helper.getAllIngredients().size());
        try (Cursor cursor = helper.getReadableDatabase().rawQuery("PRAGMA foreign_key_check", null)) {
            assertFalse(cursor.moveToFirst());
        }
        helper.close();
        helper = new DBHelper(context, TEST_DATABASE);
        assertEquals(15, helper.getAllRecipes().size());
    }

    @Test
    public void invalidInputDoesNotCreateRows() {
        assertThrows(IllegalArgumentException.class,
                () -> helper.addIngredient(" ", 1, "pcs", ""));
        assertThrows(IllegalArgumentException.class,
                () -> helper.addIngredient("Tomato", Double.NaN, "pcs", ""));
        assertThrows(IllegalArgumentException.class,
                () -> helper.addIngredient("Tomato", -1, "pcs", ""));
        assertThrows(IllegalArgumentException.class,
                () -> helper.addIngredient("Tomato", 1, "", ""));
        assertThrows(java.time.format.DateTimeParseException.class,
                () -> helper.addIngredient("Tomato", 1, "pcs", "2027-02-30"));
        assertTrue(helper.getAllIngredients().isEmpty());
    }
}
