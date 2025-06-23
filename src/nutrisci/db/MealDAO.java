package nutrisci.db;

import java.time.LocalDate;
import java.util.*;
import nutrisci.model.Meal;

public class MealDAO {
    private static MealDAO instance;
    private final Map<String, List<Meal>> database = new HashMap<>();

    private MealDAO() {}

    public static MealDAO getInstance() {
        if (instance == null) instance = new MealDAO();
        return instance;
    }

    public void saveMeal(String userId, Meal meal) {
        database.computeIfAbsent(userId, k -> new ArrayList<>()).add(meal);
    }

    public List<Meal> getMealsByUserAndDate(String userId, LocalDate date) {
        return database.getOrDefault(userId, new ArrayList<>())
                       .stream()
                       .filter(m -> m.getDate().equals(date))
                       .toList();
    }
}
