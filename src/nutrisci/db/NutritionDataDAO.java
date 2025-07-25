package nutrisci.db;

import java.sql.*;
import java.util.*;

public class NutritionDataDAO {
    private static NutritionDataDAO instance;
    
    private NutritionDataDAO() {}
    
    public static NutritionDataDAO getInstance() {
        if (instance == null) {
            instance = new NutritionDataDAO();
        }
        return instance;
    }
    
    private Connection getConnection() throws SQLException {
        String dbUrl = DBConnectionHelper.get("DB_URL");
        String user = DBConnectionHelper.get("DB_USER");
        String password = DBConnectionHelper.get("DB_PASSWORD");
        
        return DriverManager.getConnection(dbUrl, user, password);
    }
    
    public Map<String, Double> getFoodNutrients(int foodId) {
        Map<String, Double> nutrients = new HashMap<>();
        
        String sql = """
            SELECT n.name_en, nd.nutrient_value, n.unit 
            FROM NutrientData nd 
            JOIN Nutrients n ON nd.nutrient_id = n.nutrient_id 
            WHERE nd.food_id = ? 
            AND nd.nutrient_value IS NOT NULL
            AND n.name_en IN ('Protein', 'Carbohydrate, total', 'Fat, total', 'Fibre, total dietary', 
                             'Calcium', 'Iron', 'Vitamin C', 'Vitamin A', 'Energy')
            ORDER BY n.name_en
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, foodId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nutrientName = rs.getString("name_en");
                    double value = rs.getDouble("nutrient_value");
                    String unit = rs.getString("unit");

                    String displayName = simplifyNutrientName(nutrientName);
                    nutrients.put(displayName, value);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching nutrients for food ID " + foodId + ": " + e.getMessage());
        }
        
        return nutrients;
    }
    
    public Map<String, Object> getSampleFoodWithNutrients() {
        Map<String, Object> result = new HashMap<>();
        
        String sql = """
            SELECT fd.food_id, fd.description_en, fg.name_en as food_group
            FROM FoodDescriptions fd 
            JOIN FoodGroups fg ON fd.food_group_id = fg.food_group_id
            WHERE fd.description_en LIKE '%chicken%' 
            OR fd.description_en LIKE '%beef%'
            OR fd.description_en LIKE '%salmon%'
            LIMIT 1
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int foodId = rs.getInt("food_id");
                String foodName = rs.getString("description_en");
                String foodGroup = rs.getString("food_group");
                
                result.put("foodId", foodId);
                result.put("foodName", foodName);
                result.put("foodGroup", foodGroup);
                result.put("nutrients", getFoodNutrients(foodId));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sample food: " + e.getMessage());
        }
        
        return result;
    }
    
    public Map<String, Double> getMacronutrientBreakdown(int foodId) {
        Map<String, Double> macros = new HashMap<>();
        
        String sql = """
            SELECT n.name_en, nd.nutrient_value 
            FROM NutrientData nd 
            JOIN Nutrients n ON nd.nutrient_id = n.nutrient_id 
            WHERE nd.food_id = ? 
            AND n.name_en IN ('Protein', 'Carbohydrate, total', 'Fat, total')
            AND nd.nutrient_value IS NOT NULL
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, foodId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nutrientName = rs.getString("name_en");
                    double value = rs.getDouble("nutrient_value");
                    
                    String displayName = simplifyNutrientName(nutrientName);
                    macros.put(displayName, value);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching macronutrients for food ID " + foodId + ": " + e.getMessage());
        }
        
        return macros;
    }
    
    public List<Map<String, Object>> getAvailableFoods(int limit) {
        List<Map<String, Object>> foods = new ArrayList<>();
        
        String sql = """
            SELECT DISTINCT fd.food_id, fd.description_en, fg.name_en as food_group
            FROM FoodDescriptions fd 
            JOIN FoodGroups fg ON fd.food_group_id = fg.food_group_id
            JOIN NutrientData nd ON fd.food_id = nd.food_id
            WHERE fd.description_en IS NOT NULL 
            AND fd.description_en != ''
            ORDER BY fd.description_en
            LIMIT ?
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> food = new HashMap<>();
                    food.put("foodId", rs.getInt("food_id"));
                    food.put("foodName", rs.getString("description_en"));  
                    food.put("foodGroup", rs.getString("food_group"));
                    foods.add(food);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching available foods: " + e.getMessage());
        }
        
        return foods;
    }
    
    private String simplifyNutrientName(String nutrientName) {
        return switch (nutrientName) {
            case "Carbohydrate, total" -> "Carbohydrates";
            case "Fat, total" -> "Fats";
            case "Fibre, total dietary" -> "Fiber";
            case "Vitamin C" -> "Vitamin C";
            case "Vitamin A" -> "Vitamin A";
            default -> nutrientName;
        };
    }

    public int getFoodIdByName(String foodName) {
        String sql = "SELECT food_id FROM FoodDescriptions WHERE description_en = ? LIMIT 1";
        int foodId = -1; // Return -1 if not found

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, foodName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    foodId = rs.getInt("food_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching food ID for name " + foodName + ": " + e.getMessage());
        }
        return foodId;
    }
}
