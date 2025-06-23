package nutrisci.controller;

import java.io.*;
import java.util.*;
import nutrisci.model.UserProfile;

public class ProfileController {
    private static final String FILE_PATH = "profiles.dat";
    private static List<UserProfile> profiles = new ArrayList<>();

    public static void saveProfile(UserProfile profile) {
        profiles.add(profile);
        saveProfilesToFile();
    }

    public static List<UserProfile> loadProfiles() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            profiles = (List<UserProfile>) in.readObject();
        } catch (Exception ignored) {}
        return profiles;
    }

    private static void saveProfilesToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            out.writeObject(profiles);
        } catch (IOException e) {
        }
    }
}

