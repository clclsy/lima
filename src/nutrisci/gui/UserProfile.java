package nutrisci.gui;

import java.io.*;
import java.util.*;

public class UserProfile implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private final String name, dob;
    private String gender, height, weight, unit;
    private List<UserMeals> meals;
    private static List<UserProfile> savedProfiles = new ArrayList<>();
    private static final String PROFILE_FILE = "profiles.dat";

    // Constructor
    public UserProfile(String name, String dob, String gender, String height, String weight, String unit) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.unit = unit;
        this.meals = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getUnit() {
        return unit;
    }

    public void update(String newGender, String newHeight, String newWeight, String newUnit) {
        this.gender = newGender;
        this.height = newHeight;
        this.weight = newWeight;
        this.unit = newUnit;
    }

    public static void addProfile(UserProfile p) {
        savedProfiles.add(p);
    }

    public static void deleteProfile(UserProfile profile) {
        savedProfiles.remove(profile);
    }

    public static List<UserProfile> getProfiles() {
        return savedProfiles;
    }

    public static void seedTestProfilesIfEmpty() {
        if (savedProfiles.isEmpty()) {
            savedProfiles.add(new UserProfile("Alice", "2000-01-01", "Female", "165", "60", "Metric"));
            savedProfiles.add(new UserProfile("Bob", "1995-05-15", "Male", "180", "75", "Metric"));
            savedProfiles.add(new UserProfile("Charlie", "1998-10-10", "Other", "170", "70", "Imperial"));
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void saveProfilesToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PROFILE_FILE))) {
            out.writeObject(savedProfiles);
            System.out.println("✔ Profiles saved:");
            for (UserProfile p : savedProfiles) {
                System.out.println("- " + p.getName() + " has " + p.getMeals().size() + " meals");
            }
        } catch (IOException e) {
            System.out.println("❌ Failed to save profiles:");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadProfilesFromFile() {
        File file = new File(PROFILE_FILE);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                savedProfiles = (List<UserProfile>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("❌ Error loading profiles: " + e.getMessage());
            }
        }
    }
    

    public List<UserMeals> getMeals() {
        if (meals == null) {
            meals = new ArrayList<>();
        }
        return meals;
    }

    public void addMeal(UserMeals meal) {
        if (meals == null) {
            meals = new ArrayList<>();
        }
        meals.add(meal);
    }

}
