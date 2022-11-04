package com.example.foodverse;

import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Recipe
 * The recipe class serves as a representation of the concept of recipe
 * that you would have in the recipe list. As a result, it contains several
 * attributes that would be relevant to the user such as a recipe title, the preparation time,
 * the number of servings each recipe serves, the recipe category, comments about the recipe,
 * and a list of ingredients.
 * Since there are certain restrictions on the attributes, they have been made private
 * to enforce these requirements through the setters.
 *
 * @version 1.0
 */
public class Recipe implements Serializable {
   private String title;
   private int prep_time; //in mins
   private int servings;
   private String category;
   private String comments;
   private ArrayList<Ingredient> ingredientArrayList;

    /**
     * A constructor for recipe taking all parameters.
     * @param title {@link String} for the recipe
     * @param ingredientArrayList an {@link ArrayList<Ingredient>} to be given to the recipe
     * @param prep_time a {@link int} for how long it takes to prepare a recipe
     * @param servings {@link int} the number of servings a recipe can serve
     * @param category {@link String} the category for the recipe
     * @param comments {@link String} the comments on the recipe
     *
     * @since version 1.0
     */
    public Recipe(String title, int prep_time, int servings, String category, String comments, ArrayList<Ingredient> ingredientArrayList) {
        this.title = title; //add char limit
        this.prep_time = prep_time;
        this.servings = servings;
        this.category = category;
        this.comments = comments; //add limit
        this.ingredientArrayList = ingredientArrayList;
    }

    /**
     * Sums the hash codes of all the class' members, to get the hash code for
     * this object.
     *
     * @returns Returns the hash code of this object.
     */
    public int hashCode() {
        int hash = 0;
        hash += title.hashCode() + category.hashCode() + comments.hashCode()
                + prep_time + servings; // + ingredientArrayList.hashCode();
        return hash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrepTime() {
        return prep_time;
    }

    public void setPrepTime(int prep_time) {
        this.prep_time = prep_time;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

//    public ArrayList<Ingredient> getIngredientArrayList() {
//        return ingredientArrayList;
//    }
//
//    public void setIngredientArrayList(ArrayList<Ingredient> ingredientArrayList) {
//        this.ingredientArrayList = ingredientArrayList;
//    }

}
