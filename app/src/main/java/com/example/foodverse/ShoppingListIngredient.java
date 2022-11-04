package com.example.foodverse;

/**
 * ShoppingListIngredient
 * The ShoppingListIngredient class is intended to be used the same as an Ingredient.
 * It extends upon the ingredient class by also having a unit and category which would be useful
 * information for anyone viewing a shopping list.
 *
 * @Version 1.0
 *
 * 2022-10-30
 */
public class ShoppingListIngredient extends Ingredient {
    private boolean purchased;

    /**
     * Default Constructor
     */
    public ShoppingListIngredient() {
        super();
    }

    /**
     * The constructor used when ingredient info is known.
     * @param description The ingredient description.
     * @param count The ingredient count.
     * @param unit The ingredient unit.
     * @param category The ingredient category.
     */
    public ShoppingListIngredient(String description, int count, String unit, String category) {
        super(description, count, unit, category);
    }

    /**
     * Calculates the hash code of the ingredient. Just calls the
     * {@link Ingredient#hashCode()} method. Should be equal for equal
     * {@link ShoppingListIngredient} objects.
     *
     * @return The hash code of the object.
     */
    public int hashCode() {
        return (super.hashCode());
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
