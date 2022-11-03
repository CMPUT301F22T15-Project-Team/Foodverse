package com.example.foodverse;

/**
 * Ingredient
 * The ingredient class serves as a representation of the concept of ingredient
 * that you would have in a recipe or meal plan. As a result, it contains several
 * attributes that would be relevant to a customer such as a description, and count.
 * Since there are certain restrictions on the attributes, they have been made protected
 * to enforce these requirements through the setters, but still allow derived classes to access
 *
 * @Version 1.0
 *
 * 2022-10-22
 */
public class Ingredient {
    protected String description;
    protected int count;
    protected String unit;

    public Ingredient() {
        description = "";
        count = 0;
        this.unit = "";
    }

    public Ingredient(String description, int count) {
        this.description = description;
        this.count = count;
        this.unit = "";
    }

    public Ingredient(String description, int count, String unit) {
        this.description = description;
        this.count = count;
        this.unit = unit;
    }

    public int hashCode() {
        int hash = 0;
        hash += count + description.hashCode() + unit.hashCode();
        return hash;
    }

    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            return (hashCode() == ((Ingredient) o).hashCode());
        }
        return false;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

    public String getUnit() {
        return unit;
    }

    public void setCount(int count) {
        if (count >= 0) {
            this.count = count;
        } else {
            this.count = 0;
        }
    }

    public void setDescription(String description) {
        if (description.length() > 30) {
            this.description = description.substring(0,29);
        } else {
            this.description = description;
        }
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
