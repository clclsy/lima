package nutrisci.util;

import java.util.*;
import nutrisci.db.NutritionDataDAO;

public class NutrientComparator {
    public static boolean isWithinVariance(
            Map<String, Double> originalNutrients,
            int candidateFoodId,
            String[] nutrientsToCheck,
            double maxVariance) {
        if (originalNutrients == null || originalNutrients.isEmpty()) {
            return true;
        }

        Map<String, Double> candidateNutrients = NutritionDataDAO.getInstance().getFoodNutrients(candidateFoodId);

        for (String nutrient : nutrientsToCheck) {
            Double originalVal = originalNutrients.get(nutrient);
            Double candidateVal = candidateNutrients.get(nutrient);

            if (originalVal != null && candidateVal != null && originalVal > 0) {
                double variance = Math.abs(candidateVal - originalVal) / originalVal;
                if (variance > maxVariance) {
                    System.out.println("Rejecting due to high variance in " + nutrient +
                            ": " + (variance * 100) + "%");
                    return false;
                }
            }
        }
        return true;
    }
}