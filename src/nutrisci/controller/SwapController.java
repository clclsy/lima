package nutrisci.controller;

import java.util.*;
import nutrisci.db.MealDAO;
import nutrisci.model.*;

public class SwapController {
    private final SmartSwapEngine swapEngine;

    public SwapController() {
        this.swapEngine = new SmartSwapEngine();
    }

    public List<FoodSwap> getSwapSuggestionsForMeal(Meal meal, List<NutritionalGoal> goals) {
        return swapEngine.suggestSwapsForMeal(meal, goals);
    }

    public List<FoodSwap> getSwapSuggestionsForUser(int userId, List<NutritionalGoal> goals) {
        List<Meal> allMeals = MealDAO.getMealsByUserId(userId);
        List<FoodSwap> allSuggestions = new ArrayList<>();
        for (Meal meal : allMeals) {
            allSuggestions.addAll(swapEngine.suggestSwapsForMeal(meal, goals));
        }
        return allSuggestions;
    }
}