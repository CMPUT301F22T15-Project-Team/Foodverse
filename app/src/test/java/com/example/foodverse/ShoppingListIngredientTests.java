package com.example.foodverse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Date;

public class ShoppingListIngredientTests {

    /**
     * A helper method to make a new simple test ShoppingListIngredient.
     */
    public ShoppingListIngredient mockIngredient() {
        return new ShoppingListIngredient("Test", 1, "Cups", "Test Category");
    }


    /**
     * Test the getDescription method from {@link Ingredient}, which is
     * inherited.
     */
    @Test
    public void testGetDescription() {
        ShoppingListIngredient ing = mockIngredient();
        assertEquals(ing.getDescription(), "Test");
    }


    /**
     * Test the setDescription method from {@link Ingredient}, which is
     * inherited.
     */
    @Test
    public void testSetDescription() {
        ShoppingListIngredient ing = mockIngredient();
        ing.setDescription("SetTest");
        assertEquals(ing.getDescription(), "SetTest");
    }

    @Test
    /**
     * Test the getCount method from {@link Ingredient}, which is inherited.
     */
    public void testGetCount() {
        ShoppingListIngredient ing = mockIngredient();
        assertEquals(ing.getCount(), 1);
    }


    /**
     * Test the setCount method from {@link Ingredient}, which is inherited
     */
    @Test
    public void testSetCount() {
        ShoppingListIngredient ing = mockIngredient();
        ing.setCount(10);
        assertEquals(ing.getCount(), 10);
    }


    @Test
    /**
     * Test the getCategory method from {@link Ingredient}, which is inherited.
     */
    public void testGetCategory() {
        ShoppingListIngredient ing = mockIngredient();
        assertEquals(ing.getCategory(), "Test Category");
    }


    /**
     * Test the setCategory method from {@link Ingredient}, which is inherited
     */
    @Test
    public void testSetCategory() {
        ShoppingListIngredient ing = mockIngredient();
        ing.setCategory("Test Cat New");
        assertEquals(ing.getCategory(), "Test Cat New");
    }


    /**
     * Test the getUnit method from {@link ShoppingListIngredient}.
     */
    @Test
    public void testGetUnit() {
        ShoppingListIngredient ing = mockIngredient();
        assertEquals(ing.getUnit(), "Cups");
    }


    /**
     * Test the setUnit method from {@link ShoppingListIngredient}
     */
    @Test
    public void testSetUnit() {
        ShoppingListIngredient ing = mockIngredient();
        ing.setUnit("Bags");
        assertEquals(ing.getUnit(), "Bags");
    }


    /**
     * Test the isPurchased method from {@link ShoppingListIngredient}.
     */
    @Test
    public void isPurchased() {
        ShoppingListIngredient ing = mockIngredient();
        assertEquals(ing.isPurchased(), false);
    }


    /**
     * Test the setPurchased method from {@link ShoppingListIngredient}
     */
    @Test
    public void testSetPurchased() {
        ShoppingListIngredient ing = mockIngredient();
        ing.setPurchased(true);
        assertEquals(ing.isPurchased(), true);
    }
//    /**
//     * Test the getUnitCost method from {@link ShoppingListIngredient}.
//     */
//    @Test
//    public void testGetUnitCost() {
//        ShoppingListIngredient ing = mockIngredient();
//        assertEquals(ing.getUnitCost(), 1);
//    }
//
//
//    /**
//     * Test the setUnitCost method from {@link ShoppingListIngredient}
//     */
//    @Test
//    public void testSetUnitCost() {
//        ShoppingListIngredient ing = mockIngredient();
//        ing.setUnitCost(10);
//        assertEquals(ing.getUnitCost(), 10);
//    }


    /**
     * Test the hashCode method from {@link ShoppingListIngredient}. Expected to
     * return the sum of hash codes of its members, aside from 'purchased'.
     */
    @Test
    public void testHashCode() {
        ShoppingListIngredient ing = mockIngredient();
        int hash = 0;
        hash += ing.getDescription().hashCode()
                + ing.getUnit().hashCode() + ing.getCategory().hashCode();
        assertEquals(ing.hashCode(), hash);
    }

    /**
     * Test the equals method from {@link Ingredient}.
     */
    @Test
    public void testEquals() {
        ShoppingListIngredient ing = mockIngredient();
        ShoppingListIngredient ingEqual = new ShoppingListIngredient("Test", 1,
                "Cups", "Test Category");
        ShoppingListIngredient ingNotEqual = new ShoppingListIngredient("Test", 7,
                "Grams", "Test 2 Category");
        Object obj = new Object();
        assertTrue(ing.equals(ingEqual));
        assertFalse(ing.equals(ingNotEqual));
        assertFalse(ing.equals(obj));
    }
}
