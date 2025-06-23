package nutrisci;

import java.util.*;

public class SmartSwapEngine {
    public List<FoodSwap> suggestSwaps(Meal meal, List<NutritionalGoal> goals) {
        List<FoodSwap> swaps = new ArrayList<>();

        for (NutritionalGoal goal : goals) {
            for (MealEntry entry : meal.getEntries()) {
                if (goal.getNutrientName().equals("fiber") && goal.isIncrease()) {
                    swaps.add(new FoodSwap(entry.getFoodName(), "Oats", goal.getDescription()));
                }
            }
        }

        return swaps;
    }
}
