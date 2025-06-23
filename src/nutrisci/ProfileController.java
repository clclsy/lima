package nutrisci;

import nutrisci.db.UserDAO;

public class ProfileController {
    private final UserDAO userDAO = UserDAO.getInstance();

    public void createProfile(UserProfile profile) {
        userDAO.saveUser(profile);
    }

    public void updateProfile(UserProfile profile) {
        userDAO.updateUser(profile);
    }

    public UserProfile loadProfile(String userId) {
        return userDAO.getUserById(userId);
    }
}
