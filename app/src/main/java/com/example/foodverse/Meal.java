package com.example.foodverse;

import static java.lang.Math.round;

import java.util.ArrayList;

public class Meal {
    ArrayList<Ingredient> ingredients;
    // Recipe recipe;
    float time;
    float numberOfServings;

    public Meal(float time, float numberOfServings) {
        this.time = time;
        this.numberOfServings = numberOfServings;
        // this.recipe = recipe;
        this.ingredients = new ArrayList<Ingredient>();
    }

    public Meal(float time, float numberOfServings,
                ArrayList<Ingredient> ingredients) {
        this.time = time;
        this.numberOfServings = numberOfServings;
        this.ingredients = ingredients;
    }

    public int hashCode() {
        // TODO: Add recipe hashCode when complete
        int hash = 0;
        hash += round(time) + round(numberOfServings) + ingredients.hashCode();
        return hash;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getNumberOfServings() {
        return numberOfServings;
    }

    public void setNumberOfServings(float numberOfServings) {
        this.numberOfServings = numberOfServings;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
}
