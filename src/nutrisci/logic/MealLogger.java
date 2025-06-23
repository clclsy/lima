package nutrisci.logic;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import nutrisci.model.Meal;

public class MealLogger {
    private static final String FILE_PATH = "meals.dat";
    private static Map<String, List<Meal>> mealMap = new HashMap<>();

    public static void logMeal(String userName, Meal meal) {
        List<Meal> meals = mealMap.getOrDefault(userName, new ArrayList<>());
        meals.add(meal);
        mealMap.put(userName, meals);
        save();
    }

    public static List<Meal> getMealsForDate(String userName, LocalDate date) {
        load();  // ensure we have latest data
        List<Meal> meals = mealMap.getOrDefault(userName, new ArrayList<>());
        List<Meal> filtered = new ArrayList<>();
        for (Meal m : meals) {
            if (m.getDate().equals(date)) filtered.add(m);
        }
        return filtered;
    }

    private static void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(mealMap);
        } catch (IOException e) {
        }
    }

    private static void load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            mealMap = (Map<String, List<Meal>>) in.readObject();
        } catch (Exception ignored) {}
    }
}
