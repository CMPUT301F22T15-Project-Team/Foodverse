package com.example.foodverse;

import static org.junit.jupiter.api.Assertions.*;

import android.graphics.LinearGradient;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

public class RecipeTests {

    /**
     * A helper method to make a new simple test recipe.
     */
    public Recipe mockRecipe() {
        Ingredient testIng = new Ingredient("Lettuce",2,"cups");
        ArrayList<Ingredient> testlist = new ArrayList<>();
        testlist.add(testIng);
        return new Recipe("Test Recipe", 1, 3,
                "Test Category", "Test Comment",testlist);
    }


    /**
     * Test the getTitle method from {@link Recipe}
     */
    @Test
    public void testGetDescription() {
        Recipe rec = mockRecipe();
        assertEquals(rec.getTitle(), "Test Recipe");
    }


    /**
     * Test the setTitle method from {@link Recipe}, which is
     */
    @Test
    public void testSetTitle() {
        Recipe rec = mockRecipe();
        rec.setTitle("SetTest");
        assertEquals(rec.getTitle(), "SetTest");
    }


    /**
     * Test the getPrepTime method from {@link Recipe}
     */
    @Test
    public void testGetPrepTime() {
        Recipe rec = mockRecipe();
        assertEquals(rec.getPrepTime(), 1);
    }


    /**
     * Test the setPrepTime method from {@link Recipe}
     */
    @Test
    public void testSetPrepTime() {
        Recipe rec = mockRecipe();
        rec.setPrepTime(10);
        assertEquals(rec.getPrepTime(), 10);
    }


    /**
     * Test the getServings method from {@link Recipe}
     */
    @Test
    public void testGetServings() {
        Recipe rec = mockRecipe();
        assertEquals(rec.getServings(), 3);
    }


    /**
     * Test the setServings method from {@link Recipe}
     */
    @Test
    public void testSetServings() {
        Recipe rec = mockRecipe();
        rec.setServings(1);
        assertEquals(rec.getServings(), 1);
    }


    /**
     * Test the getCategory method from {@link Recipe}
     */
    @Test
    public void testGetCategory() {
        Recipe rec = mockRecipe();
        assertEquals(rec.getCategory(), "Test Category");
    }


    /**
     * Test the setCategory method from {@link Recipe}
     */
    @Test
    public void testSetCategory() {
        Recipe rec = mockRecipe();
        rec.setCategory("New Category");
        assertEquals(rec.getCategory(), "New Category");
    }


    /**
     * Test the getComments method from {@link Recipe}
     */
    @Test
    public void testGetComments() {
        Recipe rec = mockRecipe();
        assertEquals(rec.getComments(), "Test Comment");
    }


    /**
     * Test the setComments method from {@link Recipe}
     */
    @Test
    public void testSetComment() {
        Recipe rec = mockRecipe();
        rec.setComments("New Comment");
        assertEquals(rec.getComments(), "New Comment");
    }

    /**
     * Test the hashCode method from {@link Recipe} expected to
     * return the sum of hash codes of its members.
     */
    @Test
    public void testHashCode() {
        Recipe rec = mockRecipe();
        int hash = 0;
        hash += rec.getTitle().hashCode() + rec.getCategory().hashCode() + rec.getComments().hashCode()
                + rec.getPrepTime() + rec.getServings();
        assertEquals(rec.hashCode(), hash);
    }

}
