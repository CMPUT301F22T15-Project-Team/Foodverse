package com.example.foodverse;

import static java.lang.Math.round;

import java.time.LocalDate;
import java.util.ArrayList;

public class Meal {
    ArrayList<Ingredient> ingredients;
    // Recipe recipe;
    LocalDate date;

    public Meal() {

    }

    public Meal(ArrayList<Ingredient> ingredients, LocalDate date) {
        this.ingredients = ingredients;
        this.date = date;
    }

    public int hashCode() {
        // TODO: Add recipe hashCode when complete
        int hash = 0;
        hash += ingredients.hashCode();
        return hash;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
