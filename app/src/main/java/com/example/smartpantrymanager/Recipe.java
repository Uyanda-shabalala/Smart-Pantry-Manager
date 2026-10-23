package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DecimalFormat;

// hold the recipe details , its ingredients and the steps in the order they should be followed.
public final class Recipe {
    public final long id;
    public final String name;
    public final int minutes;
    public final int servings;
    public final List<Requirement> ingredients;
    public final List<String> steps;

    Recipe(long id, String name, int minutes, int servings,
           List<Requirement> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.minutes = minutes;
        this.servings = servings;
        this.ingredients = Collections.unmodifiableList(new ArrayList<>(ingredients));
        this.steps = Collections.unmodifiableList(new ArrayList<>(steps));
    }

    public String ingredientsText() {
        StringBuilder text = new StringBuilder();
        DecimalFormat format = new DecimalFormat("0.###");
        for (Requirement item : ingredients) {
            if (text.length() > 0) text.append('\n');
            text.append(item.name).append(" — ").append(format.format(item.quantity))
                    .append(' ').append(item.unit);
        }
        return text.toString();
    }

    public String methodText() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            if (i > 0) text.append("\n\n");
            text.append(i + 1).append(". ").append(steps.get(i));
        }
        return text.toString();
    }

    public static final class Requirement {
        public final String name;
        public final double quantity;
        public final String unit;

        Requirement(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}
