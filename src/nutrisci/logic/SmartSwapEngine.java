package nutrisci.logic;

import java.util.*;
import nutrisci.model.*;

public class SmartSwapEngine {
    // This is a mocked version. You should replace it with real logic later.
    public static List<String> suggestSwaps(Meal meal, List<NutritionalGoal> goals) {
        List<String> suggestions = new ArrayList<>();

        for (MealEntry entry : meal.getEntries()) {
            for (NutritionalGoal goal : goals) {
                if (goal.getNutrientName().equalsIgnoreCase("fiber") && goal.isIncrease()) {
                    if (entry.getFoodName().toLowerCase().contains("white rice")) {
                        suggestions.add("Swap white rice with brown rice");
                    } else if (entry.getFoodName().toLowerCase().contains("bread")) {
                        suggestions.add("Swap bread with whole grain bread");
                    }
                }
            }
        }

        if (suggestions.isEmpty()) {
            suggestions.add("No suggestions available for this meal/goal.");
        }

        return suggestions;
    }
}
