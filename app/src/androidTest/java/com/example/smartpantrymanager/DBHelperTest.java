package com.example.smartpantrymanager;

import android.content.Context;
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
