package com.example.foodverse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

public class MealTests {

    /**
     * A helper method to make a new simple test ingredient list.
     */
    public ArrayList<Ingredient> ingList() {
        ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
        ingredientList.add(new Ingredient());
        ingredientList.add(new Ingredient("Test2", 2));
        return ingredientList;
    }

    /**
     * A helper method to make a new simple second test ingredient list.
     */
    public ArrayList<Ingredient> ingList2() {
        ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
        ingredientList.add(new Ingredient("Test4", 7));
        ingredientList.add(new Ingredient("Test3", 5));
        return ingredientList;
    }

    /**
     * A helper method to make a new simple test Meal.
     */
    public Meal mockMeal() {
        return new Meal(ingList(), new Date());
    }


    /**
     * Test the getIngredients method from {@link Meal}.
     */
    @Test
    public void testGetIngredients() {
        Meal mel = mockMeal();
        assertEquals(mel.getIngredients(), ingList());
    }


    /**
     * Test the setIngredients method from {@link Meal}.
     */
    @Test
    public void testSetIngredients() {
        Meal mel = mockMeal();
        mel.setIngredients(ingList2());
        assertEquals(mel.getIngredients(), ingList2());
    }


    /**
     * Test the getDate method from {@link Meal}
     */
    @Test
    public void testGetDate() {
        Meal mel = mockMeal();
        assertEquals(mel.getDate(), new Date());
    }


    /**
     * Test the setDate method from {@link Meal}
     */
    @Test
    public void testSetDate() {
        Meal mel = mockMeal();
        Date newDate = new Date(0000000003);
        mel.setDate(newDate);
        assertEquals(mel.getDate(), newDate);
    }


    /**
     * Test the hashCode method from {@link Meal} expected to
     * return the sum of hash codes of its members.
     */
    @Test
    public void testHashCode() {
        Meal mel = mockMeal();
        int hash = 0;
        hash += mel.getIngredients().hashCode() + mel.getDate().hashCode();
        assertEquals(mel.hashCode(), hash);
    }

}
