package nutrisci.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import nutrisci.model.UserProfile;

public class UserProfileDAO {
    private static final String DB_URL = DBConnectionHelper.get("DB_URL");
    private static final String DB_USER = DBConnectionHelper.get("DB_USER");
    private static final String DB_PASS = DBConnectionHelper.get("DB_PASSWORD");

    public static void insertProfile(UserProfile p) {
        String sql = "INSERT INTO user_profiles (name, dob, gender, height, weight, unit) VALUES (?, ?, ?, ?, ?, ?)";
        // try-with-resources automatically closes the connection and statement
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setString(2, p.getDob());
            stmt.setString(3, p.getGender());
            stmt.setDouble(4, p.getHeight());
            stmt.setDouble(5, p.getWeight());
            stmt.setString(6, p.getUnit());

            stmt.executeUpdate();
            System.out.println("✅ Profile inserted: " + p.getName());

        } catch (SQLException e) {
            System.err.println("❌ Failed to insert profile: " + e.getMessage());
        }
    }

    public static List<UserProfile> getAllProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM user_profiles";
        // try-with-resources automatically closes all three resources
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Create a new UserProfile object using data from the current row
                UserProfile p = new UserProfile(
                        rs.getString("name"),
                        rs.getString("dob"),
                        rs.getString("gender"),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getString("unit"));
                profiles.add(p);
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to retrieve profiles: " + e.getMessage());
        }
        return profiles;
    }
}
