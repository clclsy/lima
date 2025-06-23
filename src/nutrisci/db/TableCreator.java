package nutrisci.db;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TableCreator {
    public static void main(String[] args) {
        String dbUrl = DBConnectionHelper.get("DB_URL");
        String user = DBConnectionHelper.get("DB_USER");
        String password = DBConnectionHelper.get("DB_PASSWORD");
        
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader("src/nutrisci/db/db.sql"))) {
            
            StringBuilder sqlContent = new StringBuilder();
            String line;
            
            while ((line = br.readLine()) != null) {
                sqlContent.append(line).append("\n");
            }
            
            String[] statements = sqlContent.toString().split(";");
            
            for (String sql : statements) {
                sql = sql.trim();
                if (!sql.isEmpty()) {
                    try {
                        stmt.execute(sql);
                        System.out.println("Executed: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                    } catch (Exception e) {
                        System.err.println("Error executing: " + sql.substring(0, Math.min(50, sql.length())));
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Database tables created successfully!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 