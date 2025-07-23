package nutrisci.db;

import java.sql.*;
import java.util.Map;
import nutrisci.model.NutritionalGoal;

public class FoodDAO {

    private static final String DB_URL = DBConnectionHelper.get("DB_URL");
    private static final String DB_USER = DBConnectionHelper.get("DB_USER");
    private static final String DB_PASS = DBConnectionHelper.get("DB_PASSWORD");

    public static Integer getFoodIdByName(String foodName) {
        String sql = "SELECT food_id FROM FoodDescriptions WHERE LOWER(description_en) LIKE LOWER(?) LIMIT 1";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + foodName + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("food_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String findSwapCandidate(int originalFoodId, NutritionalGoal goal) {
        String nutrientKey = switch (goal.getNutrientName().toLowerCase()) {
            case "calories" -> "Energy";
            case "fat" -> "Fats";
            case "fiber" -> "Fiber";
            case "carbs", "carbohydrates" -> "Carbohydrates";
            case "protein" -> "Protein";
            default -> goal.getNutrientName();
        };

        String sql = """
                    SELECT fd.description_en, nd.nutrient_value AS target_value, e.nutrient_value AS calories
                    FROM FoodDescriptions fd
                    JOIN NutrientData nd ON fd.food_id = nd.food_id
                    JOIN NutrientData e ON e.food_id = fd.food_id AND e.nutrient_id = (
                        SELECT nutrient_id FROM Nutrients WHERE name_en = 'Energy'
                    )
                    WHERE nd.nutrient_id = (
                        SELECT nutrient_id FROM Nutrients WHERE name_en = ?
                    )
                    AND fd.food_id != ?
                    ORDER BY
                        CASE WHEN ? THEN nd.nutrient_value ELSE -nd.nutrient_value END DESC
                    LIMIT 100
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nutrientKey);
            stmt.setInt(2, originalFoodId);
            stmt.setBoolean(3, goal.isIncrease());

            ResultSet rs = stmt.executeQuery();

            Map<String, Double> orig = NutritionDataDAO.getInstance().getFoodNutrients(originalFoodId);
            double originalTarget = orig.getOrDefault(nutrientKey, 0.0);
            double originalCalories = orig.getOrDefault("Energy", 0.0);

            String fallback = null;

            while (rs.next()) {
                String candidate = rs.getString("description_en");
                double targetValue = rs.getDouble("target_value");
                double calories = rs.getDouble("calories");

                boolean improves = goal.isIncrease() ? targetValue > originalTarget : targetValue < originalTarget;

                if (!improves)
                    continue;

                // Relax calorie constraint to 30%
                boolean caloriesOk = originalCalories <= 0 ||
                        Math.abs(calories - originalCalories) / originalCalories <= 0.30;

                if (caloriesOk)
                    return candidate;

                if (fallback == null)
                    fallback = candidate; // Save as backup
            }

            return (fallback != null) ? fallback : "No better match found";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error finding swap";
        }
    }

    public static String findSwapCandidateRelaxed(int originalFoodId, String nutrientName, boolean increase,
            double currentValue) {
        String sql = """
                    SELECT f.name_en, nd.nutrient_value
                    FROM Foods f
                    JOIN NutrientData nd ON f.food_id = nd.food_id
                    WHERE f.food_group_id = (
                        SELECT food_group_id FROM Foods WHERE food_id = ?
                    )
                    AND nd.nutrient_id = (
                        SELECT nutrient_id FROM Nutrients WHERE name_en = ?
                    )
                    LIMIT 50
                """;

        try (Connection conn = DriverManager.getConnection(
                DBConnectionHelper.get("DB_URL"),
                DBConnectionHelper.get("DB_USER"),
                DBConnectionHelper.get("DB_PASSWORD"));
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, originalFoodId);
            stmt.setString(2, nutrientName);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String candidate = rs.getString("name_en");
                double value = rs.getDouble("nutrient_value");

                boolean improves = increase ? value > currentValue : value < currentValue;
                if (improves)
                    return candidate;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "No better match found";
    }

    public static void searchFoods(String searchTerm) {
        String sql = "SELECT food_id, description_en FROM FoodDescriptions WHERE description_en LIKE ? LIMIT 10";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            System.out.println("Searching for foods containing: " + searchTerm);
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Found: " + rs.getString("description_en") +
                        " (ID: " + rs.getInt("food_id") + ")");
            }

            if (!found) {
                System.out.println("No matching foods found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
