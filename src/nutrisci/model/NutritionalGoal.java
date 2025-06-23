package nutrisci.model;

import java.io.Serializable;

public class NutritionalGoal implements Serializable {
    private String nutrientName;
    private double targetChange;
    private boolean isIncrease;

    public NutritionalGoal(String nutrientName, double targetChange, boolean isIncrease) {
        this.nutrientName = nutrientName;
        this.targetChange = targetChange;
        this.isIncrease = isIncrease;
    }

    public String getNutrientName() { return nutrientName; }
    public double getTargetChange() { return targetChange; }
    public boolean isIncrease() { return isIncrease; }

    public String getDescription() {
        return (isIncrease ? "Increase " : "Decrease ") + nutrientName + " by " + targetChange;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public void setTargetChange(double targetChange) {
        this.targetChange = targetChange;
    }

    public boolean isIsIncrease() {
        return isIncrease;
    }

    public void setIsIncrease(boolean isIncrease) {
        this.isIncrease = isIncrease;
    }
}
