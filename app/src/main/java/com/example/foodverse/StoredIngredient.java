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
 * Version 1.0
 *
 * 2022-09-24
 */
public class Ingredient {
    private Date bestBefore;
    private int count;
    private String description;
    private Location location;
    private int unitCost;

    public Ingredient() {
        bestBefore = new Date();
        count = 0;
        description = "";
        location = Location.PANTRY;
        unitCost = 0;
    }

    public Ingredient(Date bestBefore, int count, String description,
                      Location location, int unitCost) {
        this.bestBefore = bestBefore;
        this.count = count;
        this.description = description;
        this.location = location;
        this.unitCost = unitCost;
    }

    public Date getBestBefore() {
        return bestBefore;
    }

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }

    public int getUnitCost() {
        return unitCost;
    }

    public void setBestBefore(Date bestBefore) {
        this.bestBefore = bestBefore;
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

    public void setLocation(Location location) {
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

enum Location {
    PANTRY,
    FREEZER,
    FRIDGE
}
