package com.example.foodverse;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

/**
 * A simple static class providing extra functionality to Ingredients that make
 * it far easier to store them in the Firestore database.
 *
 * @Version 1.0
 * @author Tyler
 */
public class DatabaseIngredient {
    /**
     * toString used to store ingredients as strings so they can easily
     * be reconstructed upon retrieval from database.
     *
     * @return ingString, a {@link String} representing the {@link Ingredient}.
     */
    public static String ingredientToString(Ingredient ingredient) {
        String ingString = ingredient.getDescription();
        ingString += "|" + String.valueOf(ingredient.getCount());
        return ingString;
    }

    /**
     * A method used to extract data from a {@link String} and return a new
     * {@link Ingredient} object constructed from its contents.
     * @param ingString A {@link String} representing an {@link Ingredient}
     *                  object.
     * @return A new {@link Ingredient} object constructed from the string.
     */
    public static Ingredient stringToIngredient(String ingString) {
        /*
         * https://stackoverflow.com/questions/7683448/in-java-how-to-get-substring-from-a-string-till-a-character-c
         * For nicely splitting the string. Answer by Chad Schouggins (2011)
         */
        String description = ingString.split("\\|")[0];
        int count = Integer.parseInt(ingString.split("\\|")[1]);
        return new Ingredient(description, count);
    }
}
