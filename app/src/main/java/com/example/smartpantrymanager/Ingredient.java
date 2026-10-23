package com.example.smartpantrymanager;

// hold the name , quantity , unit and expiry date for one pantry ingredient.
public final class Ingredient {
    public final long id;
    public final String name, unit, expiry;
    public final double quantity;

    public Ingredient(long id, String name, double quantity, String unit, String expiry) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }
}
