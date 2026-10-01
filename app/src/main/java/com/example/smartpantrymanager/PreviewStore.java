package com.example.smartpantrymanager;
import java.util.ArrayList;
import java.util.List;
/** UI milestone only: process-local sample data, deliberately not persistence. */
public final class PreviewStore {
 public static final class Ingredient {
  public final long id; public String name, unit, expiry; public double quantity;
  Ingredient(long id,String name,double quantity,String unit,String expiry) { this.id=id; this.name=name; this.quantity=quantity; this.unit=unit; this.expiry=expiry; }
 }
 public static final List<Ingredient> items=new ArrayList<>();
 private static long nextId=4;
 public static boolean alerts=false; public static int unitsPreference=0;
 static { items.add(new Ingredient(1,"Tomatoes",4,"pcs","")); items.add(new Ingredient(2,"Pasta",500,"g","")); items.add(new Ingredient(3,"Olive oil",250,"ml","")); }
 public static Ingredient find(long id) { for(Ingredient i:items) if(i.id==id) return i; return null; }
 public static void add(String n,double q,String u,String e) { items.add(new Ingredient(nextId++,n,q,u,e)); }
 private PreviewStore() {}
}
