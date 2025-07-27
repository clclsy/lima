package nutrisci.model;

public class NutritionalGoal {
    private final String nutrientName;
    private final double amount;
    private final boolean increase;
    private final String unit; // e.g., "g" or "%"

    public NutritionalGoal(String nutrientName, double amount, boolean increase, String unit) {
        this.nutrientName = nutrientName;
        this.amount = amount;
        this.increase = increase;
        this.unit = unit;
    }

    public String getNutrientName() {
        return nutrientName;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isIncrease() {
        return increase;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        String direction = increase ? "increase" : "decrease";
        return String.format("%s %s by at least %.1f%s", direction, nutrientName.toLowerCase(), amount, unit);
    }
}