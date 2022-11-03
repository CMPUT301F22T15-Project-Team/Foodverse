package com.example.foodverse;


import androidx.annotation.NonNull;


/**
 * A simple static class providing extra functionality to the {@link Ingredient}
 * class to convert them to {@link String} objects, or to convert a
 * {@link String} back to an {@link Ingredient}. This makes it far easier to
 * store a list of {@link Ingredient} objects in the Firestore database.
 *
 * @author Tyler
 * @version 1.0
 */
public class DatabaseIngredient {
    /**
     * A method used to take the data from a {@link Ingredient} and store it in
     * a {@link String}, with fields separated by "|".
     *
     * @param ingredient A {@link Ingredient} object to convert to a string
     *                   representation.
     * @return ingString, a {@link String} representing the {@link Ingredient}.
     * @since 1.0
     */
    @NonNull
    public static String ingredientToString(@NonNull Ingredient ingredient) {
        String ingString = ingredient.getDescription();
        ingString += "|" + String.valueOf(ingredient.getCount()) + "|" +
        ingredient.getUnit();
        return ingString;
    }


    /**
     * A method used to extract data from a {@link String} and return a new
     * {@link Ingredient} object constructed from its contents.
     *
     * @param ingString A {@link String} representing an {@link Ingredient}
     *                  object.
     * @return A new {@link Ingredient} object constructed from the string.
     * @since 1.0
     */
    @NonNull
    public static Ingredient stringToIngredient(@NonNull String ingString) {
        /*
         * https://stackoverflow.com/questions/7683448/in-java-how-to-get-substring-from-a-string-till-a-character-c
         * For nicely splitting the string. Answer by Chad Schouggins (2011)
         */
        String description = ingString.split("\\|")[0];
        int count = Integer.parseInt(ingString.split("\\|")[1]);
        try {
            String unit = ingString.split("\\|")[2];
            return new Ingredient(description, count, unit);
        } catch (ArrayIndexOutOfBoundsException e) {
            return new Ingredient(description, count);
        }
    }
}
