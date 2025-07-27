package nutrisci.model;

import java.util.*;
import nutrisci.db.*;

public class SmartSwapEngine {
    private static final double CALORIE_VARIANCE = 0.30; // 30% calorie variance
    private static final double MIN_IMPROVEMENT = 0.10; // 10% minimum improvement

    public List<FoodSwap> suggestSwapsForMeal(Meal meal, List<NutritionalGoal> goals) {
        List<FoodSwap> swaps = new ArrayList<>();
        int swapCount = 0;
        int maxSwaps = Math.min(2, meal.getItems().size()); // Max 2 swaps per meal

        for (MealItem item : meal.getItems()) {
            if (swapCount >= maxSwaps)
                break;
            FoodDAO foodDao = new FoodDAO();
            Integer originalFoodId = foodDao.getFoodIdByName(item.getIngredient());
            if (originalFoodId == null)
                continue;

            Map<String, Double> origNutrients = NutritionDataDAO.getInstance().getFoodNutrients(originalFoodId);
            if (origNutrients.isEmpty())
                continue;

            for (NutritionalGoal goal : goals) {
                String nutrient = normalizeNutrientKey(goal.getNutrientName());
                Double originalValue = origNutrients.get(nutrient);
                if (originalValue == null)
                    continue;

                String replacement = findSimpleReplacement(
                        originalFoodId,
                        nutrient,
                        goal.isIncrease());

                if (replacement != null) {
                    swaps.add(createFoodSwap(meal, item, replacement, goal));
                    swapCount++;
                    break; // Only one swap per item
                }
            }
        }
        return swaps;
    }

    private String findSimpleReplacement(int originalFoodId, String nutrient, boolean increase) {
        return FoodDAO.findSimpleSwap(originalFoodId, nutrient, increase);
    }

    private FoodSwap createFoodSwap(Meal meal, MealItem item, String replacement, NutritionalGoal goal) {
        String rationale = String.format("%s %s",
                goal.isIncrease() ? "Higher" : "Lower",
                goal.getNutrientName());

        return new FoodSwap(
                meal.getDate() + " (" + meal.getType() + ")",
                item.getIngredient(),
                replacement,
                rationale);
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