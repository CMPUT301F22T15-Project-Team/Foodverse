package com.example.foodverse;

/**
 * Ingredient
 * The ingredient class serves as a representation of the concept of ingredient
 * that you would have in a recipe or meal plan. As a result, it contains several
 * attributes that would be relevant to a customer such as a description, count,
 * unit and category. Since there are certain restrictions on the attributes,
 * they have been made protected to enforce these requirements through the
 * setters, but still allow derived classes to access if needed.
 *
 * @version 1.0
 */
public class Ingredient {
    protected String description;
    protected int count;
    protected String unit;
    protected String category;


    /**
     * The constructor for the {@link Ingredient} class.
     */
    public Ingredient() {
        description = "";
        count = 0;
        unit = "";
        category = "";
    }

    /**
     * A parameterized constructor for the {@link Ingredient} class that takes
     * description and count.
     *
     * @param description The description for the ingredient.
     * @param count The count of the ingredient.
     */
    public Ingredient(String description, int count) {
        this.description = description;
        this.count = count;
        this.unit = "";
        this.category = "";
    }

    /**
     * A parameterized constructor for the {@link Ingredient} class that takes
     * description, count and a unit.
     *
     * @param description The description for the ingredient.
     * @param count The count of the ingredient.
     * @param unit The unit of storage for the ingredient.
     */
    public Ingredient(String description, int count, String unit) {
        this.description = description;
        this.count = count;
        this.unit = unit;
        this.category = "";
    }

    /**
     * A parameterized constructor for the {@link Ingredient} class that takes
     * description, count, unit and category.
     *
     * @param description The description for the ingredient.
     * @param count The count of the ingredient.
     * @param unit The unit of storage for the ingredient.
     * @param category The category of the ingredient.
     */
    public Ingredient(String description, int count, String unit, String category) {
        this.description = description;
        this.count = count;
        this.unit = unit;
        this.category = category;
    }

    /**
     * Sums the hashCode()s of all the class' members to generate the hashcode
     * for this object. Does not include the count of ingredient in the
     * hashcode. Should be equal for objects that are considered equal.
     */
    public int hashCode() {
        int hash = 0;
        hash += description.hashCode() + unit.hashCode() +
                category.hashCode();
        return hash;
    }

    /**
     * Checks to see if an {@link Object} is equal to this {@link Ingredient}
     * object. Always false if {@link Object} is not an instance of
     * {@link Ingredient}. Will return true if the {@link Ingredient#hashCode()}
     * methods for both ingredients return the same value.
     *
     * @param o An object to check if the ingredient is equal.
     * @return
     */
    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            return (hashCode() == ((Ingredient) o).hashCode());
        }
        return false;
    }

    /**
     * Getter for the description of the {@link Ingredient}.
     *
     * @return An {@link String} storing the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for the count of the {@link Ingredient}.
     *
     * @return An integer primitive storing the count.
     */
    public int getCount() {
        return count;
    }

    /**
     * Getter for the unit of the {@link Ingredient}.
     *
     * @return A {@link String} storing the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Getter for the category of the {@link Ingredient}.
     *
     * @return A {@link String} storing the category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Setter for the count of the {@link Ingredient}. Count may be set to
     * a minimum of 0.
     *
     * @param count An integer, the new count to set.
     */
    public void setCount(int count) {
        if (count >= 0) {
            this.count = count;
        } else {
            this.count = 0;
        }
    }

    /**
     * Setter for the description of the {@link Ingredient}. Must be less than
     * length 30. If longer, it is clipped to this length.
     *
     * @param description A {@link String} to set the description to.
     */
    public void setDescription(String description) {
        if (description.length() > 30) {
            this.description = description.substring(0,29);
        } else {
            this.description = description;
        }
    }

    /**
     * Setter for the unit of the {@link Ingredient}.
     *
     * @param unit A {@link String} to set the unit to.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Setter for the category of the {@link Ingredient}.
     *
     * @param category A {@link String} to set the category to.
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
