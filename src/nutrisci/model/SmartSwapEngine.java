package nutrisci.model;

import java.util.*;
import nutrisci.db.FoodDAO;
import nutrisci.db.NutritionDataDAO;

public class SmartSwapEngine {

    public List<FoodSwap> suggestSwaps(List<Meal> meals, List<NutritionalGoal> goals) {
        List<FoodSwap> result = new ArrayList<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (NutritionalGoal goal : goals) {
            Meal worstMeal = null;
            MealItem worstItem = null;
            double worstValue = goal.isIncrease() ? Double.MIN_VALUE : Double.MAX_VALUE;

            for (Meal meal : meals) {
                for (MealItem item : meal.getItems()) {
                    Integer foodId = FoodDAO.getFoodIdByName(item.getIngredient());
                    if (foodId == null)
                        continue;

                    Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                    String key = normalizeNutrientKey(goal.getNutrientName());
                    if (!nutrients.containsKey(key))
                        continue;

                    double value = nutrients.get(key);
                    boolean isWorse = goal.isIncrease() ? value < worstValue : value > worstValue;
                    if (isWorse) {
                        worstMeal = meal;
                        worstItem = item;
                        worstValue = value;
                    }
                }
            }

            if (worstItem != null) {
                int originalFoodId = FoodDAO.getFoodIdByName(worstItem.getIngredient());
                String replacement = FoodDAO.findSwapCandidate(originalFoodId, goal);

                String mealDescription;
                if (worstMeal != null) {
                    mealDescription = "Meal on " + worstMeal.getDate() + " (" + worstMeal.getType() + ")";
                } else {
                    mealDescription = "Unknown meal";
                }
                result.add(new FoodSwap(
                        mealDescription,
                        worstItem.getIngredient(),
                        replacement,
                        "Suggested to improve " + goal.getNutrientName()));
            }
        }

        return result;
    }

    private String normalizeNutrientKey(String input) {
        return switch (input.toLowerCase()) {
            case "calories" -> "Energy";
            case "fat" -> "Fat, total";
            case "fiber" -> "Fibre, total dietary";
            case "carbs", "carbohydrates" -> "Carbohydrate, total";
            case "protein" -> "Protein";
            default -> input;
        };
    }
}
