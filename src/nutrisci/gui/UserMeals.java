package nutrisci.gui;

import java.time.LocalDate;
import java.util.*;

public class UserMeals {
    private LocalDate date;
    private String type; // e.g., Breakfast, Lunch
    private List<MealItem> items;

    public UserMeals(LocalDate date, String type, List<MealItem> items) {
        this.date = date;
        this.type = type;
        this.items = items;
    }

    public double getTotalCalories() {
        return items.stream().mapToDouble(MealItem::getCalories).sum();
    }

    public Map<String, Double> getNutrientBreakdown() {
        NutrientProfile total = new NutrientProfile();
        for (MealItem item : items) {
            total.merge(item.getNutrients());
        }
        return total.toMap();
    }

    public void updateMeal(List<MealItem> newItems) {
        this.items = newItems;
    }

    public void clearItems() {
        this.items.clear();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public List<MealItem> getItems() {
        return items;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }
}
