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
                .sorted(Comparator.comparing(Meal::getDate))
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

    public Map<String, Map<String, Double>> getBeforeAfterTotals(int userId, LocalDate startDate, LocalDate endDate, String foodToReplace, String replacementFood) {
        Map<String, Double> before = getNutrientTotals(userId, startDate, endDate);
        Map<String, Double> after = new HashMap<>(before);

        List<Meal> meals = mealDAO.getMealsByUserId(userId);

        int foodToReplaceId = nutritionDAO.getFoodIdByName(foodToReplace);
        int replacementFoodId = nutritionDAO.getFoodIdByName(replacementFood);

        if (foodToReplaceId == -1 || replacementFoodId == -1) {
            return new HashMap<>();
        }

        Map<String, Double> foodToReplaceNutrients = nutritionDAO.getFoodNutrients(foodToReplaceId);
        Map<String, Double> replacementFoodNutrients = nutritionDAO.getFoodNutrients(replacementFoodId);

        for (Meal meal : meals) {
            LocalDate mealDate = meal.getDate();
            if (!mealDate.isBefore(startDate) && !mealDate.isAfter(endDate)) {
                for (MealItem item : meal.getItems()) {
                    if (item.getIngredient().equalsIgnoreCase(foodToReplace)) {
                        double factor = item.getQuantity() / 100.0;
                        for (Map.Entry<String, Double> entry : foodToReplaceNutrients.entrySet()) {
                            after.put(entry.getKey(), after.get(entry.getKey()) - (entry.getValue() * factor));
                        }
                        for (Map.Entry<String, Double> entry : replacementFoodNutrients.entrySet()) {
                            after.put(entry.getKey(), after.getOrDefault(entry.getKey(), 0.0) + (entry.getValue() * factor));
                        }
                    }
                }
            }
        }

        Map<String, Map<String, Double>> result = new HashMap<>();
        result.put("before", before);
        result.put("after", after);
        return result;
    }

    public int applySwapToDateRange(int userId, LocalDate startDate, LocalDate endDate, String oldIngredient, String newIngredient) {
        return MealDAO.applySwapToMealsInDateRange(userId, startDate, endDate, oldIngredient, newIngredient);
    }

    public int applySwapToAllMeals(int userId, String oldIngredient, String newIngredient) {
        return MealDAO.applySwapToAllMeals(userId, oldIngredient, newIngredient);
    }

    public Map<String, List<Double>> getNutrientTrendsPerMeal(int userId, LocalDate startDate, LocalDate endDate, List<String> nutrients) {
        List<Meal> meals = mealDAO.getMealsByUserId(userId);
        Map<String, List<Double>> trends = new HashMap<>();
        
        for (String nutrient : nutrients) {
            trends.put(nutrient, new ArrayList<>());
        }

        for (Meal meal : meals) {
            LocalDate mealDate = meal.getDate();
            if (!mealDate.isBefore(startDate) && !mealDate.isAfter(endDate)) {
                Map<String, Double> mealNutrients = new HashMap<>();
                
                for (MealItem item : meal.getItems()) {
                    int foodId = nutritionDAO.getFoodIdByName(item.getIngredient());
                    if (foodId != -1) {
                        Map<String, Double> itemNutrients = nutritionDAO.getFoodNutrients(foodId);
                        double factor = item.getQuantity() / 100.0;
                        
                        for (Map.Entry<String, Double> entry : itemNutrients.entrySet()) {
                            mealNutrients.put(entry.getKey(),
                                    mealNutrients.getOrDefault(entry.getKey(), 0.0) + (entry.getValue() * factor));
                        }
                    }
                }
                
                for (String nutrient : nutrients) {
                    trends.get(nutrient).add(mealNutrients.getOrDefault(nutrient, 0.0));
                }
            }
        }
        
        return trends;
    }

    public Map<String, Double> getAverageNutrients(int userId, LocalDate startDate, LocalDate endDate) {
        Map<String, Double> totals = getNutrientTotals(userId, startDate, endDate);
        List<Meal> meals = mealDAO.getMealsByUserId(userId);
        
        long mealCount = meals.stream()
                .filter(meal -> !meal.getDate().isBefore(startDate) && !meal.getDate().isAfter(endDate))
                .count();
        
        if (mealCount == 0) {
            return new HashMap<>();
        }
        
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            averages.put(entry.getKey(), entry.getValue() / mealCount);
        }
        
        return averages;
    }
}
