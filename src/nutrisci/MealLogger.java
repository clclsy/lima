package nutrisci;

import nutrisci.db.MealDAO;
import java.time.LocalDate;
import java.util.List;

public class MealLogger {
    private final MealDAO mealDAO = MealDAO.getInstance();

    public void logMeal(String userId, Meal meal) {
        mealDAO.saveMeal(userId, meal);
    }

    public List<Meal> getMealsForDate(String userId, LocalDate date) {
        return mealDAO.getMealsByUserAndDate(userId, date);
    }
}
