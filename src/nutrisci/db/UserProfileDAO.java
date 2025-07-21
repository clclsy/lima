package nutrisci.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import nutrisci.model.UserProfile;

public class UserProfileDAO {
    private static final String DB_URL = DBConnectionHelper.get("DB_URL");
    private static final String DB_USER = DBConnectionHelper.get("DB_USER");
    private static final String DB_PASS = DBConnectionHelper.get("DB_PASSWORD");

    public static void insertProfile(UserProfile profile) {
        String sql = "INSERT INTO user_profiles (name, dob, gender, height, weight, unit) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, profile.getName());
            stmt.setDate(2, Date.valueOf(profile.getDob()));
            stmt.setString(3, profile.getGender());
            stmt.setDouble(4, profile.getHeight());
            stmt.setDouble(5, profile.getWeight());
            stmt.setString(6, profile.getUnit());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                profile.setId(keys.getInt(1)); // Set the auto-generated ID into object
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateProfile(UserProfile p) {
        String sql = "UPDATE user_profiles SET gender = ?, height = ?, weight = ?, unit = ? WHERE name = ? AND dob = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getGender());
            stmt.setDouble(2, p.getHeight());
            stmt.setDouble(3, p.getWeight());
            stmt.setString(4, p.getUnit());
            stmt.setString(5, p.getName());
            stmt.setDate(6, Date.valueOf(p.getDob())); // Convert LocalDate to SQL Date

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }

    public static List<UserProfile> getAllProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM user_profiles";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UserProfile p = new UserProfile(
                        rs.getString("name"),
                        rs.getDate("dob").toLocalDate(), // Convert SQL Date to LocalDate
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

    public static boolean deleteProfile(UserProfile p) {
        String sql = "DELETE FROM user_profiles WHERE name = ? AND dob = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getName());
            stmt.setDate(2, Date.valueOf(p.getDob())); // Convert LocalDate to SQL Date

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting profile: " + e.getMessage());
            return false;
        }
    }
}