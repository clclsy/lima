package nutrisci;

import java.time.LocalDate;
import java.util.*;

public class Meal {
    private String mealId;
    private LocalDate date;
    private MealType type;
    private List<MealEntry> entries;

    public Meal(String mealId, LocalDate date, MealType type) {
        this.mealId = mealId;
        this.date = date;
        this.type = type;
        this.entries = new ArrayList<>();
    }

    public void addMealEntry(MealEntry entry) {
        entries.add(entry);
    }

    public double getTotalCalories() {
        return entries.stream().mapToDouble(MealEntry::getCalories).sum();
    }

    public Map<String, Double> getNutrientSummary() {
        Map<String, Double> summary = new HashMap<>();
        for (MealEntry entry : entries) {
            entry.getNutrients().forEach((k, v) -> summary.merge(k, v, Double::sum));
        }
        return summary;
    }

    public String getMealId() { return mealId; }
    public LocalDate getDate() { return date; }
    public MealType getType() { return type; }
    public List<MealEntry> getEntries() { return entries; }
}
