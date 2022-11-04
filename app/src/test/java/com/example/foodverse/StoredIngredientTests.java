package com.example.foodverse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class StoredIngredientTests {

    /**
     * A helper method to make a new simple test StoredIngredient.
     */
    public StoredIngredient mockIngredient() {
        return new StoredIngredient("Test", 1, new Date(),
                "Pantry", "Cups", 1);
    }


    /**
     * Test the getDescription method from {@link Ingredient}, which is
     * inherited.
     */
    @Test
    public void testGetDescription() {
        StoredIngredient ing = mockIngredient();
        assertEquals(ing.getDescription(), "Test");
    }


    /**
     * Test the setDescription method from {@link Ingredient}, which is
     * inherited.
     */
    @Test
    public void testSetDescription() {
        StoredIngredient ing = mockIngredient();
        ing.setDescription("SetTest");
        assertEquals(ing.getDescription(), "SetTest");
    }

    @Test
    /**
     * Test the getCount method from {@link Ingredient}, which is inherited.
     */
    public void testGetCount() {
        StoredIngredient ing = mockIngredient();
        assertEquals(ing.getCount(), 1);
    }


    /**
     * Test the setCount method from {@link Ingredient}, which is inherited
     */
    @Test
    public void testSetCount() {
        StoredIngredient ing = mockIngredient();
        ing.setCount(10);
        assertEquals(ing.getCount(), 10);
    }


    /**
     * Test the getBestBefore method from {@link StoredIngredient}.
     */
    @Test
    public void testGetBestBefore() {
        StoredIngredient ing = mockIngredient();
        assertEquals(ing.getBestBefore(), new Date());
    }


    /**
     * Test the setBestBefore method from {@link StoredIngredient}
     */
    @Test
    public void testSetBestBefore() {
        StoredIngredient ing = mockIngredient();
        Date date = new Date(2022, 2, 2);
        ing.setBestBefore(date);
        assertEquals(ing.getBestBefore(), date);
    }


    /**
     * Test the getLocation method from {@link StoredIngredient}.
     */
    @Test
    public void testGetLocation() {
        StoredIngredient ing = mockIngredient();
        assertEquals(ing.getLocation(), "Pantry");
    }


    /**
     * Test the setLocation method from {@link StoredIngredient}
     */
    @Test
    public void testSetLocation() {
        StoredIngredient ing = mockIngredient();
        ing.setLocation("Fridge");
        assertEquals(ing.getLocation(), "Fridge");
    }


    /**
     * Test the getUnit method from {@link StoredIngredient}.
     */
    @Test
    public void testGetUnit() {
        StoredIngredient ing = mockIngredient();
        assertEquals(ing.getUnit(), "Cups");
    }


    /**
     * Test the setUnit method from {@link StoredIngredient}
     */
    @Test
    public void testSetUnit() {
        StoredIngredient ing = mockIngredient();
        ing.setUnit("Bags");
        assertEquals(ing.getUnit(), "Bags");
    }


    /**
     * Test the getUnitCost method from {@link StoredIngredient}.
     */
    @Test
    public void testGetUnitCost() {
        StoredIngredient ing = mockIngredient();
        assertEquals(ing.getUnitCost(), 1);
    }


    /**
     * Test the setUnitCost method from {@link StoredIngredient}
     */
    @Test
    public void testSetUnitCost() {
        StoredIngredient ing = mockIngredient();
        ing.setUnitCost(10);
        assertEquals(ing.getUnitCost(), 10);
    }


    /**
     * Test the setUnitCost method from {@link StoredIngredient}
     */
    @Test
    public void testSetUnitCostInvalid() {
        StoredIngredient ing = mockIngredient();
        ing.setUnitCost(-10);
        assertEquals(ing.getUnitCost(), 0);
    }


    /**
     * Test the hashCode method from {@link StoredIngredient}. Expected to
     * return the sum of hash codes of its members.
     */
    @Test
    public void testHashCode() {
        StoredIngredient ing = mockIngredient();
        int hash = ing.getDescription().hashCode() + ing.getUnit().hashCode()
                + ing.getCategory().hashCode() + ing.getUnitCost() + ing.getLocation().hashCode()
                + ing.getBestBefore().getMonth() + ing.getBestBefore().getYear()
                + ing.getBestBefore().getDate();
        assertEquals(ing.hashCode(), hash);
    }

    /**
     * Test the equals method from {@link StoredIngredient}.
     */
    @Test
    public void testEquals() {
        StoredIngredient ing = mockIngredient();
        StoredIngredient ingEqual = new StoredIngredient("Test",
                1, new Date(), "Pantry", "Cups", 1);
        StoredIngredient ingNotEqual = new StoredIngredient("Test",
                1, new Date(), "Fridge", "Cups", 1);
        Object obj = new Object();
        assertTrue(ing.equals(ingEqual));
        assertFalse(ing.equals(ingNotEqual));
        assertFalse(ing.equals(obj));
    }

    /**
     * Test the equals method from {@link StoredIngredient}.
     */
    @Test
    public void testEquals2() {
        StoredIngredient ing = mockIngredient();
        StoredIngredient ingEqual = new StoredIngredient("Test",
                1, new Date(), "Pantry", "Cups", 1);
        StoredIngredient ingNotEqual = new StoredIngredient("Test",
                0, new Date(), "Pantry", "Cups", 2);
        Object obj = new Object();
        assertTrue(ing.equals(ingEqual));
        assertFalse(ing.equals(ingNotEqual));
        assertFalse(ing.equals(obj));
    }
}
