package com.example.foodverse;

import java.util.Date;

/**
 * Ingredient
 * The ingredient class serves as a representation of the concept of ingredient
 * that you would purchase at a grocery store. As a result, it contains several
 * attributes that would be relevant to a customer such as a best before date,
 * description, and unit cost. Since there are certain restrictions on the
 * attributes, they have been made private to enforce these requirements through
 * the setters.
 *
 * @Version 1.1
 *
 * 2022-09-24
 */
public class StoredIngredient extends Ingredient {
    private Date bestBefore;
    private String location;
    private int unitCost;

    public StoredIngredient() {
        super();
        bestBefore = new Date();
        location = "Pantry";
        unitCost = 0;
    }

    public StoredIngredient(String description, int count, Date bestBefore,
                            String location, int unitCost) {
        super(description, count);
        this.bestBefore = bestBefore;
        this.location = location;
        this.unitCost = unitCost;
    }

    public int hashCode() {
        int hash = 0;
        hash += unitCost + super.hashCode()
                + location.hashCode() + bestBefore.getYear()
                + bestBefore.getMonth() + bestBefore.getDate();
        return hash;
    }

    public Date getBestBefore() {
        return bestBefore;
    }

    public String getLocation() {
        return location;
    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setBestBefore(Date bestBefore) {
        this.bestBefore = bestBefore;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setUnitCost(int unitCost) {
        if (unitCost > 0) {
            this.unitCost = unitCost;
        } else {
            this.unitCost = 0;
        }
    }
}
