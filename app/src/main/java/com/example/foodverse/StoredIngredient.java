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
 * @version 1.1
 */
public class StoredIngredient extends Ingredient {
    private Date bestBefore;
    private String location;
    private int unitCost;


    /**
     * Default constructor for StoredIngredient.
     */
    public StoredIngredient() {
        super();
        bestBefore = new Date();
        location = "Pantry";
        unitCost = 0;
    }

    /**
     * Constructor for StoredIngredient with a given description, count, best
     * before date, location, and unit cost.
     *
     * @param description A string representing the description of the
     * ingredient.
     * @param count An integer representing the count of the ingredient.
     * @param bestBefore A Date representing the best before date of the
     * ingredient.
     * @param location A string representing the location of the ingredient.
     * @param unitCost An integer representing the unit cost of the ingredient.
     */
    public StoredIngredient(String description, int count, Date bestBefore,
                            String location, int unitCost) {
        super(description, count);
        this.bestBefore = bestBefore;
        this.location = location;
        this.unitCost = unitCost;
    }

    /**
     * Constructor for StoredIngredient with a given description, count, best
     * before date, location, unit, and unit cost.
     *
     * @param description A string representing the description of the
     * ingredient.
     * @param count An integer representing the count of the ingredient.
     * @param bestBefore A Date representing the best before date of the
     * ingredient.
     * @param location A string representing the location of the ingredient.
     * @param unit A string representing the unit used to count the ingredient.
     * @param unitCost An integer representing the unit cost of the ingredient.
     */
    public StoredIngredient(String description, int count, Date bestBefore,
                            String location, String unit, int unitCost) {
        super(description, count, unit);
        this.bestBefore = bestBefore;
        this.location = location;
        this.unitCost = unitCost;
    }

    /**
     * Sums the hashcodes of all the class' members and calls super.hashCode()
     * if applicable to generate the hashcode of this object.
     *
     * @returns Returns the hash code of this object.
     */
    public int hashCode() {
        int hash = 0;
        hash += unitCost + super.hashCode()
                + location.hashCode() + bestBefore.getYear()
                + bestBefore.getMonth() + bestBefore.getDate();
        return hash;
    }

    /**
     * Getter for the best before date of the ingredient.
     *
     * @returns Returns the {@Link Date} representing the best before date of
     * the ingredient.
     */
    public Date getBestBefore() {
        return bestBefore;
    }

    /**
     * Getter for the storage location of the ingredient.
     *
     * @returns Returns the {@Link String} representing the location of the
     * ingredient.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Getter for the unit cost of the ingredient.
     *
     * @returns Returns the {@Link int} representing the unit cost of the
     * ingredient.
     */
    public int getUnitCost() {
        return unitCost;
    }

    /**
     * Setter for the best before date of the ingredient.
     *
     * @param bestBefore A Date representing the best before date of the
     * ingredient.
     */
    public void setBestBefore(Date bestBefore) {
        this.bestBefore = bestBefore;
    }

    /**
     * Setter for the storage location of the ingredient.
     *
     * @param location A string representing the location of the ingredient.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Setter for the unit cost of the ingredient.
     *
     * @param unitCost An integer representing the unit cost of the ingredient.
     */
    public void setUnitCost(int unitCost) {
        if (unitCost > 0) {
            this.unitCost = unitCost;
        } else {
            this.unitCost = 0;
        }
    }
}
