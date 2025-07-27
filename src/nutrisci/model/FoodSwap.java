package nutrisci.model;

public class FoodSwap {
    private final String mealId;
    private final String original;
    private final String replacement;
    private final String rationale;

    public FoodSwap(String mealId, String original, String replacement, String rationale) {
        this.mealId = mealId;
        this.original = original;
        this.replacement = replacement;
        this.rationale = rationale;
    }

    public String getMealId() { return mealId; }
    public String getOriginal() { return original; }
    public String getReplacement() { return replacement; }
    public String getRationale() { return rationale; }
}