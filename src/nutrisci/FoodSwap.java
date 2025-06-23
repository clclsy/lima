package nutrisci;

public class FoodSwap {
    private String original;
    private String replacement;
    private String reason;

    public FoodSwap(String original, String replacement, String reason) {
        this.original = original;
        this.replacement = replacement;
        this.reason = reason;
    }

    public String getOriginal() { return original; }
    public String getReplacement() { return replacement; }
    public String getReason() { return reason; }
}
