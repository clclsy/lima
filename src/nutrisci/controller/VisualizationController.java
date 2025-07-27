package nutrisci.controller;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import nutrisci.db.*;
import nutrisci.model.Meal;
import nutrisci.model.MealItem;

/**
 * VisualizationController handles UC8:
 * - Bar Chart: Compare "before" vs "after" nutrients in a time range.
 * - Line Chart: Nutrient trend over time.
 */
public class VisualizationController {

    private final MealDAO mealDAO;
    private final NutritionDataDAO nutritionDAO;
    private final FoodDAO foodDAO;

    public VisualizationController() {
        this.mealDAO = new MealDAO();
        this.nutritionDAO = NutritionDataDAO.getInstance();
        this.foodDAO = new FoodDAO();
    }

    /**
     * Splits meals into "before" and "after" halves within a range, then totals nutrients.
     */
    public Map<String, Map<String, Double>> getBeforeAfterTotals(int userId, String nutrient,
                                                                 LocalDate startDate, LocalDate endDate) {
        List<Meal> meals = mealDAO.getMealsByUserId(userId).stream()
                .filter(m -> !m.getDate().isBefore(startDate) && !m.getDate().isAfter(endDate))
                .sorted(Comparator.comparing(Meal::getDate))
                .collect(Collectors.toList());

        Map<String, Double> beforeTotals = new HashMap<>();
        Map<String, Double> afterTotals = new HashMap<>();

        if (meals.isEmpty()) return Map.of("before", beforeTotals, "after", afterTotals);

        int splitIndex = meals.size() / 2;
        List<Meal> beforeMeals = meals.subList(0, splitIndex);
        List<Meal> afterMeals = meals.subList(splitIndex, meals.size());

        for (Meal meal : beforeMeals) accumulateNutrients(meal, beforeTotals, nutrient);
        for (Meal meal : afterMeals) accumulateNutrients(meal, afterTotals, nutrient);

        return Map.of("before", beforeTotals, "after", afterTotals);
    }

    /**
     * Tracks nutrient totals per day (line chart view).
     */
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

    // Helper: Sums up nutrient values into totals.
    private void accumulateNutrients(Meal meal, Map<String, Double> totals, String nutrient) {
        for (MealItem item : meal.getItems()) {
            int foodId = foodDAO.getFoodIdByName(item.getIngredient());
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

    // Helper: Calculates nutrient value for one meal.
    private double calculateMealNutrient(Meal meal, String nutrient) {
        double total = 0.0;
        for (MealItem item : meal.getItems()) {
            int foodId = foodDAO.getFoodIdByName(item.getIngredient());
            if (foodId != -1) {
                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                double factor = item.getQuantity() / 100.0;
                total += nutrients.getOrDefault(nutrient, 0.0) * factor;
            }
        }
        return total;
    }
}
