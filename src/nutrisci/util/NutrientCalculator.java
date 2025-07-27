package nutrisci.util;

import java.util.*;
import nutrisci.db.*;
import nutrisci.model.Meal;
import nutrisci.model.MealItem;

public class NutrientCalculator {
    public static Map<String, Double> calculateMealNutrients(Meal meal) {
        Map<String, Double> totals = new HashMap<>();
        NutritionDataDAO nutritionDAO = NutritionDataDAO.getInstance();

        for (MealItem item : meal.getItems()) {
            FoodDAO foodDao = new FoodDAO();
            Integer foodId = foodDao.getFoodIdByName(item.getIngredient());

            if (foodId == null) {
                System.out.println("⚠ No match found for: " + item.getIngredient());
                continue;
            }

            Map<String, Double> nutrients = nutritionDAO.getFoodNutrients(foodId);
            nutrients.forEach((k, v) -> totals.merge(k, v * item.getQuantity(), Double::sum));
            System.out.println("→ Item: " + item.getIngredient() + ", Nutrients: " + nutrients);
        }

        return totals;
    }

}