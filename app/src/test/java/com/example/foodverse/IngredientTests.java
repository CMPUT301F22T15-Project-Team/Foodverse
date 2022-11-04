package com.example.foodverse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static java.sql.Types.NULL;

public class IngredientTests {

    /**
     * A helper method to make a new simple test ingredient.
     */
    public Ingredient mockIngredient() {
        return new Ingredient("Test", 1, "Cups","Test Category");
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

    /**
     * Test the setDescription method with too high size from {@link Ingredient}.
     */
    @Test
    public void testSetDescriptionLong() {
        Ingredient ing = mockIngredient();
        String testDes = "Terrible Horrible No Good Really Bad Description";
        ing.setDescription(testDes);
        assertNotEquals(ing.getDescription(), testDes);
        assertEquals(ing.getDescription(), testDes.substring(0,29));
    }

    /**
     * Test the getUnit method from {@link Ingredient}.
     */
    @Test
    public void testGetUnit() {
        Ingredient ing = mockIngredient();
        assertEquals(ing.getUnit(), "Cups");
    }


    /**
     * Test the setUnit method from {@link Ingredient}
     */
    @Test
    public void testSetUnit() {
        Ingredient ing = mockIngredient();
        ing.setUnit("Bags");
        assertEquals(ing.getUnit(), "Bags");
    }


    @Test
    /**
     * Test the getCategory method from {@link Ingredient}.
     */
    public void testGetCategory() {
        Ingredient ing = mockIngredient();
        assertEquals(ing.getCategory(), "Test Category");
    }


    /**
     * Test the setCategory method from {@link Ingredient}.
     */
    @Test
    public void testSetCategory() {
        Ingredient ing = mockIngredient();
        ing.setCategory("Test Cat New");
        assertEquals(ing.getCategory(), "Test Cat New");
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
     * Test the setCount method from {@link Ingredient}.
     */
    @Test
    public void testSetCountZero() {
        Ingredient ing = mockIngredient();
        ing.setCount(0);
        assertEquals(ing.getCount(), 0);
    }

    /**
     * Test the setCount method with an invalid num from {@link Ingredient}.
     */
    @Test
    public void testSetCountNegative() {
        Ingredient ing = mockIngredient();
        ing.setCount(-10);
        assertEquals(ing.getCount(), 0);
    }


    /**
     * Test the setCount method with a NULL from {@link Ingredient}.
     */
    @Test
    public void testSetCountNull() {
        Ingredient ing = mockIngredient();
        ing.setCount(NULL);
        assertEquals(ing.getCount(), 0);
    }


    /**
     * Test the hashCode method from {@link Ingredient}. Expected to return the
     * sum of hash codes of its members.
     */
    @Test
    public void testHashCode() {
        Ingredient ing = mockIngredient();
        int hash = ing.getDescription().hashCode() + ing.getUnit().hashCode()
                + ing.getCategory().hashCode();
        assertEquals(ing.hashCode(), hash);
    }


    /**
     * Test the equals method from {@link Ingredient}.
     */
    @Test
    public void testEquals() {
        Ingredient ing = mockIngredient();
        Ingredient ingEqual = new Ingredient("Test", 1, "Cups", "Test Category");
        Ingredient ingNotEqual = new Ingredient("NE", 3);
        Object obj = new Object();
        assertTrue(ing.equals(ingEqual));
        assertFalse(ing.equals(ingNotEqual));
        assertFalse(ing.equals(obj));
    }
}
