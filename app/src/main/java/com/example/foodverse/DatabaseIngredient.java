package com.example.foodverse;


import androidx.annotation.NonNull;


/**
 * A simple static class providing extra functionality to the {@link Ingredient}
 * class to convert them to {@link String} objects, or to convert a
 * {@link String} back to an {@link Ingredient}. This makes it far easier to
 * store a list of {@link Ingredient} objects in the Firestore database.
 *
 * @author Tyler
 * @version 1.1
 */
public class DatabaseIngredient {
    /**
     * A simple helper method for
     * {@link DatabaseIngredient#ingredientToString(Ingredient)} to remove any
     * "|" characters before combining into a string into a database.
     *
     * @param str The {@link String} object to remove "|" characters from.
     * @return The {@link String} without any "|" characters.
     * @since 1.1
     */
    @NonNull
    private static String removePipes(String str) {
        return String.join("", str.split("\\|"));
    }


    /**
     * A method used to take the data from a {@link Ingredient} and store it in
     * a {@link String}, with fields separated by "|". Removes any "|"
     * characters in any strings before adding so we don't hit errors when
     * converting back.
     *
     * @param ingredient A {@link Ingredient} object to convert to a string
     *                   representation.
     * @return ingString, a {@link String} representing the {@link Ingredient}.
     * @since 1.1
     */
    @NonNull
    public static String ingredientToString(@NonNull Ingredient ingredient) {
        String ingString = removePipes(ingredient.getDescription());
        ingString += "|" + String.valueOf(ingredient.getCount());
        if (ingredient.getUnit() != "") {
            ingString += "|" + removePipes(ingredient.getUnit());
        }
        if (ingredient.getCategory() != "") {
            ingString += "|" + removePipes(ingredient.getCategory());
        }
        return ingString;
    }


    /**
     * A method used to extract data from a {@link String} and return a new
     * {@link Ingredient} object constructed from its contents. This method
     * should only be called using {@link String} objects generated with the
     * {@link DatabaseIngredient#ingredientToString(Ingredient)} method.
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
        String []ingredientMembers = ingString.split("\\|");
        if (ingredientMembers.length < 2) {
            return new Ingredient();
        } else if (ingredientMembers.length == 2) {
            String description = ingredientMembers[0];
            int count = Integer.parseInt(ingredientMembers[1]);
            return new Ingredient(description, count);
        } else if (ingredientMembers.length == 3) {
            String description = ingredientMembers[0];
            int count = Integer.parseInt(ingredientMembers[1]);
            String unit = ingredientMembers[2];
            return new Ingredient(description, count, unit);
        } else {
            String description = ingredientMembers[0];
            int count = Integer.parseInt(ingredientMembers[1]);
            String unit = ingredientMembers[2];
            String category = ingredientMembers[3];
            return new Ingredient(description, count, unit, category);
        }
    }
}
