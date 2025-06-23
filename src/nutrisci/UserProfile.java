package nutrisci;

import java.time.LocalDate;
import java.time.Period;

public class UserProfile {
    private String userId;
    private String name;
    private String sex;
    private LocalDate dateOfBirth;
    private double height, weight;
    private String unitPreference;

    public UserProfile(String userId, String name, String sex, LocalDate dob, double height, double weight,
            String unit) {
        this.userId = userId;
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dob;
        this.height = height;
        this.weight = weight;
        this.unitPreference = unit;
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void updateProfile(String name, String sex, LocalDate dob, double height, double weight, String unit) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dob;
        this.height = height;
        this.weight = weight;
        this.unitPreference = unit;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getUnitPreference() {
        return unitPreference;
    }
}
