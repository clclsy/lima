package nutrisci.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private int id;
    private String name;
    private LocalDate dob;
    private String gender;
    private double height;
    private double weight;
    private String unit;
    private List<Meal> meals;

    public UserProfile(String name, LocalDate dob, String gender, double height, double weight, String unit) {
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

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getUnit() {
        return unit;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void addMeal(Meal meal) {
        meals.add(meal);
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
