package nutrisci.controller;

import java.util.*;
import nutrisci.db.MealDAO;
import nutrisci.model.*;

public class SwapController {
    private final SmartSwapEngine swapEngine;

    public SwapController() {
        this.swapEngine = new SmartSwapEngine();
    }

    public List<FoodSwap> getSwapSuggestions(int userId, NutritionalGoal goal) {
        List<Meal> allMeals = MealDAO.getMealsByUserId(userId);
        return swapEngine.suggestSwaps(allMeals, List.of(goal));
    }
}
