package nutrisci.controller;

import nutrisci.db.MealDAO;
import nutrisci.db.NutritionDataDAO;
import nutrisci.model.Meal;
import nutrisci.model.MealItem;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
 
//VisualizationController:
//Handles UC8 logic for nutrient visualization:
 //Bar Chart: Compare Before vs After (splits meals in date range) | Line Chart: Nutrient trend over time
public class VisualizationController {

    private final MealDAO mealDAO;
    private final NutritionDataDAO nutritionDAO;

    public VisualizationController() {
        this.mealDAO = new MealDAO();
        this.nutritionDAO = NutritionDataDAO.getInstance();
    }

    /**
     * For Bar Chart: Compare Before vs After by splitting meals in range.
     * @param userId User ID
     * @param nutrient Nutrient name ("All" for all nutrients)
     * @param startDate Start of range
     * @param endDate End of range
     */
    public Map<String, Map<String, Double>> getBeforeAfterTotals(int userId, String nutrient,
                                                                 LocalDate startDate, LocalDate endDate) {
        List<Meal> meals = mealDAO.getMealsByUserId(userId).stream()
                .filter(m -> !m.getDate().isBefore(startDate) && !m.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(Meal::getDate)) // Oldest first
                .collect(Collectors.toList());

        Map<String, Double> beforeTotals = new HashMap<>();
        Map<String, Double> afterTotals = new HashMap<>();

        if (meals.size() == 0) return Map.of("before", beforeTotals, "after", afterTotals);

        int splitIndex = meals.size() / 2; // divide meals into two halves
        List<Meal> beforeMeals = meals.subList(0, splitIndex);
        List<Meal> afterMeals = meals.subList(splitIndex, meals.size());

        for (Meal meal : beforeMeals) accumulateNutrients(meal, beforeTotals, nutrient);
        for (Meal meal : afterMeals) accumulateNutrients(meal, afterTotals, nutrient);

        return Map.of("before", beforeTotals, "after", afterTotals);
    }

    // For Line Chart: Nutrient trend grouped by date.
    public Map<String, Double> getTrendData(int userId, String nutrient,
                                            LocalDate startDate, LocalDate endDate) {
        List<Meal> meals = mealDAO.getMealsByUserId(userId).stream()
                .filter(m -> !m.getDate().isBefore(startDate) && !m.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(Meal::getDate))
                .collect(Collectors.toList());

        Map<String, Double> trend = new TreeMap<>();
        for (Meal meal : meals) {
            String dateKey = meal.getDate().toString();
            double value = calculateMealNutrient(meal, nutrient);
            trend.put(dateKey, trend.getOrDefault(dateKey, 0.0) + value);
        }
        return trend;
    }

    // Adds nutrient values for a meal to totals.
     
    private void accumulateNutrients(Meal meal, Map<String, Double> totals, String nutrient) {
        for (MealItem item : meal.getItems()) {
            int foodId = nutritionDAO.getFoodIdByName(item.getIngredient());
            if (foodId != -1) {
                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                double factor = item.getQuantity() / 100.0;
                if (nutrient.equalsIgnoreCase("All")) {
                    for (Map.Entry<String, Double> entry : nutrients.entrySet()) {
                        totals.put(entry.getKey(),
                                totals.getOrDefault(entry.getKey(), 0.0) + entry.getValue() * factor);
                    }
                } else if (nutrients.containsKey(nutrient)) {
                    totals.put(nutrient,
                            totals.getOrDefault(nutrient, 0.0) + nutrients.get(nutrient) * factor);
                }
            }
        }
    }

    // Calculates a nutrient's value for one meal.
    
    private double calculateMealNutrient(Meal meal, String nutrient) {
        double total = 0.0;
        for (MealItem item : meal.getItems()) {
            int foodId = nutritionDAO.getFoodIdByName(item.getIngredient());
            if (foodId != -1) {
                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                double factor = item.getQuantity() / 100.0;
                total += nutrients.getOrDefault(nutrient, 0.0) * factor;
            }
        }
        return total;
    }
}
