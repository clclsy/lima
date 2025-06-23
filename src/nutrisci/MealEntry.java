package nutrisci;

import java.util.Map;

public class MealEntry {
    private String foodName;
    private double quantityInGrams;
    private Map<String, Double> nutrients;
    private double calories;

    public MealEntry(String foodName, double quantityInGrams, Map<String, Double> nutrients, double calories) {
        this.foodName = foodName;
        this.quantityInGrams = quantityInGrams;
        this.nutrients = nutrients;
        this.calories = calories;
    }

    public double getCalories() { return calories; }
    public Map<String, Double> getNutrients() { return nutrients; }
    public String getFoodName() { return foodName; }
    public double getQuantityInGrams() { return quantityInGrams; }
}
