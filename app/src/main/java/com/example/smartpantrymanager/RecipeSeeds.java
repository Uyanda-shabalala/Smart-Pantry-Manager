package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/** Initial catalog. Only invoked when the recipe schema is first installed. */
final class RecipeSeeds {
    private RecipeSeeds() {}

    static void insertInto(SQLiteDatabase db) {
        add(db, "Tomato & olive oil pasta", 20, 2,
                new Recipe.Requirement[]{item("pasta", 200, "g"), item("tomato", 2, "pcs"), item("olive oil", 15, "ml"), item("water", 1000, "ml")},
                "Bring the water to a boil and cook the pasta according to its packet instructions.",
                "Chop the tomatoes. Warm the oil in a pan and soften the tomatoes.",
                "Drain the pasta, toss with the tomatoes and serve.");
        add(db, "Simple tomato salad", 10, 2,
                new Recipe.Requirement[]{item("tomato", 3, "pcs"), item("olive oil", 15, "ml")},
                "Wash and slice the tomatoes.", "Arrange on a plate and drizzle with olive oil.");
        add(db, "Warm tomato skillet", 15, 2,
                new Recipe.Requirement[]{item("tomato", 4, "pcs"), item("olive oil", 20, "ml")},
                "Wash and halve the tomatoes.", "Warm the oil in a skillet.", "Add tomatoes and cook gently until softened.");
        add(db, "Creamy porridge", 10, 2,
                new Recipe.Requirement[]{item("oats", 100, "g"), item("milk", 400, "ml")},
                "Combine the oats and milk in a saucepan.", "Simmer for 5 to 7 minutes, stirring until creamy.");
        add(db, "Banana porridge", 12, 2,
                new Recipe.Requirement[]{item("oats", 100, "g"), item("milk", 400, "ml"), item("banana", 2, "pcs")},
                "Simmer the oats and milk for 5 to 7 minutes, stirring frequently.", "Peel and slice the bananas, stir half into the porridge and use the rest as a topping.");
        add(db, "Scrambled eggs on toast", 10, 2,
                new Recipe.Requirement[]{item("egg", 4, "pcs"), item("butter", 15, "g"), item("bread", 4, "pcs")},
                "Toast the bread and beat the eggs in a bowl.", "Melt the butter in a pan, add the eggs and stir gently until set.", "Spoon the eggs onto the toast and serve.");
        add(db, "Cheese omelette", 10, 2,
                new Recipe.Requirement[]{item("egg", 4, "pcs"), item("cheese", 60, "g"), item("olive oil", 10, "ml")},
                "Beat the eggs and grate the cheese.", "Warm the oil in a frying pan, pour in the eggs and cook over low heat until almost set.", "Sprinkle with cheese, fold and continue cooking until the egg is fully set.");
        add(db, "Garlic pasta", 20, 2,
                new Recipe.Requirement[]{item("pasta", 200, "g"), item("garlic", 10, "g"), item("olive oil", 30, "ml"), item("water", 1000, "ml")},
                "Boil the water and cook the pasta according to its packet instructions.", "Peel and finely chop the garlic, then gently cook in the oil for 1 to 2 minutes without browning.", "Drain the pasta and toss with the garlic oil.");
        add(db, "Tomato rice", 25, 2,
                new Recipe.Requirement[]{item("rice", 150, "g"), item("tomato", 2, "pcs"), item("olive oil", 15, "ml"), item("water", 350, "ml")},
                "Chop the tomatoes and soften them in the oil in a saucepan.", "Stir in the rice and water and bring to a boil.", "Cover and simmer gently for 15 to 20 minutes until the rice is tender and the liquid absorbed.");
        add(db, "Pea rice", 25, 2,
                new Recipe.Requirement[]{item("rice", 150, "g"), item("peas", 150, "g"), item("water", 350, "ml"), item("butter", 15, "g")},
                "Bring the rice and water to a boil, cover and simmer gently for 10 minutes.", "Add the peas and cook covered for another 5 to 10 minutes until the rice is tender.", "Stir in the butter and serve.");
        add(db, "Mashed potatoes", 25, 2,
                new Recipe.Requirement[]{item("potato", 500, "g"), item("water", 1000, "ml"), item("milk", 100, "ml"), item("butter", 25, "g")},
                "Peel and dice the potatoes and place in a saucepan with the water.", "Bring to a boil and simmer for 15 to 20 minutes until tender, then drain.", "Mash with the milk and butter until smooth.");
        add(db, "Carrot soup", 30, 2,
                new Recipe.Requirement[]{item("carrot", 400, "g"), item("onion", 1, "pcs"), item("olive oil", 15, "ml"), item("water", 600, "ml")},
                "Peel and chop the carrots and onion.", "Soften the onion in the oil, add the carrots and water, and simmer for 20 minutes until tender.", "Allow to cool slightly, then blend carefully until smooth and reheat to serve.");
        add(db, "Chickpea tomato stew", 25, 2,
                new Recipe.Requirement[]{item("canned chickpeas", 240, "g"), item("tomato", 4, "pcs"), item("onion", 1, "pcs"), item("olive oil", 15, "ml"), item("water", 100, "ml")},
                "Drain the chickpeas, then peel and chop the onion and chop the tomatoes.", "Soften the onion in the oil. Add the tomatoes and water and simmer for 10 minutes.", "Add the chickpeas and simmer for another 10 minutes until hot throughout.");
        add(db, "Cucumber tomato salad", 10, 2,
                new Recipe.Requirement[]{item("cucumber", 200, "g"), item("tomato", 2, "pcs"), item("olive oil", 15, "ml"), item("lemon juice", 15, "ml")},
                "Wash and dice the cucumber and tomatoes.", "Toss with the olive oil and lemon juice and serve.");
        add(db, "Peanut butter banana toast", 5, 2,
                new Recipe.Requirement[]{item("bread", 4, "pcs"), item("peanut butter", 60, "g"), item("banana", 2, "pcs")},
                "Toast the bread and peel and slice the bananas.", "Spread the peanut butter over the toast and top with the banana slices.");
    }

    private static Recipe.Requirement item(String name, double quantity, String unit) {
        return new Recipe.Requirement(name, quantity, unit);
    }

    private static void add(SQLiteDatabase db, String name, int minutes, int servings,
                            Recipe.Requirement[] ingredients, String... steps) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("minutes", minutes);
        values.put("servings", servings);
        long id = db.insertOrThrow("recipes", null, values);
        for (int i = 0; i < ingredients.length; i++) {
            values.clear();
            values.put("recipe_id", id);
            values.put("position", i + 1);
            values.put("name", ingredients[i].name);
            values.put("quantity", ingredients[i].quantity);
            values.put("unit", ingredients[i].unit);
            db.insertOrThrow("recipe_ingredients", null, values);
        }
        for (int i = 0; i < steps.length; i++) {
            values.clear();
            values.put("recipe_id", id);
            values.put("position", i + 1);
            values.put("instruction", steps[i]);
            db.insertOrThrow("recipe_steps", null, values);
        }
    }
}
