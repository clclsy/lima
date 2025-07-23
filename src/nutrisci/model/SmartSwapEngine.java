package nutrisci.model;

import java.util.*;
import nutrisci.db.FoodDAO;
import nutrisci.db.NutritionDataDAO;

public class SmartSwapEngine {
    public List<FoodSwap> suggestSwaps(List<Meal> meals, List<NutritionalGoal> goals) {
        List<FoodSwap> result = new ArrayList<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (NutritionalGoal goal : goals) {
            for (Meal meal : meals) {
                for (MealItem item : meal.getItems()) {
                    Integer foodId = FoodDAO.getFoodIdByName(item.getIngredient());
                    if (foodId == null)
                        continue;

                    Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                    String key = normalizeNutrientKey(goal.getNutrientName());
                    if (!nutrients.containsKey(key))
                        continue;

                    double currentValue = nutrients.get(key);
                    String replacement = FoodDAO.findSwapCandidateRelaxed(foodId, key, goal.isIncrease(), currentValue);
                    if (replacement == null || replacement.equalsIgnoreCase(item.getIngredient()))
                        continue;

                    result.add(new FoodSwap(
                            meal.getDate() + " (" + meal.getType() + ")",
                            item.getIngredient(),
                            replacement,
                            "Helps to " + (goal.isIncrease() ? "increase" : "decrease") + " " + key));
                }
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

    public List<FoodSwap> suggestSwapsForMeal(Meal meal, List<NutritionalGoal> goals) {
        return suggestSwaps(List.of(meal), goals);
    }

}
