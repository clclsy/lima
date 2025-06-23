package nutrisci.db;

import nutrisci.UserProfile;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static UserDAO instance;
    private final Map<String, UserProfile> database = new HashMap<>();

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) instance = new UserDAO();
        return instance;
    }

    public void saveUser(UserProfile profile) {
        database.put(profile.getUserId(), profile);
    }

    public void updateUser(UserProfile profile) {
        database.put(profile.getUserId(), profile);
    }

    public UserProfile getUserById(String userId) {
        return database.get(userId);
    }
}

