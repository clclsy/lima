package nutrisci.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FoodGroupAggregator {
    private static final String DB_URL = DBConnectionHelper.get("DB_URL");
    private static final String DB_USER = DBConnectionHelper.get("DB_USER");
    private static final String DB_PASS = DBConnectionHelper.get("DB_PASSWORD");

    public static Map<String, Double> aggregate(int userId, LocalDate start, LocalDate end){
        Map<String, Double> totals = new HashMap<>();
        String sql = """
            SELECT fg.name_en, SUM(mi.quantity) AS total
            FROM meals m
            JOIN meal_items mi ON m.id = mi.meal_id
            JOIN FoodDescriptions fd ON LOWER(fd.description_en) = LOWER(mi.ingredient)
            JOIN FoodGroups fg ON fd.food_group_id = fg.food_group_id
            WHERE m.user_id = ?
            AND m.meal_date BETWEEN ? AND ?
            GROUP BY fg.name_en
        """;
        try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(start));
            stmt.setDate(3, Date.valueOf(end));
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    String groupName = rs.getString("name_en");
                    double qty = rs.getDouble("total");
                    String key = categorize(groupName);
                    if(key != null){
                        totals.merge(key, qty, Double::sum);
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return totals;
    }

    private static String categorize(String groupName){
        String n = groupName.toLowerCase();
        if(n.contains("vegetable") || n.contains("fruit")) return "Vegetables & Fruits";
        if(n.contains("grain") || n.contains("cereal") || n.contains("bread")) return "Grain Products";
        if(n.contains("milk") || n.contains("dairy") || n.contains("cheese") || n.contains("yogurt")) return "Milk & Alternatives";
        if(n.contains("meat") || n.contains("poultry") || n.contains("fish") || n.contains("egg") || 
           n.contains("bean") || n.contains("legume") || n.contains("nut") || n.contains("seed")) return "Meat & Alternatives";
        return "Other";
    }
} 