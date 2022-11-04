package com.example.foodverse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseIngredientTests {

    /**
     * A helper method to make a new simple test ingredient.
     */
    public Ingredient mockIngredient() {
        return new Ingredient("Test",
                1, "Unit");
    }


    /**
     * A helper method to make a new simple test ingredient with default constructor.
     */
    public Ingredient mockIngredient1() {
        return new Ingredient();
    }


    /**
     * A helper method to make a new simple test ingredient with default constructor.
     */
    public Ingredient mockIngredient2() {
        return new Ingredient("Test", 1, "Unit", "Category");
    }


    /**
     * A helper method to return the expected string representation of the
     * mock ingredient.
     */
    public String mockString1() {
        return "Test";
    }

    public String mockString() {
        return "Test|1|Unit";
    }

    public String mockString2() {
        return "Test|1";
    }

    public String mockString3() {
        return "Test|1|Unit|Category";
    }


    /**
     * Test the ingredientToString static method from {@link DatabaseIngredient} with no category.
     */
    @Test
    public void testIngredientToString() {
        assertEquals(mockString(),
                DatabaseIngredient.ingredientToString(mockIngredient()));
        assertEquals("Test|1", DatabaseIngredient.ingredientToString(
                        new Ingredient("Test", 1)));
    }

    /**
     * Test the ingredientToString static method from {@link DatabaseIngredient}.
     */
    @Test
    public void testIngredientToStringCategory() {
        assertEquals(mockString3(),
                DatabaseIngredient.ingredientToString(mockIngredient2()));
    }


    /**
     * Test the stringToIngredient static method from {@link DatabaseIngredient}.
     */
    @Test
    public void testStringToIngredientThreeMembers() {
        assertEquals(mockIngredient(),
                DatabaseIngredient.stringToIngredient(mockString()));
    }


    /**
     * Test the stringToIngredient static method from {@link DatabaseIngredient}.
     */
    @Test
    public void testStringToIngredientTooFewMembers() {
        assertEquals(mockIngredient1(),
                DatabaseIngredient.stringToIngredient(mockString1()));
        // Test with only description and count
        Ingredient ing = new Ingredient("Test", 1);
        assertEquals(ing,
                DatabaseIngredient.stringToIngredient("Test|1"));
    }


    /**
     * Test the stringToIngredient static method from {@link DatabaseIngredient}.
     */
    @Test
    public void testStringToIngredientTwoMembers() {
        Ingredient ing = new Ingredient("Test", 1);
        assertEquals(ing,
                DatabaseIngredient.stringToIngredient("Test|1"));
    }

    /**
     * Test the stringToIngredient static method from {@link DatabaseIngredient}.
     */
    @Test
    public void testStringToIngredientFourMembers() {
        assertEquals(mockIngredient2(),
                DatabaseIngredient.stringToIngredient(mockString3()));
        // Test with only description and count
        Ingredient ing = new Ingredient("Test", 1);
        assertEquals(ing,
                DatabaseIngredient.stringToIngredient("Test|1"));
    }
}
