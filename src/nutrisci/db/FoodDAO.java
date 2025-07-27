package nutrisci.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import nutrisci.model.MealItem;
import nutrisci.model.NutritionalGoal;

public class FoodDAO {
    private static final String DB_URL = DBConnectionHelper.get("DB_URL");
    private static final String DB_USER = DBConnectionHelper.get("DB_USER");
    private static final String DB_PASS = DBConnectionHelper.get("DB_PASSWORD");

    private static class ScoredItem {
        MealItem item;
        int score;
        int foodId;

        ScoredItem(MealItem item, int score, int foodId) {
            this.item = item;
            this.score = score;
            this.foodId = foodId;
        }
    }

    public static MealItem findFoodIdByName(String userInput) {
        if (userInput == null || userInput.isBlank())
            return null;

        String[] tokens = userInput.toLowerCase().split("\\s+");
        List<ScoredItem> scoredResults = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement("SELECT food_id, description_en FROM FoodDescriptions");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int foodId = rs.getInt("food_id");
                String dbName = rs.getString("description_en").toLowerCase();

                int score = calculateScore(userInput.toLowerCase(), tokens, dbName);

                if (score < 4) {
                    MealItem item = new MealItem(userInput, 100); // Keep user input for display
                    scoredResults.add(new ScoredItem(item, score, foodId));
                    System.out.println("? Match: " + userInput + " ? " + rs.getString("description_en") + " [score="
                            + score + "]");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scoredResults.stream()
                .sorted(Comparator.comparingInt(si -> si.score))
                .map(si -> si.item)
                .findFirst()
                .orElse(null);
    }

    public Integer getFoodIdByName(String userInput) {
        if (userInput == null || userInput.isBlank())
            return null;

        String[] tokens = userInput.toLowerCase().split("\s+");
        TreeSet<ScoredItem> results = new TreeSet<>(Comparator.comparingInt(si -> si.score));

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement("SELECT food_id, description_en FROM FoodDescriptions");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int foodId = rs.getInt("food_id");
                String dbName = rs.getString("description_en").toLowerCase();

                int score = calculateScore(userInput.toLowerCase(), tokens, dbName);

                if (score < 4) {
                    results.add(new ScoredItem(new MealItem(userInput, 100), score, foodId));
                    System.out.println("? Match: " + userInput + " ? " + rs.getString("description_en") + " [score="
                            + score + "]");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results.stream().findFirst().map(si -> si.foodId).orElse(null);
    }

    private static int calculateScore(String input, String[] tokens, String dbName) {
        if (dbName.equalsIgnoreCase(input))
            return 0;

        boolean allTokensMatch = Arrays.stream(tokens).allMatch(dbName::contains);
        if (allTokensMatch)
            return 1;

        for (String token : tokens) {
            if (dbName.contains(token))
                return 2;
        }

        return 4;
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
                    SELECT fd.description_en, nd.nutrient_value, cal.nutrient_value AS calories
                    FROM fooddescriptions fd
                    JOIN nutrientdata nd ON fd.food_id = nd.food_id
                    JOIN nutrientdata cal ON cal.food_id = fd.food_id AND cal.nutrient_id = (
                        SELECT nutrient_id FROM nutrients WHERE name_en = 'Energy'
                    )
                    WHERE fd.food_group_id = (
                        SELECT food_group_id FROM fooddescriptions WHERE food_id = ?
                    )
                    AND nd.nutrient_id = (
                        SELECT nutrient_id FROM nutrients WHERE name_en = ?
                    )
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
            double currentValue, String originalIngredientName) {
        String nutrientKey = switch (nutrientName.toLowerCase()) {
            case "calories" -> "Energy";
            case "fat" -> "Fats";
            case "fiber" -> "Fiber";
            case "carbs", "carbohydrates" -> "Carbohydrates";
            case "protein" -> "Protein";
            default -> nutrientName;
        };

        String sql = """
                SELECT fd.description_en, nd.nutrient_value, cal.nutrient_value AS calories
                FROM fooddescriptions fd
                JOIN nutrientdata nd ON fd.food_id = nd.food_id
                JOIN nutrientdata cal ON cal.food_id = fd.food_id AND cal.nutrient_id = (
                    SELECT nutrient_id FROM nutrients WHERE name_en = 'Energy'
                )
                WHERE fd.food_group_id = (
                    SELECT food_group_id FROM fooddescriptions WHERE food_id = ?
                )
                AND nd.nutrient_id = (
                    SELECT nutrient_id FROM nutrients WHERE name_en = ?
                )
                AND fd.description_en NOT LIKE ?
                LIMIT 100
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, originalFoodId);
            stmt.setString(2, nutrientKey);
            stmt.setString(3, "%" + originalIngredientName + "%");

            ResultSet rs = stmt.executeQuery();

            Map<String, Double> orig = NutritionDataDAO.getInstance().getFoodNutrients(originalFoodId);
            double originalCalories = orig.getOrDefault("Energy", 0.0);
            String fallback = null;

            while (rs.next()) {
                String candidate = rs.getString("description_en");
                double nutrientValue = rs.getDouble("nutrient_value");
                double calories = rs.getDouble("calories");

                boolean improves = increase ? nutrientValue > currentValue : nutrientValue < currentValue;
                boolean calorieOk = originalCalories <= 0
                        || Math.abs(calories - originalCalories) / originalCalories <= 0.3;

                if (improves && calorieOk)
                    return candidate;
                if (improves && fallback == null)
                    fallback = candidate;
            }

            return fallback;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    public static List<Map<String, Object>> findSwapCandidates(int originalFoodId, String nutrientName,
            boolean increase, double currentValue, String originalIngredientName) {

        String nutrientKey = switch (nutrientName.toLowerCase()) {
            case "calories" -> "Energy";
            case "fat" -> "Fats";
            case "fiber" -> "Fiber";
            case "carbs", "carbohydrates" -> "Carbohydrates";
            case "protein" -> "Protein";
            default -> nutrientName;
        };

        String sql = """
                SELECT fd.food_id, fd.description_en, nd.nutrient_value, cal.nutrient_value AS calories
                FROM fooddescriptions fd
                JOIN nutrientdata nd ON fd.food_id = nd.food_id
                JOIN nutrientdata cal ON cal.food_id = fd.food_id AND cal.nutrient_id = (
                    SELECT nutrient_id FROM nutrients WHERE name_en = 'Energy'
                )
                WHERE fd.food_group_id = (
                    SELECT food_group_id FROM fooddescriptions WHERE food_id = ?
                )
                AND nd.nutrient_id = (
                    SELECT nutrient_id FROM nutrients WHERE name_en = ?
                )
                AND fd.description_en NOT LIKE ?
                ORDER BY """ + (increase ? "nd.nutrient_value DESC" : "nd.nutrient_value ASC") + """
                LIMIT 50
                """;

        List<Map<String, Object>> candidates = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, originalFoodId);
            stmt.setString(2, nutrientKey);
            stmt.setString(3, "%" + originalIngredientName + "%");

            ResultSet rs = stmt.executeQuery();
            Map<String, Double> orig = NutritionDataDAO.getInstance().getFoodNutrients(originalFoodId);
            double originalCalories = orig.getOrDefault("Energy", 0.0);

            while (rs.next()) {
                Map<String, Object> candidate = new HashMap<>();
                candidate.put("foodId", rs.getInt("food_id"));
                candidate.put("description", rs.getString("description_en"));
                candidate.put("nutrientValue", rs.getDouble("nutrient_value"));
                candidate.put("calories", rs.getDouble("calories"));

                // Calculate similarity score (you can customize this)
                double score = calculateSwapScore(
                        rs.getDouble("nutrient_value"),
                        currentValue,
                        rs.getDouble("calories"),
                        originalCalories,
                        increase);
                candidate.put("score", score);

                candidates.add(candidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Sort by our calculated score
        candidates.sort((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")));
        return candidates;
    }

    private static double calculateSwapScore(double newNutrientValue, double originalValue,
            double newCalories, double originalCalories, boolean increase) {

        // Nutrient improvement factor (how much better is this for our goal)
        double nutrientFactor = increase ? (newNutrientValue - originalValue) / originalValue
                : (originalValue - newNutrientValue) / originalValue;

        // Calorie similarity factor (how close are calories)
        double calorieFactor = 1 - (Math.abs(newCalories - originalCalories) / originalCalories);

        // Combine factors (you can adjust weights)
        return (nutrientFactor * 0.7) + (calorieFactor * 0.3);
    }

    public static List<String> findSimilarFoods(int originalFoodId, String nutrientToOptimize) {
        String sql = """
                SELECT fd.description_en
                FROM fooddescriptions fd
                JOIN nutrientdata nd ON fd.food_id = nd.food_id
                WHERE fd.food_group_id = (
                    SELECT food_group_id FROM fooddescriptions WHERE food_id = ?
                )
                AND nd.nutrient_id = (
                    SELECT nutrient_id FROM nutrients WHERE name_en = ?
                )
                ORDER BY nd.nutrient_value DESC
                LIMIT 10
                """;

        List<String> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, originalFoodId);
            stmt.setString(2, nutrientToOptimize);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("description_en"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static String findSimpleSwap(int originalFoodId, String nutrientName, boolean increase) {
        String nutrientKey = switch (nutrientName.toLowerCase()) {
            case "calories" -> "Energy";
            case "fat" -> "Fat, total";
            case "fiber" -> "Fibre, total dietary";
            case "carbs", "carbohydrates" -> "Carbohydrate, total";
            case "protein" -> "Protein";
            default -> nutrientName;
        };

        String operator = increase ? ">" : "<";
        String order = increase ? "DESC" : "ASC";

        String sql = String.format("""
                SELECT fd.description_en
                FROM fooddescriptions fd
                JOIN nutrientdata nd ON fd.food_id = nd.food_id
                WHERE nd.nutrient_id = (
                    SELECT nutrient_id FROM nutrients WHERE name_en = ?
                )
                AND fd.food_id != ?
                AND nd.nutrient_value %s (
                    SELECT nutrient_value FROM nutrientdata
                    WHERE food_id = ? AND nutrient_id = (
                        SELECT nutrient_id FROM nutrients WHERE name_en = ?
                    )
                )
                ORDER BY nd.nutrient_value %s
                LIMIT 1
                """,
                operator,
                order);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nutrientKey);
            stmt.setInt(2, originalFoodId);
            stmt.setInt(3, originalFoodId);
            stmt.setString(4, nutrientKey);

            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("description_en") : null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}