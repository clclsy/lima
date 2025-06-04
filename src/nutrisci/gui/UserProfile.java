package nutrisci.gui;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name, dob;
    private String sex, height, weight, unit;
    private List<Meal> meals = new ArrayList<>();

    private static List<UserProfile> savedProfiles = new ArrayList<>();
    private static final String PROFILE_FILE = "profiles.dat";

    // Constructor
    public UserProfile(String name, String dob, String sex, String height, String weight, String unit) {
        this.name = name;
        this.dob = dob;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.unit = unit;
    }

    // ======= INNER MEAL CLASS =======
    public static class Meal implements Serializable {
        private static final long serialVersionUID = 1L;

        private String date, type;
        private List<String> ingredients;

        public Meal(String date, String type, List<String> ingredients) {
            this.date = date;
            this.type = type;
            this.ingredients = ingredients;
        }

        public String getDate() { return date; }
        public String getType() { return type; }
        public List<String> getIngredients() { return ingredients; }
    }

    // ======= GETTERS =======
    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getSex() { return sex; }
    public String getHeight() { return height; }
    public String getWeight() { return weight; }
    public String getUnit() { return unit; }
    public List<Meal> getMeals() { return meals; }

    // ======= PROFILE OPERATIONS =======
    public void update(String newSex, String newHeight, String newWeight, String newUnit) {
        this.sex = newSex;
        this.height = newHeight;
        this.weight = newWeight;
        this.unit = newUnit;
    }

    public void addMeal(Meal meal) {
        meals.add(meal);
    }

    // ======= PROFILE LIST MANAGEMENT =======
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

    // ======= FILE PERSISTENCE =======
    public static void saveProfilesToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(PROFILE_FILE))) {
            out.writeObject(savedProfiles);
        } catch (IOException e) {
            System.err.println("❌ Error saving profiles: " + e.getMessage());
        }
    }

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
}
