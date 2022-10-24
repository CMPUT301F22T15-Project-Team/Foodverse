package com.example.foodverse;

import java.util.ArrayList;

public class Meal {
    ArrayList<Ingredient> ingredients;
    // Recipe recipe;
    float time;
    float numberOfServings;

    public Meal(float time, float numberOfServings) {
        this.time = time;
        this.numberOfServings = numberOfServings;
        //this.recipe = recipe;
        // this.ingredients = new ArrayList<Ingredient>();

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
}
