package nutrisci.model;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private String name, dob, gender;
    private double height, weight;
    private String unit;

    public UserProfile(String name, String dob, String gender, double height, double weight, String unit) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.unit = unit;
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

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getUnit() {
        return unit;
    }

    public void update(String gender, double height, double weight, String unit) {
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
