package nutrisci.controller;
import nutrisci.db.MealDAO;
import nutrisci.db.NutritionDataDAO;
import nutrisci.model.Meal;
import nutrisci.model.MealItem;

import java.time.LocalDate;
import java.util.*;

/**
 * VisualizationController:
 * - Fetches meals from MealDAO
 * - For each ingredient -> find foodId -> fetch nutrients
 * - Aggregates nutrient totals for charting
 */
public class VisualizationController {

    private final MealDAO mealDAO;
    private final NutritionDataDAO nutritionDAO;

    public VisualizationController() {
        this.mealDAO = new MealDAO();
        this.nutritionDAO = NutritionDataDAO.getInstance();
    }

    /**
     * Returns nutrient totals for a user within a date range
     * @param userId The user's ID
     * @param startDate Start date
     * @param endDate End date
     */
    public Map<String, Double> getNutrientTotals(int userId, LocalDate startDate, LocalDate endDate) {
        List<Meal> meals = mealDAO.getMealsByUserId(userId);
        Map<String, Double> totals = new HashMap<>();

        for (Meal meal : meals) {
            LocalDate mealDate = meal.getDate();
            if (!mealDate.isBefore(startDate) && !mealDate.isAfter(endDate)) {
                for (MealItem item : meal.getItems()) {
                    int foodId = nutritionDAO.getFoodIdByName(item.getIngredient());
                    if (foodId != -1) {
                        Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);

                        // Scale nutrient values by quantity (assuming nutrient values/100g)
                        double factor = item.getQuantity() / 100.0;
                        for (Map.Entry<String, Double> entry : nutrients.entrySet()) {
                            totals.put(entry.getKey(),
                                    totals.getOrDefault(entry.getKey(), 0.0) + (entry.getValue() * factor));
                        }
                    }
                }
            }
        }
        return totals;
    }

    /**
     * Placeholder for Before/After comparison.
     * For now, After = same as Before (will update when swaps persist).
     */
    public Map<String, Map<String, Double>> getBeforeAfterTotals(int userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Double> before = getNutrientTotals(userId, startDate, endDate);
        Map<String, Double> after = new HashMap<>(before); // Placeholder
        Map<String, Map<String, Double>> result = new HashMap<>();
        result.put("before", before);
        result.put("after", after);
        return result;
    }
}
