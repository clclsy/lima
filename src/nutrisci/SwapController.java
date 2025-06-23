package nutrisci;

import java.time.LocalDate;
import java.util.*;

public class SwapController {
    private final MealLogger mealLogger = new MealLogger();
    private final SmartSwapEngine swapEngine = new SmartSwapEngine();

    public List<FoodSwap> generateSuggestions(String userId, LocalDate date, List<NutritionalGoal> goals) {
        List<Meal> meals = mealLogger.getMealsForDate(userId, date);
        List<FoodSwap> swaps = new ArrayList<>();

        for (Meal meal : meals) {
            swaps.addAll(swapEngine.suggestSwaps(meal, goals));
        }

        return swaps;
    }
}
