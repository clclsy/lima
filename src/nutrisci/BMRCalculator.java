package nutrisci;

public class BMRCalculator {

    public static double calculateBMR(int age, double heightCm, double weightKg, boolean isMale) {
        if (isMale) {
            return 88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age);
        } else {
            return 447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age);
        }
    }

    public static void main(String[] args) {
        int age = 21;
        double heightCm = 178;
        double weightKg = 70;
        boolean isMale = true;

        double bmr = calculateBMR(age, heightCm, weightKg, isMale);
        System.out.printf("Estimated BMR: %.2f calories/day\n", bmr);
    }
}
