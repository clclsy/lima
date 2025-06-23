package nutrisci;

public class NutritionalGoal {
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
        return (isIncrease ? "Increase" : "Reduce") + " " + nutrientName + " by " + targetChange;
    }
}
