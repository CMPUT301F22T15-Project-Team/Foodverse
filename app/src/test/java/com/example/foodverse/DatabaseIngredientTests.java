package com.example.foodverse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIngredientTests {

    /**
     * A helper method to make a new simple test ingredient.
     */
    public Ingredient mockIngredient() {
        return new Ingredient("Test", 1);
    }

    /**
     * A helper method to return the expected string representation of the
     * mock ingredient.
     */
    public String mockString() {
        return "Test|1";
    }


    /**
     * Test the ingredientToString static method from {@link DatabaseIngredient}.
     */
    @Test
    public void testIngredientToString() {
        assertEquals(mockString(),
                DatabaseIngredient.ingredientToString(mockIngredient()));
    }


    /**
     * Test the stringToIngredient static method from {@link DatabaseIngredient}.
     */
    @Test
    public void testStringToIngredient() {
        assertEquals(mockIngredient(),
                DatabaseIngredient.stringToIngredient(mockString()));
    }
}
