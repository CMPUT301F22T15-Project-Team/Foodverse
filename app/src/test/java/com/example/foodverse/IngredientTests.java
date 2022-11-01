package com.example.foodverse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IngredientTests {

    /**
     * A helper method to make a new simple test ingredient.
     */
    public Ingredient mockIngredient() {
        return new Ingredient("Test", 1);
    }


    /**
     * Test the getDescription method from {@link Ingredient}.
     */
    @Test
    public void testGetDescription() {
        Ingredient ing = mockIngredient();
        assertEquals(ing.getDescription(), "Test");
    }


    /**
     * Test the setDescription method from {@link Ingredient}.
     */
    @Test
    public void testSetDescription() {
        Ingredient ing = mockIngredient();
        ing.setDescription("SetTest");
        assertEquals(ing.getDescription(), "SetTest");
    }

    @Test
    /**
     * Test the getCount method from {@link Ingredient}.
     */
    public void testGetCount() {
        Ingredient ing = mockIngredient();
        assertEquals(ing.getCount(), 1);
    }


    /**
     * Test the setCount method from {@link Ingredient}.
     */
    @Test
    public void testSetCount() {
        Ingredient ing = mockIngredient();
        ing.setCount(10);
        assertEquals(ing.getCount(), 10);
    }

    /**
     * Test the hashCode method from {@link Ingredient}. Expected to return the
     * sum of hash codes of its members.
     */
    @Test
    public void testHashCode() {
        Ingredient ing = mockIngredient();
        int hash = ing.getCount() + ing.getDescription().hashCode();
        assertEquals(ing.hashCode(), hash);
    }

    /**
     * Test the equals method from {@link Ingredient}.
     */
    @Test
    public void testEquals() {
        Ingredient ing = mockIngredient();
        Ingredient ingEqual = new Ingredient("Test", 1);
        Ingredient ingNotEqual = new Ingredient("NE", 3);
        Object obj = new Object();
        assertTrue(ing.equals(ingEqual));
        assertFalse(ing.equals(ingNotEqual));
        assertFalse(ing.equals(obj));
    }
}
