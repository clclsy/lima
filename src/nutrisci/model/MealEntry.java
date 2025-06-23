package nutrisci.model;

import java.io.Serializable;

public class MealEntry implements Serializable {
    private String foodName;
    private double quantityInGrams;

    public MealEntry(String foodName, double quantityInGrams) {
        this.foodName = foodName;
        this.quantityInGrams = quantityInGrams;
    }

    public String getFoodName() { return foodName; }
    public double getQuantity() { return quantityInGrams; }
}
