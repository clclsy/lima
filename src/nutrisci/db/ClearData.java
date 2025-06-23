package nutrisci.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ClearData {
    public static void main(String[] args) {
        String dbUrl = DBConnectionHelper.get("DB_URL");
        String user = DBConnectionHelper.get("DB_USER");
        String password = DBConnectionHelper.get("DB_PASSWORD");
        
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            String[] tables = {
                "ConversionFactors", "NutrientData", "RefuseAmounts", "Yields",
                "FoodDescriptions", "FoodGroups", "FoodSources", "Nutrients", 
                "NutrientSources", "Measures", "RefuseDescriptions", "YieldDescriptions"
            };
            
            for (String table : tables) {
                try {
                    stmt.execute("TRUNCATE TABLE " + table);
                    System.out.println("Cleared table: " + table);
                } catch (Exception e) {
                    System.err.println("Error clearing table " + table + ": " + e.getMessage());
                }
            }
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("All tables cleared successfully!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 