package nutrisci.db;

import java.sql.*;
import java.util.Map;
import nutrisci.model.NutritionalGoal;

public class FoodDAO {
    public static Integer getFoodIdByName(String foodName) {
        String sql = "SELECT food_id FROM Foods WHERE LOWER(name_en) = LOWER(?)";

        try (Connection conn = DriverManager.getConnection(
                DBConnectionHelper.get("DB_URL"),
                DBConnectionHelper.get("DB_USER"),
                DBConnectionHelper.get("DB_PASSWORD"));
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, foodName);
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
            case "fat" -> "Fat, total";
            case "fiber" -> "Fibre, total dietary";
            case "carbs", "carbohydrates" -> "Carbohydrate, total";
            case "protein" -> "Protein";
            default -> goal.getNutrientName();
        };

        String sql = """
                    SELECT f.name_en, nd.nutrient_value AS target_value, e.nutrient_value AS calories
                    FROM Foods f
                    JOIN NutrientData nd ON f.food_id = nd.food_id
                    JOIN NutrientData e ON e.food_id = f.food_id
                    WHERE f.food_group_id = (
                        SELECT food_group_id FROM Foods WHERE food_id = ?
                    )
                    AND nd.nutrient_id = (
                        SELECT nutrient_id FROM Nutrients WHERE name_en = ?
                    )
                    AND e.nutrient_id = (
                        SELECT nutrient_id FROM Nutrients WHERE name_en = 'Energy'
                    )
                    LIMIT 50
                """;

        try (Connection conn = DriverManager.getConnection(
                DBConnectionHelper.get("DB_URL"),
                DBConnectionHelper.get("DB_USER"),
                DBConnectionHelper.get("DB_PASSWORD"));
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, originalFoodId);
            stmt.setString(2, nutrientKey);

            ResultSet rs = stmt.executeQuery();

            // Get original nutrient & calorie values
            NutritionDataDAO dao = NutritionDataDAO.getInstance();
            Map<String, Double> orig = dao.getFoodNutrients(originalFoodId);
            double originalTarget = orig.getOrDefault(nutrientKey, 0.0);
            double originalCalories = orig.getOrDefault("Energy", 0.0);

            while (rs.next()) {
                String candidate = rs.getString("name_en");
                double targetValue = rs.getDouble("target_value");
                double calories = rs.getDouble("calories");

                boolean caloriesOk = Math.abs(calories - originalCalories) / originalCalories <= 0.10;
                boolean improves = goal.isIncrease() ? targetValue > originalTarget : targetValue < originalTarget;

                if (caloriesOk && improves) {
                    return candidate;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "No better match found";
    }
}
