package nutrisci.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        String dbUrl = DBConnectionHelper.get("DB_URL");
        String user = DBConnectionHelper.get("DB_USER");
        String password = DBConnectionHelper.get("DB_PASSWORD");
        
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }
} 