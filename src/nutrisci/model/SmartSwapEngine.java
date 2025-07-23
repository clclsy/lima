package nutrisci.model;

import java.util.*;
import nutrisci.db.FoodDAO;
import nutrisci.db.NutritionDataDAO;

public class SmartSwapEngine {

    public List<FoodSwap> suggestSwapsForMeal(Meal meal, List<NutritionalGoal> goals) {
        List<FoodSwap> result = new ArrayList<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        System.out.println("Analyzing meal for swaps with goals: " + goals);

        for (NutritionalGoal goal : goals) {
            MealItem worstItem = null;
            double worstValue = goal.isIncrease() ? Double.MIN_VALUE : Double.MAX_VALUE;

            System.out.println("Processing goal: " + goal.getDescription());

            for (MealItem item : meal.getItems()) {
                Integer foodId = FoodDAO.getFoodIdByName(item.getIngredient());
                if (foodId == null) {
                    System.out.println("Could not find food ID for: " + item.getIngredient());
                    continue;
                }

                Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
                String key = normalizeNutrientKey(goal.getNutrientName());

                if (!nutrients.containsKey(key)) {
                    System.out.println("Nutrient " + key + " not found for " + item.getIngredient());
                    continue;
                }

                double value = nutrients.get(key);
                boolean isWorse = goal.isIncrease() ? value < worstValue : value > worstValue;

                if (isWorse) {
                    worstItem = item;
                    worstValue = value;
                }
            }

            if (worstItem != null) {
                System.out.println("Selected worst item for replacement: " + worstItem.getIngredient());
                int originalFoodId = FoodDAO.getFoodIdByName(worstItem.getIngredient());
                String replacement = FoodDAO.findSwapCandidate(originalFoodId, goal);

                if (!replacement.equals("No better match found")) {
                    result.add(new FoodSwap(
                            meal.getDate() + " - " + meal.getType(),
                            worstItem.getIngredient(),
                            replacement,
                            "Suggested to " + (goal.isIncrease() ? "increase" : "decrease") + " "
                                    + goal.getNutrientName()));
                } else {
                    System.out.println("No suitable replacement found for " + worstItem.getIngredient());
                }
            } else {
                System.out.println("No items found that could be improved for goal: " + goal.getDescription());
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