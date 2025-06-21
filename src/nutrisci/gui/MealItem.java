package nutrisci.gui;

public class MealItem {
    private String ingredient;
    private double quantity;
    private NutrientProfile nutrients;

    public MealItem(String igd, double qty, NutrientProfile nts) {
        this.ingredient = igd;
        this.quantity = qty;
        this.nutrients = nts;
    }

    public double getCalories() {
        return nutrients.getCalories();
    }

    public NutrientProfile getNutrients() {
        return nutrients;
    }

    public void setNutrients(NutrientProfile nutrients) {
        this.nutrients = nutrients;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}
