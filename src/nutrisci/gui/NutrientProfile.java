package nutrisci.gui;

import java.util.HashMap;
import java.util.Map;

public class NutrientProfile {
    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double fiber;

    public NutrientProfile() {
        this.calories = 0;
        this.protein = 0;
        this.carbs = 0;
        this.fats = 0;
        this.fiber = 0;
    }

    public void merge(NutrientProfile other) {
        this.calories += other.calories;
        this.protein += other.protein;
        this.carbs += other.carbs;
        this.fats += other.fats;
        this.fiber += other.fiber;
    }

    public Map<String, Double> toMap() {
        Map<String, Double> map = new HashMap<>();
        map.put("Calories", calories);
        map.put("Protein", protein);
        map.put("Carbs", carbs);
        map.put("Fats", fats);
        map.put("Fiber", fiber);
        return map;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFats() {
        return fats;
    }

    public double getFiber() {
        return fiber;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }
}
