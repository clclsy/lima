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

    // testing purposes
    public static String getFoodNameById(int foodId) {
        String sql = "SELECT description_en FROM FoodDescriptions WHERE food_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, foodId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("description_en");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer findFoodIdByName(String userInput) {
        if (userInput == null || userInput.isBlank())
            return null;

        String[] tokens = userInput.toLowerCase().split("\\s+");
        ScoredItem bestMatch = null;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement("SELECT food_id, description_en FROM FoodDescriptions");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int foodId = rs.getInt("food_id");
                String dbName = rs.getString("description_en").toLowerCase();

                int score = calculateScore(userInput.toLowerCase(), tokens, dbName);

                if (score < 4 && (bestMatch == null || score < bestMatch.score)) {
                    MealItem matchedItem = new MealItem(dbName, 100); // store matched description
                    bestMatch = new ScoredItem(matchedItem, score, foodId);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (bestMatch != null) {
            System.out.println("✅ Matched '" + userInput + "' → '" + bestMatch.item.getIngredient() + "' (food_id="
                    + bestMatch.foodId + ", score=" + bestMatch.score + ")");
            return bestMatch.foodId;
        }

        return null;
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

                if (score <= 2) {
                    results.add(new ScoredItem(new MealItem(userInput, 100), score, foodId));
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

        // Strong match if all tokens match and dbName starts with one of them
        boolean allTokensPresent = Arrays.stream(tokens).allMatch(dbName::contains);
        boolean startsWithInput = dbName.startsWith(tokens[0]);

        if (allTokensPresent && startsWithInput)
            return 1;

        // Moderate match if all tokens are somewhere
        if (allTokensPresent)
            return 2;

        // Weak match if any token is present
        for (String token : tokens) {
            if (dbName.contains(token))
                return 3;
        }

        // Otherwise, bad match
        return 5;
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
        SELECT fd.food_id, fd.description_en
        FROM fooddescriptions fd
        JOIN nutrientdata nd ON fd.food_id = nd.food_id
        WHERE nd.nutrient_id = (
            SELECT nutrient_id FROM nutrients WHERE name_en = ?
        )
        AND fd.food_id != ?
        AND fd.food_group_id = (
            SELECT food_group_id FROM fooddescriptions WHERE food_id = ?
        )
        AND fd.description_en NOT REGEXP 'powder|gelatin|supplement|sweets|infant|baby|formula'
        AND nd.nutrient_value %s (
            SELECT nutrient_value FROM nutrientdata
            WHERE food_id = ? AND nutrient_id = (
                SELECT nutrient_id FROM nutrients WHERE name_en = ?
            )
        )
        ORDER BY nd.nutrient_value %s
        LIMIT 20
        """, operator, order);

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, nutrientKey);
        stmt.setInt(2, originalFoodId);
        stmt.setInt(3, originalFoodId);
        stmt.setInt(4, originalFoodId);
        stmt.setString(5, nutrientKey);

        ResultSet rs = stmt.executeQuery();

        Map<String, Double> originalNutrients = NutritionDataDAO.getInstance().getFoodNutrients(originalFoodId);

        while (rs.next()) {
            int candidateId = rs.getInt("food_id");
            String candidateName = rs.getString("description_en");

            Map<String, Double> candidateNutrients = NutritionDataDAO.getInstance().getFoodNutrients(candidateId);

            if (candidateNutrients == null || candidateNutrients.isEmpty()) {
                System.out.println("Skipped " + candidateName + ": no nutrient data");
                continue;
            }

            Double origValue = originalNutrients.getOrDefault(nutrientKey, 0.0);
            Double candidateValue = candidateNutrients.getOrDefault(nutrientKey, 0.0);
            if (increase && candidateValue <= origValue) {
                System.out.println("Skipped " + candidateName + ": does not increase " + nutrientKey);
                continue;
            }
            if (!increase && candidateValue >= origValue) {
                System.out.println("Skipped " + candidateName + ": does not decrease " + nutrientKey);
                continue;
            }

            boolean similar = true;
            for (Map.Entry<String, Double> entry : originalNutrients.entrySet()) {
                String key = entry.getKey();
                if (key.equalsIgnoreCase(nutrientKey)) continue;

                double orig = entry.getValue();
                double cand = candidateNutrients.getOrDefault(key, orig);

                double delta = Math.abs(cand - orig) / (orig == 0 ? 1 : orig);
                if (delta > 0.10) {
                    similar = false;
                    System.out.printf("Skipped %s: %s differs too much (%.2f vs %.2f)\n",
                            candidateName, key, cand, orig);
                    break;
                }
            }

            if (!similar) continue;

            System.out.println("Found swap: " + candidateName);
            return candidateName;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}


}