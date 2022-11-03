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
    private String unit;
    private String category;
    private boolean purchased;

    /**
     * Default Constructor
     */
    public ShoppingListIngredient() {
        super();
        unit = "N/A";
        category = "N/A";
    }

    /**
     * The constructor used when ingredient info is known.
     * @param description The ingredient description.
     * @param count The ingredient count.
     * @param unit The ingredient unit.
     * @param category The ingredient category.
     */
    public ShoppingListIngredient(String description, int count, String unit, String category) {
        super(description, count);
        this.unit = unit;
        this.category = category;
    }

    /**
     * Calculates the hash code of the ingredient.
     * @return The hash code of the ingredient.
     */
    public int hashCode() {
        return (super.hashCode() + category.hashCode() + unit.hashCode());
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
