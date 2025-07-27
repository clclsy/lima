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

    public static void insertMeal(int userId, Meal meal) {
        // Check if meal type is snack - if not, verify no existing meal of same type
        // and date
        if (meal.getType() != MealType.SNACK) {
            String checkSql = "SELECT id FROM meals WHERE user_id = ? AND meal_date = ? AND meal_type = ?";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                checkStmt.setInt(1, userId);
                checkStmt.setDate(2, Date.valueOf(meal.getDate()));
                checkStmt.setString(3, meal.getType().toString());

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    throw new IllegalArgumentException("You can only have one " + meal.getType() + " per day");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
            throw new RuntimeException("Database error while inserting meal", e);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static List<Meal> getMealsByUserId(int userId) {
        String sql = """
                    SELECT m.id, m.user_id, m.meal_date, m.meal_type, i.ingredient, i.quantity
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
                    int actualUserId = rs.getInt("user_id"); // ✅ pull from DB
                    LocalDate date = rs.getDate("meal_date").toLocalDate();
                    MealType type = MealType.valueOf(rs.getString("meal_type").toUpperCase());
                    meal = new Meal(actualUserId, date, type, new ArrayList<>());
                    mealMap.put(mealId, meal);
                    meals.add(meal);
                }

                String ingredient = rs.getString("ingredient");
                if (ingredient != null) {
                    MealItem item = new MealItem(
                            ingredient,
                            rs.getDouble("quantity"));
                    meal.getItems().add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return meals;
    }

    public static Map<LocalDate, Map<String, Double>> getDailyNutrientAverages(int userId) {
        Map<LocalDate, Map<String, Double>> result = new TreeMap<>();

        String sql = """
                    SELECT m.meal_date, n.name_en AS nutrient, SUM(nd.nutrient_value) AS total
                    FROM meals m
                    JOIN meal_items mi ON m.id = mi.meal_id
                    JOIN FoodDescriptions f ON LOWER(f.name_en) = LOWER(mi.ingredient)
                    JOIN nutrientdata nd ON nd.food_id = f.food_id
                    JOIN nutrients n ON nd.nutrient_id = n.nutrient_id
                    WHERE m.user_id = ?
                    AND n.name_en IN ('Protein', 'Carbohydrate, total', 'Fat, total', 'Energy')
                    GROUP BY m.meal_date, n.name_en
                    ORDER BY m.meal_date ASC
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("meal_date").toLocalDate();
                String nutrient = rs.getString("nutrient");
                double value = rs.getDouble("total");

                result.computeIfAbsent(date, d -> new HashMap<>()).put(nutrient, value);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}