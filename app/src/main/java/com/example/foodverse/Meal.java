package com.example.foodverse;

import static java.lang.Math.round;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Meal {
    ArrayList<Ingredient> ingredients;
    // Recipe recipe;
    Date date;

    public Meal() {

    }

    public Meal(ArrayList<Ingredient> ingredients, Date date) {
        this.ingredients = ingredients;
        this.date = date;
    }

    public int hashCode() {
        // TODO: Add recipe hashCode when complete
        int hash = 0;
        hash += ingredients.hashCode() + date.hashCode();
        return hash;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
