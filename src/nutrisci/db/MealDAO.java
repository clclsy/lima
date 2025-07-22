package nutrisci.db;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import nutrisci.model.Meal;
import nutrisci.model.MealItem;
import nutrisci.model.MealType;

public class MealDAO {
    private static final String DB_URL = DBConnectionHelper.get("DB_URL");
    private static final String DB_USER = DBConnectionHelper.get("DB_USER");
    private static final String DB_PASS = DBConnectionHelper.get("DB_PASSWORD");

    @SuppressWarnings("CallToPrintStackTrace")
    public static void insertMeal(int userId, Meal meal) {
        String sqlMeal = "INSERT INTO meals(user_id, meal_date, meal_type) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO meal_items(meal_id, ingredient, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement mealStmt = conn.prepareStatement(sqlMeal, Statement.RETURN_GENERATED_KEYS)) {

            mealStmt.setInt(1, userId);
            mealStmt.setDate(2, Date.valueOf(meal.getDate()));
            mealStmt.setString(3, meal.getType().toString());
            mealStmt.executeUpdate();

            ResultSet rs = mealStmt.getGeneratedKeys();
            if (rs.next()) {
                int mealId = rs.getInt(1);
                try (PreparedStatement itemStmt = conn.prepareStatement(sqlItem)) {
                    for (MealItem item : meal.getItems()) {
                        itemStmt.setInt(1, mealId);
                        itemStmt.setString(2, item.getIngredient());
                        itemStmt.setDouble(3, item.getQuantity());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static List<Meal> getMealsByUserId(int userId) {
        String sql = """
            SELECT m.id, m.meal_date, m.meal_type, i.ingredient, i.quantity
            FROM meals m
            LEFT JOIN meal_items i ON m.id = i.meal_id
            WHERE m.user_id = ?
            ORDER BY m.meal_date DESC
        """;

        List<Meal> meals = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            Map<Integer, Meal> mealMap = new HashMap<>();

            while (rs.next()) {
                int mealId = rs.getInt("id");
                Meal meal = mealMap.get(mealId);

                if (meal == null) {
                    LocalDate date = rs.getDate("meal_date").toLocalDate();
                    MealType type = MealType.valueOf(rs.getString("meal_type").toUpperCase());
                    meal = new Meal(userId, date, type, new ArrayList<>());
                    mealMap.put(mealId, meal);
                    meals.add(meal);
                }

                String ingredient = rs.getString("ingredient");
                if (ingredient != null) {
                    MealItem item = new MealItem(
                            ingredient,
                            rs.getDouble("quantity")
                    );
                    meal.getItems().add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meals;
    }

}
